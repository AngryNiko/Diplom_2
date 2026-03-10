package tests;

import client.UserClient;
import io.qameta.allure.*;
import models.User;
import org.junit.After;
import org.junit.Test;
import utils.UserGenerator;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@Epic("Stellar Burgers API")
@Feature("Создание пользователя")
public class UserCreateTest extends BaseTest {

    UserClient userClient = new UserClient();
    String accessToken;
    User user;

    @Test
    @Story("Создание уникального пользователя")
    @Description("Проверяем успешное создание пользователя")
    public void createUniqueUserTest() {

        user = UserGenerator.createRandomUser();

        accessToken = userClient.createUser(user)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .extract()
                .path("accessToken");
    }

    @Test
    @Story("Создание существующего пользователя")
    @Description("Проверяем, что повторная регистрация невозможна")
    public void createExistingUserTest() {

        user = UserGenerator.createRandomUser();

        userClient.createUser(user);

        userClient.createUser(user)
                .then()
                .statusCode(403)
                .body("success", equalTo(false));
    }

    @Test
    @Story("Создание пользователя без имени")
    public void createUserWithoutNameTest() {

        String body = "{\"email\":\"test@mail.com\",\"password\":\"123456\"}";

        given()
                .header("Content-type", "application/json")
                .body(body)
                .post("/api/auth/register")
                .then()
                .statusCode(403);
    }

    @Test
    @Story("Создание пользователя без email")
    public void createUserWithoutEmailTest() {

        String body = "{\"password\":\"123456\",\"name\":\"Test\"}";

        given()
                .header("Content-type", "application/json")
                .body(body)
                .post("/api/auth/register")
                .then()
                .statusCode(403);
    }

    @Test
    @Story("Создание пользователя без пароля")
    public void createUserWithoutPasswordTest() {

        String body = "{\"email\":\"test@mail.com\",\"name\":\"Test\"}";

        given()
                .header("Content-type", "application/json")
                .body(body)
                .post("/api/auth/register")
                .then()
                .statusCode(403);
    }

    @After
    public void deleteUser() {

        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }
}