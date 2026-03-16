package utils;

import models.User;

import java.util.UUID;

public class UserGenerator {

    public static User createRandomUser() {

        String email = "user_" + UUID.randomUUID() + "@mail.com";
        String password = "123456";
        String name = "User_" + UUID.randomUUID();

        return new User(email, password, name);
    }
}