package io.github.artsok.tasks;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.ResponseSpecification;
import org.junit.BeforeClass;
import org.junit.ClassRule;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static io.restassured.RestAssured.*;
import static io.restassured.parsing.Parser.JSON;
import static org.hamcrest.Matchers.*;

public class BaseApiTest {

    private static final String RESPONSE_STATUS = "response.status";
    private static final String SUCCESS = "success";
    private static final String ERROR = "error";

    static final int STATUS_CODE_200 = 200;
    static final int STATUS_CODE_404 = 404;
    static final String WIREMOCK_PATH = "/api/getClientTypes";
    static final String REST_ASSURED_PATH = "getClientTypes";
    static final String TYPE_OF_OWNERSHIP_PARAM = "typeOfOwnership";
    static final String ORDER_PARAM = "orderBy";
    static final String ORDER_ASC = "asc";
    static final String ORDER_DESC = "desc";
    static final String INDIVIDUAL_OWNERSHIP = "1";
    static final String ORGANIZATION_OWNERSHIP = "2";
    static final String IND_SCHEMA_PATH = "schema/individual-jsonschema.json";
    static final String ORG_SCHEMA_PATH = "schema/organization-jsonschema.json";
    static final String RESPONSE_TYPE_OF_OWNERSHIP = "response.typeofownership";

    /**
     * Спецификация для Response
     * Задаем проверки в данной спецификации со статусом SUCCESS
     */
    static final ResponseSpecification RS_OK_NOT_EMPTY_SUCCESS_STATUS = new ResponseSpecBuilder()
            .expectStatusCode(STATUS_CODE_200)
            .expectBody(RESPONSE_STATUS, is((SUCCESS)))
            .expectBody(not(isEmptyString()))
            .setDefaultParser(JSON)
            .build();

    /**
     * Спецификация для Response
     * Задаем проверки в данной спецификации со статусом ERROR
     */
    static final ResponseSpecification RS_ERROR_NOT_EMPTY = new ResponseSpecBuilder()
            .expectStatusCode(STATUS_CODE_404)
            .expectBody(RESPONSE_STATUS, is((ERROR)))
            .setDefaultParser(JSON)
            .build();

    @ClassRule
    public static final WireMockRule WIRE_MOCK_RULE = new WireMockRule(
            wireMockConfig()
                    .dynamicPort()
                    .dynamicHttpsPort()
    );

    /**
     * Задаем спецификация для Request. Настраиваем все заглушки и выставляем приоритеты.
     */
    @BeforeClass
    public static void setUp() {
        baseURI = "http://localhost";
        port = WIRE_MOCK_RULE.port();
        basePath = "api";
        defaultParser = JSON;
        requestSpecification = new RequestSpecBuilder()
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();
        RestAssured.filters(new AllureRestAssured());

        WIRE_MOCK_RULE.stubFor(get(urlPathEqualTo(WIREMOCK_PATH))
                .atPriority(2)
                .withQueryParam(TYPE_OF_OWNERSHIP_PARAM, equalTo(INDIVIDUAL_OWNERSHIP))
                .willReturn(aResponse()
                        .withBodyFile("Individual-data-with-no-filter-response.json")
                        .withStatus(STATUS_CODE_200)));

        WIRE_MOCK_RULE.stubFor(get(urlPathEqualTo(WIREMOCK_PATH))
                .atPriority(1)
                .withQueryParam(TYPE_OF_OWNERSHIP_PARAM, equalTo(INDIVIDUAL_OWNERSHIP))
                .withQueryParam(ORDER_PARAM, equalTo(ORDER_ASC))
                .willReturn(aResponse()
                        .withBodyFile("Individual-data-with-filter-response.json")
                        .withStatus(STATUS_CODE_200)));

        WIRE_MOCK_RULE.stubFor(get(urlPathEqualTo(WIREMOCK_PATH))
                .atPriority(1)
                .withQueryParam(TYPE_OF_OWNERSHIP_PARAM, equalTo(ORGANIZATION_OWNERSHIP))
                .withQueryParam(ORDER_PARAM, equalTo(ORDER_DESC))
                .willReturn(aResponse()
                        .withBodyFile("organization-data-with-filter-desc-response.json")
                        .withStatus(STATUS_CODE_200)));

        WIRE_MOCK_RULE.stubFor(get(urlPathEqualTo(WIREMOCK_PATH))
                .atPriority(2)
                .withQueryParam(TYPE_OF_OWNERSHIP_PARAM, equalTo(ORGANIZATION_OWNERSHIP))
                .willReturn(aResponse()
                        .withBodyFile("organization-data-no-filter-response.json")
                        .withStatus(STATUS_CODE_200)));

        WIRE_MOCK_RULE.stubFor(get(urlPathEqualTo(WIREMOCK_PATH))
                .withQueryParam(ORDER_PARAM, equalTo(ORDER_ASC))
                .withQueryParam(TYPE_OF_OWNERSHIP_PARAM, equalTo(""))
                .willReturn(aResponse()
                        .withBodyFile("Individual-data-with-no-filter-error-response.json")
                        .withStatus(STATUS_CODE_404)));
    }
}
