package io.github.artsok.tasks;

import io.qameta.allure.*;
import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

public class GetClientTypesTest extends BaseApiTest {

    @Epic(value = "GetClientTypes")
    @Feature(value = "Обязательные параметры сервиса")
    @Story(value = "Физические лица")
    @Severity(value = SeverityLevel.BLOCKER)
    @DisplayName("Возврат данных физических лиц (Без фильтра)")
    @Test
    public void shouldReturnIndividualDataWithoutOrder() {
        given()
                .param(TYPE_OF_OWNERSHIP_PARAM, INDIVIDUAL_OWNERSHIP)
                .when().get(REST_ASSURED_PATH)
                .then()
                .assertThat()
                .spec(RS_OK_NOT_EMPTY_SUCCESS_STATUS)
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
        List<String> individualData = given()
                .param(TYPE_OF_OWNERSHIP_PARAM, INDIVIDUAL_OWNERSHIP)
                .param(ORDER_PARAM, ORDER_ASC)
                .when().get(REST_ASSURED_PATH)
                .then()
                .assertThat()
                .spec(RS_OK_NOT_EMPTY_SUCCESS_STATUS)
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
        List<String> organizationData = given()
                .param(TYPE_OF_OWNERSHIP_PARAM, ORGANIZATION_OWNERSHIP)
                .param(ORDER_PARAM, ORDER_DESC)
                .when().get(REST_ASSURED_PATH)
                .then()
                .assertThat()
                .spec(RS_OK_NOT_EMPTY_SUCCESS_STATUS)
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
        given().param(TYPE_OF_OWNERSHIP_PARAM, ORGANIZATION_OWNERSHIP)
                .when().get(REST_ASSURED_PATH)
                .then()
                .assertThat()
                .spec(RS_OK_NOT_EMPTY_SUCCESS_STATUS)
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
        given().param(TYPE_OF_OWNERSHIP_PARAM, "")
                .param(ORDER_PARAM, ORDER_ASC)
                .when().get(REST_ASSURED_PATH)
                .then()
                .assertThat()
                .spec(RS_ERROR_NOT_EMPTY);
    }

}
