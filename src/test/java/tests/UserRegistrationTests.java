package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import model.User;
import org.junit.*;
import utils.UserGenerator;

@Epic("API: Пользователи")
@Feature("Регистрация")
public class UserRegistrationTests extends BaseTest {

    @After
    public void tearDown() {
        deleteUser();
    }

    @Test
    @Story("Успешная регистрация")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Регистрация нового уникального пользователя. Ожидается 200 и данные пользователя.")
    public void createUniqueUserSuccess() {
        User user = UserGenerator.getRandomUser();
        Response response = createUser(user);
        assertUserCreated(response, user);
        accessToken = extractAccessToken(response); // сохраняем для deleteUser
    }

    @Test
    @Story("Повторная регистрация")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Попытка зарегистрировать пользователя повторно. Ожидается 403 с сообщением.")
    public void createAlreadyRegisteredUserFail() {
        User user = UserGenerator.getRandomUser();
        createUser(user); // первая регистрация
        Response response = createUser(user); // вторая попытка
        assertUserCreationFailed(response, 403, "User already exists");
    }

    @Test
    @Story("Ошибки валидации")
    @Severity(SeverityLevel.NORMAL)
    @Description("Создание пользователя без email. Ожидается 403 с сообщением об обязательных полях.")
    public void createUserWithMissingFieldsFail() {
        User user = UserGenerator.getRandomUser();
        user.setEmail(null); // пропускаем email
        Response response = createUser(user);
        assertUserCreationFailed(response, 403, "Email, password and name are required fields");
    }

    @Step("Создать пользователя")
    private Response createUser(User user) {
        return userClient.register(user);
    }

    @Step("Проверить успешное создание пользователя")
    private void assertUserCreated(Response response, User user) {
        checks.checkStatusCode(response, 200);
        checks.checkLabelSuccess(response, true);
        checks.checkUser(response, user.getEmail(), user.getName());
    }

    @Step("Проверить ошибку регистрации")
    private void assertUserCreationFailed(Response response, int statusCode, String message) {
        checks.checkStatusCode(response, statusCode);
        checks.checkLabelSuccess(response, false);
        checks.checkLabelMessage(response, message);
    }
}

