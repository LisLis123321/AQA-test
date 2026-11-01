import io.restassured.RestAssured;
import io.qameta.allure.Description;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.Matchers.equalTo;

public class LoginTest extends BaseTest{

    @BeforeEach
    void configureAuthStub() {
        wireMockServer.stubFor(
                post(urlEqualTo("/auth"))
                        .willReturn(
                                aResponse().withStatus(200)
                        )
        );
    }


        @Test
        @DisplayName("Успешная авторизация с корректным токеном")
        @Description("Проверяем, что приложение разрешает логин с корректным токеном и обращается к сервису авторизации.")
        void successfulLogin() {

            String token = java.util.UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .toUpperCase();

            io.restassured.response.Response response =
                    RestAssured.given()
                            .baseUri("http://localhost:8080")
                            .header("X-Api-Key", "qazWSXedc")
                            .header("Accept", "application/json")
                            .contentType("application/x-www-form-urlencoded")
                            .formParam("token", token)
                            .formParam("action", "LOGIN")
                            .when()
                            .post("/endpoint");

            System.out.println("HTTP status: " + response.statusCode());
            System.out.println("Response body: " + response.asString());

            org.junit.jupiter.api.Assertions.assertEquals(
                    "OK",
                    response.jsonPath().getString("result")
            );

            wireMockServer.verify(
                    1,
                    postRequestedFor(urlEqualTo("/auth"))
            );
        }

    }
