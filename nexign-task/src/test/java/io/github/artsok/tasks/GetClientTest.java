package io.github.artsok.tasks;

import io.qameta.allure.*;
import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class GetClientTest extends BaseApiTest {


    @Epic(value = "GetClientTypes")
    @Feature(value = "Обязательные параметры сервиса")
    @Story(value = "Физические лица")
    @Severity(value = SeverityLevel.BLOCKER)
    @DisplayName("Возврат данных физических лиц (Без фильтра)")
    @Test
    public void shouldReturnIndividualDataWithoutOrder() {
        WIRE_MOCK_RULE.stubFor(get(urlPathEqualTo(WIREMOCK_PATH))
                .withQueryParam(TYPE_OF_OWNERSHIP_PARAM, equalTo(INDIVIDUAL_OWNERSHIP))
                .willReturn(aResponse()
                        .withBodyFile("individual-data-with-no-filter-response.json")
                        .withStatus(STATUS_CODE_200)));

        given().param(TYPE_OF_OWNERSHIP_PARAM, INDIVIDUAL_OWNERSHIP)
                .when().get(REST_ASSURED_PATH)
                .then()
                .assertThat()
                .statusCode(STATUS_CODE_200)
                .body(not(isEmptyString()))
                .body(RESPONSE_STATUS, is((SUCCESS)))
                .body(RESPONSE_TYPE_OF_OWNERSHIP, is(INDIVIDUAL_OWNERSHIP))
                .body(matchesJsonSchemaInClasspath(IND_SCHEMA_PATH));
    }

    @Epic(value = "GetClientTypes")
    @Features(value = {@Feature(value = "Обязательные параметры сервиса"),
            @Feature(value = "Не обязательные параметры сервиса")})
    @Story(value = "Физические лица")
    @Severity(value = SeverityLevel.MINOR)
    @DisplayName("Возврат данных физических лиц (Фильтр: Сортировка по возрастанию)")
    @Test
    public void shouldReturnIndividualDataWithOrderASC() {
        WIRE_MOCK_RULE.stubFor(get(urlPathEqualTo(WIREMOCK_PATH))
                .withQueryParam(TYPE_OF_OWNERSHIP_PARAM, equalTo(INDIVIDUAL_OWNERSHIP))
                .withQueryParam(ORDER_PARAM, equalTo(ORDER_ASC))
                .willReturn(aResponse()
                        .withBodyFile("individual-data-with-filter-response.json")
                        .withStatus(STATUS_CODE_200)));

        List<String> individualData = given()
                .param(TYPE_OF_OWNERSHIP_PARAM, INDIVIDUAL_OWNERSHIP)
                .param(ORDER_PARAM, ORDER_ASC)
                .when().get(REST_ASSURED_PATH)
                .then()
                .assertThat()
                .statusCode(STATUS_CODE_200)
                .body(not(isEmptyString()))
                .body(RESPONSE_STATUS, is((SUCCESS)))
                .body(RESPONSE_TYPE_OF_OWNERSHIP, is(INDIVIDUAL_OWNERSHIP))
                .body(matchesJsonSchemaInClasspath(IND_SCHEMA_PATH))
                .extract().path("response.results.collect { it.Name }");

        assertThat("Данные не отсортированы по возрастанию", individualData,
                contains("Arthur Crosby", "Octavius Gamble", "Raja Michael", "Reece Ferrell", "Xanthus Bryant"));
    }

    @Epic(value = "GetClientTypes")
    @Features(value = {@Feature(value = "Обязательные параметры сервиса"),
            @Feature(value = "Не обязательные параметры сервиса")})
    @Story(value = "Юридические лица")
    @Severity(value = SeverityLevel.BLOCKER)
    @DisplayName("Возврат данных юридических лиц (Фильтр: Сортировка по убыванию)")
    @Test
    public void shouldReturnOrganizationDataWithOrderDESC() {
        WIRE_MOCK_RULE.stubFor(get(urlPathEqualTo(WIREMOCK_PATH))
                .withQueryParam(TYPE_OF_OWNERSHIP_PARAM, equalTo(ORGANIZATION_OWNERSHIP))
                .withQueryParam(ORDER_PARAM, equalTo(ORDER_DESC))
                .willReturn(aResponse()
                        .withBodyFile("organization-data-with-filter-desc-response.json")
                        .withStatus(STATUS_CODE_200)));

        List<String> organizationData = given()
                .param(TYPE_OF_OWNERSHIP_PARAM, ORGANIZATION_OWNERSHIP)
                .param(ORDER_PARAM, ORDER_DESC)
                .when().get(REST_ASSURED_PATH)
                .then()
                .assertThat()
                .statusCode(STATUS_CODE_200)
                .body(not(isEmptyString()))
                .body(RESPONSE_STATUS, is((SUCCESS)))
                .body(RESPONSE_TYPE_OF_OWNERSHIP, is(ORGANIZATION_OWNERSHIP))
                .body(matchesJsonSchemaInClasspath(ORG_SCHEMA_PATH))
                .extract().path("response.results.collect { it.Company }");

        assertThat("Данные не отсортированы по убыванию", organizationData,
                contains("Velit Limited", "Nunc Ac Mattis Limited",
                        "Nec Leo Morbi LLP", "Egestas Duis Ac Foundation",
                        "Dictum Corporation", "Ac Turpis Egestas Inc."));
    }

    @Epic(value = "GetClientTypes")
    @Feature(value = "Обязательные параметры сервиса")
    @Story(value = "Юридические лица")
    @Severity(value = SeverityLevel.BLOCKER)
    @DisplayName("Возврат данных юридических лиц (Без фильтра)")
    @Test
    public void shouldReturnOrganizationDataWithoutOrder() {
        WIRE_MOCK_RULE.stubFor(get(urlPathEqualTo(WIREMOCK_PATH))
                .withQueryParam(TYPE_OF_OWNERSHIP_PARAM, equalTo(ORGANIZATION_OWNERSHIP))
                .willReturn(aResponse()
                        .withBodyFile("organization-data-no-filter-response.json")
                        .withStatus(STATUS_CODE_200)));

        given().param(TYPE_OF_OWNERSHIP_PARAM, ORGANIZATION_OWNERSHIP)
                .when().get(REST_ASSURED_PATH)
                .then()
                .assertThat()
                .statusCode(STATUS_CODE_200)
                .body(not(isEmptyString()))
                .body(RESPONSE_STATUS, is((SUCCESS)))
                .body(RESPONSE_TYPE_OF_OWNERSHIP, is(ORGANIZATION_OWNERSHIP))
                .body(matchesJsonSchemaInClasspath(ORG_SCHEMA_PATH));
    }



    @Epic(value = "GetClientTypes")
    @Feature(value = "Обязательные параметры сервиса")
    @Story(value = "Физические лица")
    @Severity(value = SeverityLevel.CRITICAL)
    @DisplayName("Ошибка 404 при не заданном параметре typeOfOwnership")
    @Test
    public void shouldReturnErrorWhenRequiredNotDefined() {
        WIRE_MOCK_RULE.stubFor(get(urlPathEqualTo(WIREMOCK_PATH))
                .withQueryParam(ORDER_PARAM, equalTo(ORDER_ASC))
                .withQueryParam(TYPE_OF_OWNERSHIP_PARAM, equalTo(""))
                .willReturn(aResponse()
                        .withBodyFile("individual-data-with-no-filter-error-response.json")
                        .withStatus(STATUS_CODE_404)));


        given().param(TYPE_OF_OWNERSHIP_PARAM, "")
                .param(ORDER_PARAM, ORDER_ASC)
                .when().get(REST_ASSURED_PATH)
                .then()
                .assertThat()
                .statusCode(STATUS_CODE_404)
                .body(not(isEmptyString()))
                .body(RESPONSE_STATUS, is((ERROR)));
    }



}
