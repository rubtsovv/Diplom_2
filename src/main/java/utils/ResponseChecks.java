package utils;

import io.restassured.response.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ResponseChecks {

    public void checkStatusCode(Response response, int expectedCode) {
        assertThat("Статус код не совпадает", response.getStatusCode(), equalTo(expectedCode));
    }

    public void checkLabelSuccess(Response response, boolean expected) {
        assertThat("Поле success не совпадает", response.path("success"), equalTo(expected));
    }

    public void checkLabelMessage(Response response, String expectedMessage) {
        assertThat("Сообщение message не совпадает", response.path("message"), equalTo(expectedMessage));
    }

    public void checkUser(Response response, String expectedEmail, String expectedName) {
        assertThat("Email не совпадает", response.path("user.email"), equalTo(expectedEmail));
        assertThat("Имя не совпадает", response.path("user.name"), equalTo(expectedName));
    }
}
