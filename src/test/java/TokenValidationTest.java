import io.qameta.allure.Description;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.Matchers.equalTo;

public class TokenValidationTest extends BaseTest{



    @BeforeEach
    void configureAuthStub() {
        wireMockServer.stubFor(
                post(urlEqualTo("/auth"))
                        .willReturn(aResponse().withStatus(200))
        );
    }


    @Test
    @DisplayName("Логин с токеном длиной 31 символ отклоняется")
    @Description("Проверяем валидацию длины токена. При попытке авторизации с токеном из 31 символа приложение " +
            "должно вернуть ERROR и не отправлять запос в сервис /auth.")

    void loginWithShortTokenShouldFail() {

        String invalidToken = "A".repeat(31);

        RestAssured.given()
                .baseUri("http://localhost:8080")
                .header("X-Api-Key", "qazWSXedc")
                .header("Accept", "application/json")
                .contentType("application/x-www-form-urlencoded")
                .formParam("token", invalidToken)
                .formParam("action", "LOGIN")
                .when()
                .post("/endpoint")
                .then()
                .body("result", equalTo("ERROR"));

        wireMockServer.verify(
                0,
                postRequestedFor(urlEqualTo("/auth"))
        );
    }
}
