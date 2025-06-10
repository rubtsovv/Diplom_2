package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.AllureJunit4;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.util.List;

import static org.hamcrest.Matchers.notNullValue;

public class OrderCreationTests extends BaseTest {

    private List<String> ingredientIds;

    @Before
    public void setUp() {
        registerAndLoginUser();
        loadIngredients();
    }

    @Step("Загрузить список ингредиентов")
    private void loadIngredients() {
        Response response = orderClient.getIngredients();
        checks.checkStatusCode(response, 200);
        ingredientIds = response.path("data._id");
    }

    @Test
    @Description("Создание заказа с авторизацией и валидными ингредиентами")
    public void createOrder_WithAuth_ValidIngredients_ShouldReturn200() {
        Response response = orderClient.createOrder(accessToken, List.of(ingredientIds.get(0), ingredientIds.get(1)));
        checks.checkStatusCode(response, 200);
        checks.checkLabelSuccess(response, true);
        response.then().body("order.number", notNullValue());
    }

    @Test
    @Description("Создание заказа с авторизацией, но без ингредиентов")
    public void createOrder_WithAuth_NoIngredients_ShouldReturn400() {
        Response response = orderClient.createOrder(accessToken, List.of());
        checks.checkStatusCode(response, 400);
        checks.checkLabelSuccess(response, false);
        checks.checkLabelMessage(response, "Ingredient ids must be provided");
    }

    @Test
    @Description("Создание заказа с авторизацией и невалидным ингредиентом")
    public void createOrder_WithAuth_InvalidIngredient_ShouldReturn500() {
        Response response = orderClient.createOrder(accessToken, List.of("invalid_id"));
        checks.checkStatusCode(response, 500);
    }

    @Test
    @Description("Создание заказа без авторизации")
    public void createOrder_WithoutAuth_ShouldReturn401() {
        Response response = orderClient.createOrder("", List.of(ingredientIds.get(0)));
        checks.checkStatusCode(response, 401);
        checks.checkLabelSuccess(response, false);
        checks.checkLabelMessage(response, "You should be authorised");
    }
}
