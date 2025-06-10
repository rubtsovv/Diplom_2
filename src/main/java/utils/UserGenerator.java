package utils;

import model.User;

import java.util.UUID;

public class UserGenerator {
    public static User getRandomUser() {
        String unique = UUID.randomUUID().toString().replace("-", "");
        return new User("user" + unique + "@mail.com", "pass" + unique, "name" + unique);
    }
}
