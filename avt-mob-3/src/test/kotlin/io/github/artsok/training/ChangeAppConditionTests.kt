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
import org.hamcrest.Matchers.equalTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExternalResource
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.openqa.selenium.By
import org.openqa.selenium.ScreenOrientation
import ru.yandex.qatools.matchers.decorators.MatcherDecorators.should
import ru.yandex.qatools.matchers.decorators.TimeoutWaiter.timeoutHasExpired
import ru.yandex.qatools.matchers.decorators.WaiterMatcherDecorator
import ru.yandex.qatools.matchers.webdriver.driver.CanFindElementMatcher.canFindElement
import java.time.Duration

class ChangeAppConditionTests {
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
    fun articleShouldNotBeRenameAfterChangeScreenOrientationOnSearchResults() {
        val articlePage by lazy { ArticlePage(driver) }

        val searchLine = "Java"
        mainPage.searchWikipediaInputInit.lateClick(errorMassage = "Can't find and click 'Search Wikipedia input'")
        searchPage.searchInput.lateSendKeys(searchLine, errorMassage = "Cannot find and type into search input")
        searchPage.clickByArticleWithSubString("Object-oriented programming language")

        val titleBeforeRotation = articlePage.getArticleTitle()
        driver.rotate(ScreenOrientation.LANDSCAPE)
        val titleAfterRotation = articlePage.getArticleTitle()

        assertThat("Article title have been changed after screen rotation", titleBeforeRotation, equalTo(titleAfterRotation))

        driver.rotate(ScreenOrientation.PORTRAIT)
        val titleAfterSecondRotation = articlePage.getArticleTitle()

        assertThat("Article title have been changed after screen rotation", titleBeforeRotation, equalTo(titleAfterSecondRotation))
    }

    @Test //flaky test
    fun searchArticleShouldBeAvailableAfterBackground() {
        val articlePage by lazy { ArticlePage(driver) }

        val searchLine = "Java"
        mainPage.searchWikipediaInputInit.lateClick(errorMassage = "Can't find and click 'Search Wikipedia input'")
        searchPage.searchInput.lateSendKeys(searchLine, errorMassage = "Cannot find and type into search input")
        searchPage.clickByArticleWithSubString("Object-oriented programming language")

        driver.runAppInBackground(Duration.ofSeconds(3))

        assertThat("Cannot find article after returning from background",
                driver, WaiterMatcherDecorator.decorateMatcherWithWaiter(should(canFindElement(By.xpath("//*[@resource-id='org.wikipedia:id/page_list_item_container']" +
                "//*[contains(@text, 'Object-oriented programming language')]"))), timeoutHasExpired(5000L)))
    }
}