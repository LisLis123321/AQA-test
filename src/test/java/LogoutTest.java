import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class LogoutTest extends BaseTest{

    private String token;



    @BeforeEach
    void configureStubs() {

        token = java.util.UUID.randomUUID()
                .toString()
                .replace("-", "")
                .toUpperCase();

        wireMockServer.stubFor(
                post(urlEqualTo("/auth"))
                        .willReturn(aResponse().withStatus(200))
        );

        wireMockServer.stubFor(
                post(urlEqualTo("/doAction"))
                        .willReturn(aResponse().withStatus(200))
        );
    }



    private String sendRequest(String action) {
        return RestAssured.given()
                .baseUri("http://localhost:8080")
                .header("X-Api-Key", "qazWSXedc")
                .header("Accept", "application/json")
                .contentType("application/x-www-form-urlencoded")
                .formParam("token", token)
                .formParam("action", action)
                .when()
                .post("/endpoint")
                .then()
                .extract()
                .path("result");
    }

    @Test
    @DisplayName("После LOGOUT токен больше не авторизован")
    @Description("Проверяем полседовательность LOGIN - LOGOUT - ACTION. После выхода токен должен стать " +
            "недействительным, повторное действие должно завершиться ошибкой, а запрос к /doAction не должен отправляться.")

    void logoutShouldInvalidateToken() {

        String loginResult = sendRequest("LOGIN");
        org.junit.jupiter.api.Assertions.assertEquals(
                "OK", loginResult
        );

        String logoutResult = sendRequest("LOGOUT");
        org.junit.jupiter.api.Assertions.assertEquals(
                "OK", logoutResult
        );

        String actionResult = sendRequest("ACTION");
        org.junit.jupiter.api.Assertions.assertEquals(
                "ERROR", actionResult
        );

        wireMockServer.verify(
                1,
                postRequestedFor(urlEqualTo("/auth"))
        );

        wireMockServer.verify(
                0,
                postRequestedFor(urlEqualTo("/doAction"))
        );
    }
}
