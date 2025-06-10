package tests;

import io.qameta.allure.*;
import io.qameta.allure.junit4.AllureJunit4;
import model.User;
import org.junit.*;
import org.junit.rules.TestRule;
import io.restassured.response.Response;

import static org.junit.Assert.*;

@Epic("API: Пользователи")
@Feature("Обновление данных пользователя")
public class UserUpdateTests extends BaseTest {

    @Before
    public void setUp() {
        registerAndLoginUser();
    }

    @After
    public void tearDown() {
        deleteUser();
    }

    @Test
    @Story("Обновление с авторизацией")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Обновление имени пользователя с валидным токеном. Ожидается 200 OK и новое имя.")
    public void updateUserWithAuthTest() {
        User updateUser = new User();
        updateUser.setName("UpdatedName");
        updateUser.setEmail(testUser.getEmail()); // оставить email без изменений

        Response response = updateUserWithToken(accessToken, updateUser);

        assertEquals(200, response.getStatusCode());
        assertTrue(response.path("success"));
        assertEquals("UpdatedName", response.path("user.name"));
    }

    @Test
    @Story("Обновление без авторизации")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Попытка обновить имя без accessToken. Ожидается 401 Unauthorized.")
    public void updateUserWithoutAuthTest() {
        User updateUser = new User();
        updateUser.setName("UpdatedName");

        Response response = updateUserWithToken("", updateUser);

        assertEquals(401, response.getStatusCode());
        assertEquals("You should be authorised", response.path("message"));
    }

    @Step("Обновить пользователя (токен: {token}, имя: {user.name})")
    private Response updateUserWithToken(String token, User user) {
        return userClient.updateUser(token, user);
    }
}


