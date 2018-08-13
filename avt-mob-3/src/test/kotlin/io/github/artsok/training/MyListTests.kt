package io.github.artsok.training

import io.appium.java_client.MobileElement
import io.appium.java_client.android.AndroidDriver
import io.github.artsok.training.rules.DriverRule
import io.github.artsok.training.rules.RotateRule
import io.github.artsok.training.ui.pageobjects.*
import io.github.artsok.training.utils.lateClick
import io.github.artsok.training.utils.lateSendKeys
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExternalResource
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.openqa.selenium.By
import ru.yandex.qatools.matchers.decorators.MatcherDecorators.should
import ru.yandex.qatools.matchers.webdriver.driver.CanFindElementMatcher.canFindElement

class MyListTests {

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

    /**
     * 1. Запустить приложение
     * 2. Ввести название слова в поиск
     * 3. Выбрать статью
     * 4. Открыть меню нажав кнопкой 'more options' и нажать кнопку 'Add to reading list'. Далее клик по Overlay
     * 5. Создать новый список
     * 6. Перейти в свои списки
     * 7. Выбрать один из списков
     * 8. Убедиться, что тут присутсвуте нужная нам статья
     * 9. Удалить статью
     * 10. Проверить, что статья удалена
     */
    @Test
    fun articleShouldBeSavedToMyList() {
        val articlePage by lazy { ArticlePage(driver) }
        val navigationUIPage by lazy { NavigationUIPage(driver) }
        val myListPage by lazy { MyListPage(driver) }

        mainPage.searchWikipediaInputInit.lateClick(errorMassage = "Can't find and click 'Search Wikipedia input'")
        searchPage.searchInput.lateSendKeys("Appium", errorMassage = "Cannot find and type into search input")
        searchPage.clickByArticleWithSubString("Appium")

        val articleTitle = articlePage.getArticleTitle()
        val nameOfFolder = "Learning programing"

        articlePage.addArticleToMyList(nameOfFolder)
        articlePage.closeArticle()
        navigationUIPage.clickMyList()
        myListPage.openFolderByName(nameOfFolder)
        myListPage.swipeArticleToDelete(articleTitle)

        assertThat("Cannot delete saved article '$articleTitle'", driver,
                should(Matchers.not(canFindElement(By.xpath("//*[@text='%s']".format(articleTitle))))))
    }
}