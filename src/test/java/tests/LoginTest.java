package tests;

import client.UserClient;
import io.qameta.allure.*;
import io.qameta.allure.junit4.DisplayName;
import models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.UserGenerator;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

@Epic("Stellar Burgers API")
@Feature("Логин пользователя")
public class LoginTest extends BaseTest {

    UserClient userClient = new UserClient();

    User user;
    String accessToken;

    @Before
    public void createUser() {

        user = UserGenerator.createRandomUser();
        userClient.createUser(user);

        accessToken = userClient.loginUser(user)
                .then()
                .extract()
                .path("accessToken");
    }

    @Test
    @DisplayName("Логин существующего пользователя")
    @Description("Проверяем успешный вход под существующим пользователем")
    public void loginExistingUserTest() {

        userClient.loginUser(user)
                .then()
                .statusCode(SC_OK);
    }

    @Test
    @DisplayName("Логин с неверным email")
    @Description("Проверяем ошибку авторизации при неверном email")
    public void loginWithWrongEmailTest() {

        User wrongUser = new User("wrong_" + user.email, user.password, user.name);

        userClient.loginUser(wrongUser)
                .then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Логин с неверным паролем")
    @Description("Проверяем ошибку авторизации при неверном пароле")
    public void loginWithWrongPasswordTest() {

        User wrongUser = new User(user.email, "wrong_password", user.name);

        userClient.loginUser(wrongUser)
                .then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @After
    public void deleteUser() {

        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }
}