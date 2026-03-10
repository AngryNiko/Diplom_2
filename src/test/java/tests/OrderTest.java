package tests;

import client.OrderClient;
import client.UserClient;
import io.qameta.allure.*;
import models.Order;
import models.User;
import org.junit.After;
import org.junit.Test;
import utils.UserGenerator;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;

@Epic("Stellar Burgers API")
@Feature("Создание заказа")
public class OrderTest extends BaseTest {

    OrderClient orderClient = new OrderClient();
    UserClient userClient = new UserClient();

    String accessToken;

    @Test
    @Story("Создание заказа с авторизацией")
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
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    @Story("Создание заказа без авторизации")
    public void createOrderWithoutAuthTest() {

        Order order = new Order(List.of("61c0c5a71d1f82001bdaaa6d"));

        orderClient.createOrder(order)
                .then()
                .statusCode(200);
    }

    @Test
    @Story("Создание заказа без ингредиентов")
    public void createOrderWithoutIngredientsTest() {

        Order order = new Order(null);

        orderClient.createOrder(order)
                .then()
                .statusCode(400);
    }

    @Test
    @Story("Создание заказа с неверным хешем")
    public void createOrderWithWrongHashTest() {

        Order order = new Order(List.of("invalid_hash"));

        orderClient.createOrder(order)
                .then()
                .statusCode(400);
    }

    @Test
    @Story("Создание заказа с ингредиентами")
    public void createOrderWithIngredientsTest() {

        Order order = new Order(List.of(
                "61c0c5a71d1f82001bdaaa6d",
                "61c0c5a71d1f82001bdaaa6f"));

        orderClient.createOrder(order)
                .then()
                .statusCode(200);
    }

    @After
    public void deleteUser() {

        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }
}