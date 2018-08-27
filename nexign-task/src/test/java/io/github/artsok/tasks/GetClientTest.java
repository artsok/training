package io.github.artsok.tasks;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.qameta.allure.*;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static io.restassured.RestAssured.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class GetClientTest {

    private static final int STATUS_CODE = 200;
    private static final String WIREMOCK_PATH = "/api/getClientTypes";
    private static final String RESTASSURED_PATH = "getClientTypes";
    private static final String TYPE_OF_OWNERSHIP_PARAM = "typeOfOwnership";
    private static final String ORDER_PARAM = "orderBy";
    private static final String ORDER_ASC= "asc";
    private static final String INDIVIDUAL_OWNERSHIP = "1";


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
        defaultParser = Parser.JSON;
        requestSpecification = new RequestSpecBuilder().addFilter(new AllureRestAssured())
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
    public void shouldReturnIndividualDataWithoutOrder() {
        WIRE_MOCK_RULE.stubFor(get(urlPathEqualTo(WIREMOCK_PATH))
                .withQueryParam(TYPE_OF_OWNERSHIP_PARAM, equalTo(INDIVIDUAL_OWNERSHIP))
                .willReturn(aResponse()
                        .withBodyFile("individual-data-with-no-filter-response.json")
                        .withStatus(STATUS_CODE)));

        given().param(TYPE_OF_OWNERSHIP_PARAM, INDIVIDUAL_OWNERSHIP)
                .when().get(RESTASSURED_PATH)
                .then()
                .assertThat()
                    .statusCode(STATUS_CODE)
                    .body(not(isEmptyString()))
                    .body("response.status", is(("success")))
                    .body(matchesJsonSchemaInClasspath("schema/individual-jsonschema.json"));
    }

    @Epic(value = "GetClientTypes")
    @Features(value = {@Feature(value = "Обязательные параметры сервиса"),
            @Feature(value = "Не обязательные параметры сервиса")})
    @Story(value = "Сложение")
    @Severity(value = SeverityLevel.BLOCKER)
    @DisplayName("Возврат данных физических лиц (Фильтр: Сортировка по возрастанию)")
    @Test
    public void shouldReturnIndividualDataWithOrderASC() {
        WIRE_MOCK_RULE.stubFor(get(urlPathEqualTo(WIREMOCK_PATH))
                .withQueryParam(TYPE_OF_OWNERSHIP_PARAM, equalTo(INDIVIDUAL_OWNERSHIP))
                .withQueryParam(ORDER_PARAM, equalTo(ORDER_ASC))
                .willReturn(aResponse()
                        .withBodyFile("individual-data-with-filter-response.json")
                        .withStatus(STATUS_CODE)));

        List<String> names= given()
                .param(TYPE_OF_OWNERSHIP_PARAM, INDIVIDUAL_OWNERSHIP)
                .param(ORDER_PARAM, ORDER_ASC)
                .when().get(RESTASSURED_PATH)
                .then()
                .assertThat()
                .statusCode(STATUS_CODE)
                .body(not(isEmptyString()))
                .body("response.status", is(("success")))
                .body(matchesJsonSchemaInClasspath("schema/individual-jsonschema.json"))
                .extract().path("response.results.collect { it.Name }");

        assertThat("Данные не отсортированы по возрастанию", names,
                contains("Arthur Crosby", "Octavius Gamble", "Raja Michael", "Reece Ferrell", "Xanthus Bryant"));
    }




}
