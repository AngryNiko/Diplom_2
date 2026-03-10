package tests;

import client.UserClient;
import io.qameta.allure.*;
import models.User;
import org.junit.After;
import org.junit.Test;
import utils.UserGenerator;

import static org.hamcrest.Matchers.equalTo;

public class LoginTest extends BaseTest {

    UserClient userClient = new UserClient();

    String accessToken;
    User user;

    @Test
    @Story("Логин существующего пользователя")
    @Description("Проверяем успешный вход под существующим пользователем")
    public void loginExistingUserTest() {

        user = UserGenerator.createRandomUser();

        userClient.createUser(user);

        accessToken = userClient.loginUser(user)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .extract()
                .path("accessToken");
    }

    @Test
    @Story("Логин с неверным email")
    @Description("Проверяем что система возвращает ошибку при неверном логине")
    public void loginWithWrongEmailTest() {

        user = UserGenerator.createRandomUser();
        userClient.createUser(user);

        User wrongUser = new User(
                "wrong_" + user.email,
                user.password,
                user.name
        );

        userClient.loginUser(wrongUser)
                .then()
                .statusCode(401);
    }

    @Test
    @Story("Логин с неверным паролем")
    @Description("Проверяем что система возвращает ошибку при неверном пароле")
    public void loginWithWrongPasswordTest() {

        user = UserGenerator.createRandomUser();
        userClient.createUser(user);

        User wrongUser = new User(
                user.email,
                "wrong_password",
                user.name
        );

        userClient.loginUser(wrongUser)
                .then()
                .statusCode(401);
    }

    @After
    public void deleteUser() {

        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }
}