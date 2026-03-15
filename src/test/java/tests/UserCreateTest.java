package tests;

import client.UserClient;
import io.qameta.allure.*;
import io.qameta.allure.junit4.DisplayName;
import models.User;
import org.junit.After;
import org.junit.Test;
import utils.UserGenerator;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

@Epic("Stellar Burgers API")
@Feature("Создание пользователя")
public class UserCreateTest extends BaseTest {

    UserClient userClient = new UserClient();

    String accessToken;
    User user;

    @Test
    @DisplayName("Создание уникального пользователя")
    @Description("Проверяем успешное создание пользователя")
    public void createUniqueUserTest() {

        user = UserGenerator.createRandomUser();

        accessToken = userClient.createUser(user)
                .then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .extract()
                .path("accessToken");
    }

    @Test
    @DisplayName("Создание существующего пользователя")
    @Description("Проверяем что повторная регистрация невозможна")
    public void createExistingUserTest() {

        user = UserGenerator.createRandomUser();

        userClient.createUser(user);

        userClient.createUser(user)
                .then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без имени")
    @Description("Проверяем ошибку при отсутствии имени")
    public void createUserWithoutNameTest() {

        User user = new User("test@mail.com", "123456", null);

        userClient.createUser(user)
                .then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без email")
    @Description("Проверяем ошибку при отсутствии email")
    public void createUserWithoutEmailTest() {

        User user = new User(null, "123456", "Test");

        userClient.createUser(user)
                .then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без пароля")
    @Description("Проверяем ошибку при отсутствии пароля")
    public void createUserWithoutPasswordTest() {

        User user = new User("test@mail.com", null, "Test");

        userClient.createUser(user)
                .then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @After
    public void deleteUser() {

        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }
}