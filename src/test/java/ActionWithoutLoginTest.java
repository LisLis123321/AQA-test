import io.restassured.RestAssured;
import io.qameta.allure.Description;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.Matchers.equalTo;

public class ActionWithoutLoginTest extends BaseTest{


    @BeforeEach
    void configureStubs() {
        wireMockServer.stubFor(
                post(urlEqualTo("/doAction"))
                        .willReturn(aResponse().withStatus(200))
        );
    }

    @Test
    @DisplayName("ACTION без предварительного логин запрещён")
    @Description("Проверяем, что приложение отклоняет запрос ACTION для токена, который не проходил авторизацию. " +
            "Ожидаем результат ERROR и отсутствие обращений к сервису /doAction.")

    void actionWithoutLoginShouldFail() {

        String token = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .toUpperCase();

        RestAssured.given()
                .baseUri("http://localhost:8080")
                .header("X-Api-Key", "qazWSXedc")
                .header("Accept", "application/json")
                .contentType("application/x-www-form-urlencoded")
                .formParam("token", token)
                .formParam("action", "ACTION")
                .when()
                .post("/endpoint")
                .then()
                .body("result", equalTo("ERROR"));

        wireMockServer.verify(
                0,
                postRequestedFor(urlEqualTo("/doAction"))
        );
    }
}
