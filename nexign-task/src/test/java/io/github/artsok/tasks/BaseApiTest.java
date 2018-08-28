package io.github.artsok.tasks;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import org.junit.BeforeClass;
import org.junit.ClassRule;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static io.restassured.RestAssured.*;
import static io.restassured.RestAssured.requestSpecification;

public class BaseApiTest {

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
    static final String SUCCESS = "success";
    static final String ERROR = "error";
    static final String IND_SCHEMA_PATH = "schema/individual-jsonschema.json";
    static final String ORG_SCHEMA_PATH = "schema/organization-jsonschema.json";
    static final String RESPONSE_STATUS = "response.status";
    static final String RESPONSE_TYPE_OF_OWNERSHIP = "response.typeofownership";






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

}
