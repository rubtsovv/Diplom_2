package tests;

import client.OrderClient;
import client.UserClient;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import model.User;
import utils.ResponseChecks;
import utils.UserGenerator;

import static org.junit.Assert.fail;

public class BaseTest {

    protected final UserClient userClient = new UserClient();
    protected final OrderClient orderClient = new OrderClient();
    protected final ResponseChecks checks = new ResponseChecks();

    protected User testUser;
    protected String accessToken;

    @Step("Создать и авторизовать пользователя")
    protected void registerAndLoginUser() {
        testUser = UserGenerator.getRandomUser();
        Response regResponse = userClient.register(testUser);
        checks.checkStatusCode(regResponse, 200);

        Response loginResponse = userClient.login(testUser);
        checks.checkStatusCode(loginResponse, 200);

        accessToken = extractAccessToken(loginResponse);
        if (accessToken == null || accessToken.isEmpty()) {
            fail("Токен доступа не получен");
        }
    }

    @Step("Извлечь accessToken из ответа (без Bearer)")
    protected String extractAccessToken(Response response) {
        String rawToken = response.path("accessToken");
        return rawToken != null ? rawToken.replace("Bearer ", "") : null;
    }

    @Step("Удалить пользователя")
    protected void deleteUser() {
        if (accessToken != null && !accessToken.isEmpty()) {
            Response response = userClient.deleteUser(accessToken);
            int code = response.getStatusCode();
            if (code != 200 && code != 202 && code != 401) {
                fail("Удаление пользователя завершилось с неожиданным статусом: " + code);
            }
        }
    }
}