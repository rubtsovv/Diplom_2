package client;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.User;

import static io.restassured.RestAssured.given;

public class UserClient {

    private static final String BASE_URI = "https://stellarburgers.nomoreparties.site";
    private static final String PATH_REGISTER = "/api/auth/register";
    private static final String PATH_LOGIN = "/api/auth/login";
    private static final String PATH_LOGOUT = "/api/auth/logout";
    private static final String PATH_USER = "/api/auth/user";

    public UserClient() {
        RestAssured.baseURI = BASE_URI;
    }

    @Step("Регистрация пользователя")
    public Response register(User user) {
        return given()
                .contentType("application/json")
                .body(user)
                .when()
                .post(PATH_REGISTER);
    }

    @Step("Логин пользователя")
    public Response login(User user) {
        return given()
                .contentType("application/json")
                .body(user)
                .when()
                .post(PATH_LOGIN);
    }

    @Step("Выход из системы")
    public Response logout(String refreshToken) {
        return given()
                .contentType("application/json")
                .body("{\"token\":\"" + refreshToken + "\"}")
                .when()
                .post(PATH_LOGOUT);
    }

    @Step("Получение информации о пользователе")
    public Response getUser(String accessToken) {
        return given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get(PATH_USER);
    }

    @Step("Обновление информации о пользователе")
    public Response updateUser(String accessToken, User user) {
        return given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType("application/json")
                .body(user)
                .when()
                .patch(PATH_USER);
    }

    @Step("Удаление пользователя")
    public Response deleteUser(String accessToken) {
        return given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .delete(PATH_USER);
    }
}
