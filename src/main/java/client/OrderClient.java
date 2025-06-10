package client;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class OrderClient {

    private static final String BASE_URI = "https://stellarburgers.nomoreparties.site";
    private static final String PATH_ORDERS = "/api/orders";
    private static final String PATH_INGREDIENTS = "/api/ingredients";

    public OrderClient() {
        RestAssured.baseURI = BASE_URI;
    }

    @Step("Получить список ингредиентов")
    public Response getIngredients() {
        return given()
                .contentType("application/json")
                .when()
                .get(PATH_INGREDIENTS);
    }

    @Step("Создать заказ: авторизация = {accessToken != null}, ингредиенты = {ingredientIds}")
    public Response createOrder(String accessToken, List<String> ingredientIds) {
        Map<String, Object> body = new HashMap<>();
        body.put("ingredients", ingredientIds);

        var request = given()
                .contentType("application/json")
                .body(body);

        if (accessToken != null && !accessToken.isBlank()) {
            request.header("Authorization", "Bearer " + accessToken);
        }

        return request.when().post(PATH_ORDERS);
    }

    @Step("Получить заказы пользователя")
    public Response getUserOrders(String accessToken) {
        return given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get(PATH_ORDERS);
    }
}
