package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.*;

import java.util.List;

import static org.hamcrest.Matchers.*;

@Epic("API: Заказы")
@Feature("Получение заказов")
public class OrderRetrievalTests extends BaseTest {

    private List<String> ingredientIds;

    @Before
    public void setUp() {
        registerAndLoginUser();
        loadIngredients();
        createTestOrder();
    }

    @After
    public void tearDown() {
        deleteUser();
    }

    @Step("Загрузить список ингредиентов")
    private void loadIngredients() {
        Response response = orderClient.getIngredients();
        checks.checkStatusCode(response, 200);
        ingredientIds = response.path("data._id");
    }

    @Step("Создать тестовый заказ")
    private void createTestOrder() {
        Response response = orderClient.createOrder(accessToken, List.of(ingredientIds.get(0), ingredientIds.get(1)));
        checks.checkStatusCode(response, 200);
    }

    @Step("Получить заказы с токеном: {token}")
    private Response getUserOrders(String token) {
        return orderClient.getUserOrders(token);
    }

    @Test
    @Story("Получение заказов авторизованного пользователя")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Ожидается успешный ответ и наличие заказов в списке")
    public void getUserOrdersWithAuth_ShouldReturnOrders() {
        Response response = getUserOrders(accessToken);
        checks.checkStatusCode(response, 200);
        response.then()
                .body("success", equalTo(true))
                .body("orders", notNullValue())
                .body("orders.size()", greaterThan(0));
    }

    @Test
    @Story("Получение заказов без авторизации")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Ожидается ошибка 401 при отсутствии токена авторизации")
    public void getUserOrdersWithoutAuth_ShouldReturn401() {
        Response response = getUserOrders("");
        checks.checkStatusCode(response, 401);
        response.then()
                .body("success", equalTo(false))
                .body("message", containsString("You should be authorised"));
    }
}


