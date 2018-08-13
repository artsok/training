package io.github.artsok.training

import io.appium.java_client.MobileElement
import io.appium.java_client.android.AndroidDriver
import io.github.artsok.training.matchers.WikiMatchers
import io.github.artsok.training.rules.DriverRule
import io.github.artsok.training.rules.RotateRule
import io.github.artsok.training.ui.pageobjects.MainPage
import io.github.artsok.training.ui.pageobjects.SearchPage
import io.github.artsok.training.utils.lateClick
import io.github.artsok.training.utils.lateSendKeys
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExternalResource
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.openqa.selenium.By.id
import org.openqa.selenium.By.xpath
import ru.yandex.qatools.matchers.decorators.MatcherDecorators.should
import ru.yandex.qatools.matchers.decorators.TimeoutWaiter.timeoutHasExpired
import ru.yandex.qatools.matchers.decorators.WaiterMatcherDecorator.decorateMatcherWithWaiter
import ru.yandex.qatools.matchers.webdriver.ExistsMatcher.exists
import ru.yandex.qatools.matchers.webdriver.driver.CanFindElementMatcher.canFindElement
import kotlin.test.assertTrue


class SearchTests {
    private lateinit var driver: AndroidDriver<MobileElement>
    private lateinit var mainPage: MainPage
    private lateinit var searchPage: SearchPage

    private val driverRule = DriverRule()
    private val rotateRule = RotateRule()
    private val extractDriver = object : ExternalResource() {
        override fun before() {
            driver = driverRule.getDriver()
        }
    }

    @Rule
    @JvmField
    val chain: TestRule = RuleChain
            .outerRule(driverRule)
            .around(extractDriver)
            .around(rotateRule)


    @Before
    fun setUp() {
        mainPage = MainPage(driver)
        searchPage = SearchPage(driver)
    }

    @Test
    fun shouldFindSpecialWordInSearchResultList() {
        searchPage.searchResultTPL = "Object-oriented programming language"

        mainPage.searchWikipediaInputInit.lateClick(errorMassage = "Can't find and click 'Search Wikipedia input'")
        searchPage.searchInput.lateSendKeys("Java", errorMassage = "Cannot find and type into search input")

        assertThat("Cannot find search result with ${searchPage.searchResultTPL}", driver,
                decorateMatcherWithWaiter(canFindElement(xpath(searchPage.searchResultTPL)),
                        timeoutHasExpired(5000L)))
    }

    @Test
    fun searchCloseBtnShouldNotExistOnMainPage() {
        searchPage.searchResultTPL = "Allure"

        mainPage.searchWikipediaInputInit.lateClick(errorMassage = "Can't find and click 'Search Wikipedia input'")
        searchPage.closeBtn.lateClick(errorMassage = "Can't find 'Search Close' Button")

        assertThat("Search cancel button is still present", searchPage.closeBtn, should(not(exists())))
    }

    @Test
    fun amountOfArticleSearchShouldNotBeEmpty() {
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
    @Test
    fun specialWordShouldExistInSearchInput() {
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
    @Test
    fun afterCancelSearchesListShouldBeEmpty() {
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
    @Test
    fun resultListShouldContainSpecialWords() {
        mainPage.searchWikipediaInputInit.lateClick()
        searchPage.searchInput.lateSendKeys("Java")

        assertThat("Java",
                should(WikiMatchers(mainPage)
                        .containsInResultList())) //TODO: refactor -> Подумать, что делать со своим матчером
    }

}