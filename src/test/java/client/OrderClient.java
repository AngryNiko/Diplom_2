package client;

import config.Endpoints;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.Response;
import models.Order;

import static io.restassured.RestAssured.given;

public class OrderClient {

    @Step("Создание заказа без авторизации")
    public Response createOrder(Order order) {

        return given()
                .filter(new AllureRestAssured())
                .header("Content-type", "application/json")
                .body(order)
                .post(Endpoints.CREATE_ORDER);
    }

    @Step("Создание заказа с авторизацией")
    public Response createOrderWithAuth(Order order, String token) {

        return given()
                .filter(new AllureRestAssured())
                .header("Content-type", "application/json")
                .header("Authorization", token)
                .body(order)
                .post(Endpoints.CREATE_ORDER);
    }
}