package io.github.artsok.training

import io.appium.java_client.AppiumDriver
import io.appium.java_client.MobileElement
import io.github.artsok.training.rules.AndroidTest
import io.github.artsok.training.rules.Driver
import io.github.artsok.training.rules.DriverResolver
import io.github.artsok.training.rules.Rotate
import io.github.artsok.training.ui.pageobjects.ArticlePage
import io.github.artsok.training.ui.pageobjects.MainPage
import io.github.artsok.training.ui.pageobjects.SearchPage
import io.github.artsok.training.utils.lateClick
import io.github.artsok.training.utils.lateSendKeys
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.openqa.selenium.By
import org.openqa.selenium.ScreenOrientation
import ru.yandex.qatools.matchers.decorators.MatcherDecorators.should
import ru.yandex.qatools.matchers.decorators.TimeoutWaiter.timeoutHasExpired
import ru.yandex.qatools.matchers.decorators.WaiterMatcherDecorator
import ru.yandex.qatools.matchers.webdriver.driver.CanFindElementMatcher.canFindElement
import java.time.Duration

@Driver
@Rotate
@DriverResolver
class ChangeAppConditionTests {

    private lateinit var mainPage: MainPage
    private lateinit var searchPage: SearchPage

    @BeforeEach
    fun setUp(driver: AppiumDriver<MobileElement>) {
        mainPage = MainPage(driver)
        searchPage = SearchPage(driver)
    }

    @AndroidTest
    fun `article Should Not Be Rename After Change Screen Orientation On Search Results`(driver: AppiumDriver<MobileElement>)  {
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

    @AndroidTest //flaky test
    fun `search Article Should Be Available After Background`(driver: AppiumDriver<MobileElement>) {
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