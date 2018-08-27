package io.github.artsok.tasks;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.qameta.allure.*;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static io.restassured.RestAssured.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;

public class GetClientTest {

    private static final String WIREMOCK_PATH = "/api/getClientTypes";
    private static final int STATUS_CODE = 200;
    private static final String TYPE_OF_OWNERSHIP_PARAM = "typeOfOwnership";

    @ClassRule
    public static final WireMockRule WIRE_MOCK_RULE = new WireMockRule(
            wireMockConfig()
                    .dynamicPort()
                    .dynamicHttpsPort()
    );

    @BeforeClass
    public static void setUp() {
        baseURI = "http://localhost";
        port = WIRE_MOCK_RULE.port();
        basePath = "api";
        requestSpecification = new RequestSpecBuilder()
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();
    }



    @Epic(value = "GetClientTypes")
    @Feature(value = "Обязательные параметры сервиса")
    @Story(value = "Сложение")
    @Severity(value = SeverityLevel.BLOCKER)
    @DisplayName("Возврат данных физических лиц (Без фильтра)")
    @Test
    public void shouldReturnIndividualDataWithNoFilter() {
        WIRE_MOCK_RULE.stubFor(get(urlPathEqualTo(WIREMOCK_PATH))
                .withQueryParam(TYPE_OF_OWNERSHIP_PARAM, equalTo("1"))
                .willReturn(aResponse()
                        .withBodyFile("individual-data-with-no-filter-response.json")
                        .withStatus(STATUS_CODE)));

        String body = given().filter(new AllureRestAssured())
                .param(TYPE_OF_OWNERSHIP_PARAM, "1")
                .when().get("getClientTypes")
                .then().statusCode(STATUS_CODE).log().all().extract().asString();

        assertThat(body, not(isEmptyString()));
    }
}
