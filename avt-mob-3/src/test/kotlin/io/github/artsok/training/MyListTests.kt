package io.github.artsok.training

import io.appium.java_client.AppiumDriver
import io.appium.java_client.MobileElement
import io.github.artsok.training.rules.AndroidTest
import io.github.artsok.training.rules.Driver
import io.github.artsok.training.rules.DriverResolver
import io.github.artsok.training.rules.Rotate
import io.github.artsok.training.ui.pageobjects.*
import io.github.artsok.training.utils.lateClick
import io.github.artsok.training.utils.lateSendKeys
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.BeforeEach
import org.openqa.selenium.By.xpath
import ru.yandex.qatools.matchers.decorators.MatcherDecorators.should
import ru.yandex.qatools.matchers.webdriver.driver.CanFindElementMatcher.canFindElement

@Driver
@Rotate
@DriverResolver
class MyListTests {

    private lateinit var mainPage: MainPage
    private lateinit var searchPage: SearchPage

    @BeforeEach
    fun setUp(driver: AppiumDriver<MobileElement>) {
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
    @AndroidTest
    fun `article Should Be Saved To My List`(driver: AppiumDriver<MobileElement>) {
        val articlePage by lazy { ArticlePage(driver) }
        val navigationUIPage by lazy { NavigationUIPage(driver) }
        val myListPage by lazy { MyListPage(driver) }

        mainPage.searchWikipediaInputInit.lateClick(errorMassage = "Can't find and click 'Search Wikipedia input'")
        searchPage.searchInput.lateSendKeys("Appium", errorMassage = "Cannot find and type into search input")
        searchPage.clickByArticleWithSubString("Appium")

        val articleTitle = articlePage.getArticleTitle()
        val nameOfFolder = "Learning programing"

        articlePage.addFirstArticleToMyList(nameOfFolder)
        articlePage.closeArticle()
        navigationUIPage.clickMyList()
        myListPage.openFolderByName(nameOfFolder)
        myListPage.swipeArticleToDelete(articleTitle)

        assertThat("Cannot delete saved article '$articleTitle'", driver,
                should(not(canFindElement(xpath("//*[@text='%s']".format(articleTitle))))))
    }


    /**
     * Ex5: Тест: сохранение двух статей
     *
     * Написать тест, который:
     * 1. Сохраняет две статьи в одну папку
     * 2. Удаляет одну из статей
     * 3. Убеждается, что вторая осталась
     * 4. Переходит в неё и убеждается, что title совпадает
     */
    @AndroidTest
    fun `two Article Should Be Saved To List`(driver: AppiumDriver<MobileElement>) {
        val firstArticleName = "Java"
        val secondArticleName = "Kotlin"
        val nameOfArticlesList = "My favorite list"

        mainPage.searchWikipediaInputInit.lateClick()
        searchPage.searchInput.lateSendKeys(firstArticleName)
        searchPage.clickByArticleWithSubString("Island of Indonesia")

        val articlePage = ArticlePage(driver)
        articlePage.addFirstArticleToMyList(nameOfArticlesList)
        articlePage.closeArticle()

        mainPage.searchWikipediaInputInit.lateClick()
        searchPage.searchInput.lateSendKeys(secondArticleName)
        searchPage.clickByArticleWithSubString("Programming language")

        val secondArticleTitle = articlePage.getArticleTitle()
        articlePage.addNextArticlesToMyList(nameOfArticlesList)
        articlePage.closeArticle()

        val navigationUIPage = NavigationUIPage(driver)
        navigationUIPage.clickMyList()

        val myListPage = MyListPage(driver)
        myListPage.openFolderByName(nameOfArticlesList)

        assertThat(myListPage.getAmountOfArticles(), equalTo(2))
        myListPage.swipeArticleToDelete(firstArticleName)
        assertThat(myListPage.getAmountOfArticles(), equalTo(1))
        assertThat("Cannot find article in saved list",
                driver, should(canFindElement(xpath("//*[@text='Kotlin (programming language)']"))))

        myListPage.clickByArticlesInMyList("Kotlin (programming language)")
        val currentTitle = articlePage.getArticleTitle()

        assertThat("Title of article not same", secondArticleTitle, should(equalTo(currentTitle)))
    }
}