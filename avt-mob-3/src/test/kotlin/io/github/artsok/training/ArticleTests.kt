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
import org.openqa.selenium.By.id

@Driver
@Rotate
@DriverResolver
class ArticleTests {

    private lateinit var mainPage: MainPage
    private lateinit var searchPage: SearchPage

    @BeforeEach
    fun setUp(driver: AppiumDriver<MobileElement>) {
        mainPage = MainPage(driver)
        searchPage = SearchPage(driver)
    }

    @AndroidTest
    fun `article Should Have Special Title`(driver: AppiumDriver<MobileElement>) {
        val articlePage by lazy { ArticlePage(driver) }

        mainPage.searchWikipediaInputInit.lateClick(errorMassage = "Can't find and click 'Search Wikipedia input'")
        searchPage.searchInput.lateSendKeys("Java", errorMassage = "Cannot find and type into search input")
        searchPage.clickByArticleWithSubString("Object-oriented programming language")

        val articleTitle = articlePage.getArticleTitle()
        assertThat("We see unexpected title", articleTitle, equalTo("Java (programming language)"))
    }

    @AndroidTest
    fun `article Should Be With Swipe Action`(driver: AppiumDriver<MobileElement>) {
        val articlePage by lazy { ArticlePage(driver) }

        mainPage.searchWikipediaInputInit.lateClick(errorMassage = "Can't find and click 'Search Wikipedia input'")
        searchPage.searchInput.lateSendKeys("Appium", errorMassage = "Cannot find and type into search input")
        searchPage.clickByArticleWithSubString("Appium")
        articlePage.swipeToFooter()
    }

    /**
     * Ex6: Assert title
     *
     * Написать тест, который открывает статью и убеждается, что у нее есть элемент title.
     * Важно: тест не должен дожидаться появления title, проверка должна производиться сразу.
     * Если title не найден - тест падает с ошибкой. Метод можно назвать assertElementPresent.
     */
    @AndroidTest
    fun `article Should Have Title With Quick Find`(driver: AppiumDriver<MobileElement>) {
        val searchLine = "Java"

        mainPage.searchWikipediaInputInit.lateClick()
        searchPage.searchInput.lateSendKeys(searchLine)
        searchPage.clickByArticleWithSubString("Island of Indonesia")
        searchPage.assertElementPresent(id("org.wikipedia:id/view_page_title_text"),
                "Article doesn't have title")
    }
}