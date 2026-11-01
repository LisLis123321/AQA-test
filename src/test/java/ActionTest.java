import io.restassured.RestAssured;
import io.qameta.allure.Description;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.Matchers.equalTo;

public class ActionTest extends BaseTest{


    private final String token =
            UUID.randomUUID().toString()
                    .replace("-", "")
                    .toUpperCase();


    @BeforeEach
    void configureStubs() {
        wireMockServer.stubFor(
                post(urlEqualTo("/auth"))
                        .willReturn(aResponse().withStatus(200))
        );

        wireMockServer.stubFor(
                post(urlEqualTo("/doAction"))
                        .willReturn(aResponse().withStatus(200))
        );
    }


    private void sendRequest(String action) {
        RestAssured.given()
                .baseUri("http://localhost:8080")
                .header("X-Api-Key", "qazWSXedc")
                .header("Accept", "application/json")
                .contentType("application/x-www-form-urlencoded")
                .formParam("token", token)
                .formParam("action", action)
                .when()
                .post("/endpoint")
                .then()
                .body("result", equalTo("OK"));
    }

    @Test
    @DisplayName("Действие успешно выполняется после авторизации")
    @Description("Проверяем, что после успешного логина приложение разрешает выполнить ACTION. " +
            "Ожидаем успешный результат и по одному обращению к сервисам /auth и /doAction.")
    void actionAfterLogin() {

        sendRequest("LOGIN");
        sendRequest("ACTION");

        wireMockServer.verify(
                1,
                postRequestedFor(urlEqualTo("/auth"))
        );

        wireMockServer.verify(
                1,
                postRequestedFor(urlEqualTo("/doAction"))
        );
    }
}

