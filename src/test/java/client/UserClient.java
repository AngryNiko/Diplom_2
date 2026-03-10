package client;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.Response;
import models.User;

import static io.restassured.RestAssured.given;

public class UserClient {

    @Step("Создание пользователя")
    public Response createUser(User user) {

        return given()
                .filter(new AllureRestAssured())
                .header("Content-type", "application/json")
                .body(user)
                .post("/api/auth/register");
    }

    @Step("Логин пользователя")
    public Response loginUser(User user) {

        return given()
                .filter(new AllureRestAssured())
                .header("Content-type", "application/json")
                .body(user)
                .post("/api/auth/login");
    }

    @Step("Удаление пользователя")
    public Response deleteUser(String token) {

        return given()
                .filter(new AllureRestAssured())
                .header("Authorization", token)
                .delete("/api/auth/user");
    }
}