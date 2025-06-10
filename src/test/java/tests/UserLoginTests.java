package tests;

import io.qameta.allure.*;
import io.qameta.allure.junit4.AllureJunit4;
import io.restassured.response.Response;
import model.User;
import org.junit.*;
import org.junit.rules.TestRule;
import utils.UserGenerator;

import static org.junit.Assert.fail;

@Epic("API: Авторизация")
@Feature("Логин пользователя")
public class UserLoginTests extends BaseTest {

    @Test
    @Story("Успешный логин")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Успешный вход зарегистрированного пользователя. Ожидается 200 и получение access/refresh токенов.")
    public void loginWithValidUserSuccess() {
        User user = UserGenerator.getRandomUser();
        registerUser(user);

        Response loginResponse = loginUser(user);
        assertLoginSuccess(loginResponse);

        accessToken = extractAccessToken(loginResponse); // сохраняем accessToken для deleteUser
    }

    @Test
    @Story("Ошибка при логине")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Логин с несуществующим пользователем. Ожидается 401 Unauthorized.")
    public void loginWithInvalidUserFail() {
        User wrongUser = new User("wrong@mail.com", "badpass", "badname");
        Response response = loginUser(wrongUser);
        assertLoginFailure(response);
    }

    @After
    public void tearDown() {
        deleteUser(); // удаляем пользователя только если есть токен
    }

    @Step("Зарегистрировать нового пользователя")
    private void registerUser(User user) {
        Response response = userClient.register(user);
        checks.checkStatusCode(response, 200);
    }

    @Step("Логин пользователя")
    private Response loginUser(User user) {
        return userClient.login(user);
    }

    @Step("Проверить успешный логин")
    private void assertLoginSuccess(Response response) {
        checks.checkStatusCode(response, 200);
        checks.checkLabelSuccess(response, true);

        String accessToken = response.path("accessToken");
        String refreshToken = response.path("refreshToken");

        if (accessToken == null || refreshToken == null) {
            fail("Токены не получены");
        }
    }

    @Step("Проверить неуспешный логин")
    private void assertLoginFailure(Response response) {
        checks.checkStatusCode(response, 401);
        checks.checkLabelSuccess(response, false);
        checks.checkLabelMessage(response, "email or password are incorrect");
    }
}

