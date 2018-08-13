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

class ArticleTests {
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
    fun articleShouldHaveSpecialTitle() {
        val articlePage by lazy { ArticlePage(driver) }

        mainPage.searchWikipediaInputInit.lateClick(errorMassage = "Can't find and click 'Search Wikipedia input'")
        searchPage.searchInput.lateSendKeys("Java", errorMassage = "Cannot find and type into search input")
        searchPage.clickByArticleWithSubString("Object-oriented programming language")

        val articleTitle = articlePage.getArticleTitle()
        assertThat("We see unexpected title", articleTitle, equalTo("Java (programming language)"))
    }

    @Test
    fun articleShouldBeWithSwipeAction() {
        val articlePage by lazy { ArticlePage(driver) }

        mainPage.searchWikipediaInputInit.lateClick(errorMassage = "Can't find and click 'Search Wikipedia input'")
        searchPage.searchInput.lateSendKeys("Appium", errorMassage = "Cannot find and type into search input")
        searchPage.clickByArticleWithSubString("Appium")
        articlePage.swipeToFooter()
    }


}