package io.github.artsok.training

import io.appium.java_client.AppiumDriver
import io.appium.java_client.MobileElement
import io.github.artsok.training.matchers.WikiMatchers
import io.github.artsok.training.rules.AndroidTest
import io.github.artsok.training.rules.Driver
import io.github.artsok.training.rules.DriverResolver
import io.github.artsok.training.rules.Rotate
import io.github.artsok.training.ui.pageobjects.MainPage
import io.github.artsok.training.ui.pageobjects.SearchPage
import io.github.artsok.training.utils.lateClick
import io.github.artsok.training.utils.lateSendKeys
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.openqa.selenium.By.id
import org.openqa.selenium.By.xpath
import ru.yandex.qatools.matchers.decorators.MatcherDecorators.should
import ru.yandex.qatools.matchers.decorators.TimeoutWaiter.timeoutHasExpired
import ru.yandex.qatools.matchers.decorators.WaiterMatcherDecorator.decorateMatcherWithWaiter
import ru.yandex.qatools.matchers.webdriver.ExistsMatcher.exists
import ru.yandex.qatools.matchers.webdriver.driver.CanFindElementMatcher.canFindElement

@Driver
@Rotate
@DriverResolver
class SearchTests {

    private lateinit var mainPage: MainPage
    private lateinit var searchPage: SearchPage

    @BeforeEach
    fun setUp(driver: AppiumDriver<MobileElement>) {
        mainPage = MainPage(driver)
        searchPage = SearchPage(driver)
    }

    @AndroidTest
    fun `should Find Special Word In Search Result List`(driver: AppiumDriver<MobileElement>) {
        searchPage.searchResultTPL = "Object-oriented programming language"

        mainPage.searchWikipediaInputInit.lateClick(errorMassage = "Can't find and click 'Search Wikipedia input'")
        searchPage.searchInput.lateSendKeys("Java", errorMassage = "Cannot find and type into search input")

        assertThat("Cannot find search result with ${searchPage.searchResultTPL}", driver,
                decorateMatcherWithWaiter(canFindElement(xpath(searchPage.searchResultTPL)),
                        timeoutHasExpired(5000L)))
    }

    @AndroidTest
    fun `search Close Btn Should Not Exist On Main Page`(driver: AppiumDriver<MobileElement>) {
        searchPage.searchResultTPL = "Allure"

        mainPage.searchWikipediaInputInit.lateClick(errorMassage = "Can't find and click 'Search Wikipedia input'")
        searchPage.closeBtn.lateClick(errorMassage = "Can't find 'Search Close' Button")

        assertThat("Search cancel button is still present", searchPage.closeBtn, should(not(exists())))
    }

    @AndroidTest
    fun `amount Of Article Search Should Not Be Empty`(driver: AppiumDriver<MobileElement>) {
        val searchLine = "Linkin Park Diskography"

        mainPage.searchWikipediaInputInit.lateClick()
        searchPage.searchInput.lateSendKeys(searchLine)

        val searchResultsList = searchPage.getFoundArticles()
        assertTrue(searchResultsList.isNotEmpty(), "We found too few results")
    }


    /**
     * Ex2: Создание метода
     *
     * Написать тест, который проверяет наличие текста “Search…” в строке поиска перед вводом текста
     * и помечает тест упавшим, если такого текста нет.
     */
    @AndroidTest
    fun `special Word Should Exist In Search Input`(driver: AppiumDriver<MobileElement>) {
        mainPage.searchWikipediaInputInit.lateClick()
        assertThat("Special Word 'Search…' is missed",
                searchPage.searchInput.text, should(containsString("Search…")))
    }


    /**
     * Ex3: Отмена поиска
     *
     * Написать тест, который:
     * 1. Ищет какое-то слово
     * 2. Убеждается, что найдено несколько статей
     * 3. Отменяет поиск
     * 4. Убеждается, что результат поиска пропал
     */
    @AndroidTest
    fun `after Cancel Searches List Should Be Empty`(driver: AppiumDriver<MobileElement>) {
        mainPage.searchWikipediaInputInit.lateClick()
        searchPage.searchInput.lateSendKeys("C++")

        val searchResultsList = searchPage.getFoundArticles()

        assertThat("It's no results with you search text",
                searchResultsList.size, greaterThanOrEqualTo(1))
        searchPage.closeBtn.lateClick()

        assertThat("Search list not empty", driver,
                should(not(canFindElement(id("org.wikipedia:id/search_results_list")))))
    }

    /**
     * Ex4*: Проверка слов в поиске
     *
     * Написать тест, который:
     * Ищет какое-то слово
     * Убеждается, что в каждом результате поиска есть это слово.
     */
    @AndroidTest
    fun `result List Should Contain Special Words`(driver: AppiumDriver<MobileElement>) {
        mainPage.searchWikipediaInputInit.lateClick()
        searchPage.searchInput.lateSendKeys("Java")

        assertThat("Java",
                should(WikiMatchers(mainPage)
                        .containsInResultList())) //TODO: refactor -> Подумать, что делать со своим матчером
    }

    /**
     * Ex9*: Рефакторинг темплейта
     *
     * Написать тест, который будет делать поиск по любому запросу на ваш выбор
     * (поиск по этому слову должен возвращать как минимум 3 результата).
     * Далее тест должен убеждаться, что первых три результата присутствуют в результате поиска.
     */
    @AndroidTest
    fun `result List Should Contain Three Special Article With Title And Description`(driver: AppiumDriver<MobileElement>) {
        mainPage.searchWikipediaInputInit.lateClick()
        searchPage.searchInput.lateSendKeys("Java")
        searchPage.waitForElementByTitleAndDescription("Java", "Island of Indonesia")
        searchPage.waitForElementByTitleAndDescription("Java (programming language)", "Object-oriented programming language")
        searchPage.waitForElementByTitleAndDescription("JavaScript", "Programming language")
    }

}