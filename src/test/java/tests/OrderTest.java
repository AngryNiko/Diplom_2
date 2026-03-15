package tests;

import client.OrderClient;
import client.UserClient;
import io.qameta.allure.*;
import io.qameta.allure.junit4.DisplayName;
import models.Order;
import models.User;
import org.junit.After;
import org.junit.Test;
import utils.UserGenerator;

import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

@Epic("Stellar Burgers API")
@Feature("Создание заказа")
public class OrderTest extends BaseTest {

    OrderClient orderClient = new OrderClient();
    UserClient userClient = new UserClient();

    String accessToken;

    @Test
    @DisplayName("Создание заказа с авторизацией")
    @Description("Проверяем создание заказа авторизованным пользователем")
    public void createOrderWithAuthTest() {

        User user = UserGenerator.createRandomUser();

        userClient.createUser(user);

        accessToken = userClient.loginUser(user)
                .then()
                .extract()
                .path("accessToken");

        Order order = new Order(List.of("61c0c5a71d1f82001bdaaa6d"));

        orderClient.createOrderWithAuth(order, accessToken)
                .then()
                .statusCode(SC_OK)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description("Проверяем создание заказа без авторизации")
    public void createOrderWithoutAuthTest() {

        Order order = new Order(List.of("61c0c5a71d1f82001bdaaa6d"));

        orderClient.createOrder(order);

        User user = UserGenerator.createRandomUser();
        UserClient userClient = new UserClient();

        userClient.createUser(user);

        String accessToken = userClient.loginUser(user)
                .then()
                .extract()
                .path("accessToken");

        orderClient.createOrderWithAuth(order, accessToken)
                .then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    @Description("Проверяем ошибку создания заказа без ингредиентов")
    public void createOrderWithoutIngredientsTest() {

        Order order = new Order(null);

        orderClient.createOrder(order)
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем")
    @Description("Проверяем ошибку при передаче неверного хеша ингредиента")
    public void createOrderWithWrongHashTest() {

        Order order = new Order(List.of("invalidhash"));

        orderClient.createOrder(order)
                .then()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Создание заказа с несколькими ингредиентами")
    @Description("Проверяем создание заказа с несколькими ингредиентами")
    public void createOrderWithIngredientsTest() {

        Order order = new Order(List.of(
                "61c0c5a71d1f82001bdaaa6d",
                "61c0c5a71d1f82001bdaaa6f"));

        orderClient.createOrder(order)
                .then()
                .statusCode(SC_OK);
    }

    @After
    public void deleteUser() {

        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }
}