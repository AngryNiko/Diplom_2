package tests;

import config.BaseUrl;
import io.restassured.RestAssured;
import org.junit.Before;

public class BaseTest {

    @Before
    public void setup() {
        RestAssured.baseURI = BaseUrl.BASE_URL;
    }
}