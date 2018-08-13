package io.github.artsok.training

import io.appium.java_client.MobileElement
import io.appium.java_client.android.AndroidDriver
import io.github.artsok.training.rules.DriverRule
import io.github.artsok.training.rules.RotateRule
import io.github.artsok.training.ui.pageobjects.ArticlePage
import io.github.artsok.training.ui.pageobjects.MainPage
import io.github.artsok.training.ui.pageobjects.SearchPage
import io.github.artsok.training.utils.lateClick
import io.github.artsok.training.utils.lateSendKeys
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExternalResource
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.openqa.selenium.By
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
                decorateMatcherWithWaiter(canFindElement(By.xpath(searchPage.searchResultTPL)),
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
        val articlePage by lazy { ArticlePage(driver) }
        val searchLine = "Linkin Park Diskography"

        mainPage.searchWikipediaInputInit.lateClick()
        searchPage.searchInput.lateSendKeys(searchLine)

        val searchResultsList = articlePage.getFoundArticles()
        assertTrue(searchResultsList.isNotEmpty(), "We found too few results")
    }

}