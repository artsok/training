package io.github.artsok.training


import io.appium.java_client.MobileElement
import io.appium.java_client.android.AndroidDriver
import io.github.artsok.training.matchers.WikiMatchers
import io.github.artsok.training.rules.DriverRule
import io.github.artsok.training.rules.RotateRule
import io.github.artsok.training.ui.pageobjects.ArticlePage
import io.github.artsok.training.ui.pageobjects.MainPage
import io.github.artsok.training.ui.pageobjects.SearchPage
import io.github.artsok.training.utils.lateClick
import io.github.artsok.training.utils.lateSendKeys
import io.github.artsok.training.utils.randomString
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
import org.openqa.selenium.ScreenOrientation
import org.openqa.selenium.WebElement
import ru.yandex.qatools.matchers.decorators.MatcherDecorators.should
import ru.yandex.qatools.matchers.decorators.TimeoutWaiter.timeoutHasExpired
import ru.yandex.qatools.matchers.decorators.WaiterMatcherDecorator.decorateMatcherWithWaiter
import ru.yandex.qatools.matchers.webdriver.ExistsMatcher.exists
import ru.yandex.qatools.matchers.webdriver.driver.CanFindElementMatcher.canFindElement
import java.time.Duration.ofSeconds
import kotlin.test.assertTrue


class MobileTest {

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

        assertThat("Search cancel button is still present",searchPage.closeBtn, should(not(exists())))
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

    /**
     * Написать тест, который проверяет наличие текста “Search…” в строке поиска перед вводом текста
     * и помечает тест упавшим, если такого текста нет.
     */
    @Test
    fun specialWordShouldExistInSearchInput() {
        mainPage.actions(xpath("//*[contains(@text, 'Search Wikipedia')]"),
                WebElement::click,
                "Can't find Search Wikipedia input")
        val searchElement = mainPage.actions(xpath("//*[contains(@text, 'Search…')]"))
        assertThat("Special Word 'Search…' is missed",
                searchElement.text, should(containsString("Search…")))
    }

    /**
     * Написать тест, который:
     * 1. Ищет какое-то слово
     * 2. Убеждается, что найдено несколько статей
     * 3. Отменяет поиск
     * 4. Убеждается, что результат поиска пропал
     */
    @Test
    fun afterCancelSearchesListShouldBeEmpty() {
        mainPage.actions(xpath("//*[contains(@text, 'Search Wikipedia')]"),
                WebElement::click,
                "Can't find Search Wikipedia input")
        mainPage.actions(xpath("//*[contains(@text, 'Search…')]"),
                { element: WebElement -> element.sendKeys("C++") })

        val searchResultsList = mainPage.getListViewElement(id("org.wikipedia:id/search_results_list"),
                id("org.wikipedia:id/page_list_item_container"))
        assertThat("It's no results with you search text", searchResultsList.size, greaterThanOrEqualTo(1))

        mainPage.actions(id("search_close_btn"),
                WebElement::click,
                "Can't find Search Close Bottom")
        assertThat("Searche list not empty", driver, should(not(canFindElement(id("org.wikipedia:id/search_results_list")))))
    }

    /**
     * Написать тест, который:
     * Ищет какое-то слово
     * Убеждается, что в каждом результате поиска есть это слово.
     */
    @Test
    fun resultListShouldContainSpecialWords() {
        mainPage.actions(xpath("//*[contains(@text, 'Search Wikipedia')]"),
                WebElement::click,
                "Can't find Search Wikipedia input")
        mainPage.actions(xpath("//*[contains(@text, 'Search…')]"),
                { element: WebElement -> element.sendKeys("Java") })
        assertThat("Java", should(WikiMatchers(mainPage).containsInResultList())) //TODO: refactor
    }


    /**
     * III. Сложные тесты
     */
    @Test
    fun articleShouldBeWithSwipeAction() {
        val articlePage by lazy { ArticlePage(driver) }

        mainPage.searchWikipediaInputInit.lateClick(errorMassage = "Can't find and click 'Search Wikipedia input'")
        searchPage.searchInput.lateSendKeys("Appium", errorMassage = "Cannot find and type into search input")
        searchPage.clickByArticleWithSubString("Appium")
        articlePage.swipeToFooter()
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
        mainPage.actions(xpath("//*[contains(@text, 'Search Wikipedia')]"),
                WebElement::click)
        mainPage.actions(xpath("//*[contains(@text, 'Search…')]"),
                { element: WebElement -> element.sendKeys("Appium") })
        mainPage.actions(xpath("//*[@resource-id='org.wikipedia:id/page_list_item_title'][contains(@text, 'Appium')]"),
                WebElement::click)
        mainPage.actions(id("org.wikipedia:id/view_page_title_text"))
        mainPage.actions(xpath("//*[@content-desc='More options']"), WebElement::click)
        mainPage.actions(xpath("//android.widget.ListView"))
        mainPage.actions(xpath("//*[@text='Add to reading list']"), WebElement::click, "Cannot find option to add article to reading list")
        mainPage.actions(id("org.wikipedia:id/onboarding_button"), WebElement::click, "Cannot find 'Got it' tip overlay'")
        mainPage.actions(id("org.wikipedia:id/text_input"), WebElement::clear, "Cannot find input to set name of articles folder")
        mainPage.actions(id("org.wikipedia:id/text_input"),
                { webElement -> webElement.sendKeys("Learning programing") },
                "Cannot put text into articles folder input")
        mainPage.actions(xpath("//*[@text='OK']"), WebElement::click, "Cannot press 'OK' button ")
        mainPage.actions(xpath("//android.widget.ImageButton[@content-desc='Navigate up']"), WebElement::click, "Cannot close article, cannot find X link")
        mainPage.actions(xpath("//android.widget.FrameLayout[@content-desc='My lists']"), WebElement::click, "Cannot find navigation button to My lists")
        mainPage.actions(xpath("//*[@text='Learning programing']"), WebElement::click, "Cannot find created folder")
        mainPage.swipeElementToLeft(xpath("//*[@text='Appium']"), "Cannot find saved article")
        assertThat("Cannot delete saved article", driver, should(not(canFindElement(xpath("//*[@text='Appium']")))))
    }

    @Test
    fun amountOfArticleSearchShouldNotBeEmpty() {
        val searchLine = "Linkin Park Diskography"
        mainPage.actions(xpath("//*[contains(@text, 'Search Wikipedia')]"),
                WebElement::click)
        mainPage.actions(xpath("//*[contains(@text, 'Search…')]"),
                { element: WebElement -> element.sendKeys(searchLine) })
        val searchResultsList = mainPage.getListViewElement(id("org.wikipedia:id/search_results_list"),
                id("org.wikipedia:id/page_list_item_container"))
        assertTrue(searchResultsList.isNotEmpty(), "We found too few results")
    }

    @Test
    fun amountOfArticleSearchShouldBeEmpty() {
        val searchLine = ('a'..'z').randomString(6)
        mainPage.actions(xpath("//*[contains(@text, 'Search Wikipedia')]"),
                WebElement::click)
        mainPage.actions(xpath("//*[contains(@text, 'Search…')]"),
                { element: WebElement -> element.sendKeys(searchLine) })
        mainPage.actions(xpath("//*[@resource-id='org.wikipedia:id/search_empty_text']"),
                errorMassage = "Cannot find empty result label by request $searchLine")

        val searchResultLocator = "//*[@resource-id='org.wikipedia:id/search_results_list']/*[@resource-id='org.wikipedia:id/page_list_item_container']"
        assertThat("We've found some results by request $searchLine", driver, should(not(canFindElement(xpath(searchResultLocator)))))
    }


    @Test
    fun articleShouldNotBeRenameAfterChangeScreenOrientationOnSearchResults() {
        val searchLine = "Java"
        mainPage.actions(xpath("//*[contains(@text, 'Search Wikipedia')]"),
                WebElement::click)
        mainPage.actions(xpath("//*[contains(@text, 'Search…')]"),
                { element: WebElement -> element.sendKeys(searchLine) })
        mainPage.actions(xpath("//*[@resource-id='org.wikipedia:id/page_list_item_container']//*[contains(@text, 'Object-oriented programming language')]"),
                WebElement::click, "Can't find element with text 'Object-oriented programming language' topic searching by $searchLine")

        val titleBeforeRotation = mainPage.waitForElementAndGetAttribute(id("org.wikipedia:id/view_page_title_text"), attribute = "text", errorMassage = "Cannot find title of article")
        driver.rotate(ScreenOrientation.LANDSCAPE)
        val titleAfterRotation = mainPage.waitForElementAndGetAttribute(id("org.wikipedia:id/view_page_title_text"), attribute = "text", errorMassage = "Cannot find title of article")

        assertThat("Article title have been changed after screen rotation", titleBeforeRotation, equalTo(titleAfterRotation))

        driver.rotate(ScreenOrientation.PORTRAIT)
        val titleAfterSecondRotation = mainPage.waitForElementAndGetAttribute(id("org.wikipedia:id/view_page_title_text"), attribute = "text", errorMassage = "Cannot find title of article")

        assertThat("Article title have been changed after screen rotation", titleBeforeRotation, equalTo(titleAfterSecondRotation))
    }

    @Test
    fun searchArticleShouldBeAvailableAfterBackground() {
        val searchLine = "Java"
        mainPage.actions(xpath("//*[contains(@text, 'Search Wikipedia')]"),
                WebElement::click)
        mainPage.actions(xpath("//*[contains(@text, 'Search…')]"),
                { element: WebElement -> element.sendKeys(searchLine) })
        mainPage.actions(xpath("//*[@resource-id='org.wikipedia:id/page_list_item_container']//*[contains(@text, 'Object-oriented programming language')]"),
                errorMassage = "Can't find element with text 'Object-oriented programming language' topic searching by $searchLine")
        driver.runAppInBackground(ofSeconds(3))


        assertThat("Cannot find article after returning from background",
                driver, should(canFindElement(xpath("//*[@resource-id='org.wikipedia:id/page_list_item_container']//*[contains(@text, 'Object-oriented programming language')]"))))
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
    @Test
    fun twoArticleShouldBeSavedToList() {
        val firstArticleName = "Java"
        val secondArticleName = "Kotlin"
        val nameOfArticlesList = "My favorite list"

        mainPage.actions(xpath("//*[contains(@text, 'Search Wikipedia')]"),
                WebElement::click)
        mainPage.actions(xpath("//*[contains(@text, 'Search…')]"),
                { it.sendKeys(firstArticleName) })
        mainPage.actions(xpath("//*[@resource-id='org.wikipedia:id/page_list_item_container']//*[contains(@text, 'Island of Indonesia')]"),
                WebElement::click)
        mainPage.actions(xpath("//*[@content-desc='More options']"), WebElement::click)
        mainPage.actions(xpath("//android.widget.ListView"))
        mainPage.actions(xpath("//*[@text='Add to reading list']"), WebElement::click,
                "Cannot find option to add article to reading list")
        mainPage.actions(id("org.wikipedia:id/onboarding_button"), WebElement::click,
                "Cannot find 'Got it' tip overlay'")
        mainPage.actions(id("org.wikipedia:id/text_input"), WebElement::clear,
                "Cannot find input to set name of articles folder")
        mainPage.actions(id("org.wikipedia:id/text_input"), { it.sendKeys(nameOfArticlesList) },
                "Cannot put text into articles folder input")
        mainPage.actions(xpath("//*[@text='OK']"), WebElement::click, "Cannot press 'OK' button ")
        mainPage.actions(xpath("//android.widget.ImageButton[@content-desc='Navigate up']"), WebElement::click,
                "Cannot close article, cannot find X link")
        mainPage.actions(xpath("//*[contains(@text, 'Search Wikipedia')]"),
                WebElement::click)
        mainPage.actions(xpath("//*[contains(@text, 'Search…')]"),
                { it.sendKeys(secondArticleName) })
        mainPage.actions(xpath("//*[@resource-id='org.wikipedia:id/page_list_item_container']//*[contains(@text, 'Programming language')]"),
                WebElement::click)

        val secondArticleTitle = mainPage.waitForElementAndGetAttribute(id("org.wikipedia:id/view_page_title_text"), attribute = "text")

        mainPage.actions(xpath("//*[@content-desc='More options']"), WebElement::click)
        mainPage.actions(xpath("//android.widget.ListView"))
        mainPage.actions(xpath("//*[@text='Add to reading list']"), WebElement::click,
                "Cannot find option to add article to reading list")
        mainPage.actions(xpath("//*[@resource-id='org.wikipedia:id/lists_container']"))
        mainPage.actions(xpath("//*[@text='$nameOfArticlesList']"), WebElement::click,
                "Cannot find option to add article to reading list")
        mainPage.actions(xpath("//android.widget.ImageButton[@content-desc='Navigate up']"), WebElement::click,
                "Cannot close article, cannot find X link")
        mainPage.actions(xpath("//android.widget.FrameLayout[@content-desc='My lists']"), WebElement::click,
                "Cannot find navigation button to My lists")
        mainPage.actions(xpath("//*[@text='$nameOfArticlesList']"), WebElement::click,
                "Cannot find created folder")

        var savedArticleList = mainPage.getListViewElement(xpath("//android.widget.ScrollView"),
                id("org.wikipedia:id/page_list_item_description"))

        assertThat(savedArticleList.size, equalTo(2))

        mainPage.swipeElementToLeft(xpath("//*[@text='Java']"), "Cannot find saved article")
        savedArticleList = mainPage.getListViewElement(xpath("//android.widget.ScrollView"),
                id("org.wikipedia:id/page_list_item_description"))

        assertThat(savedArticleList.size, equalTo(1))
        assertThat("Cannot find article in saved list",
                driver, should(canFindElement(xpath("//*[@text='Kotlin (programming language)']"))))

        mainPage.actions(xpath("//*[@text='Kotlin (programming language)']"), WebElement::click)
        val currentTitle = mainPage.waitForElementAndGetAttribute(id("org.wikipedia:id/view_page_title_text"), attribute = "text", errorMassage = "Cannot find title of article")

        assertThat("Title of article not same", secondArticleTitle, should(equalTo(currentTitle)))
    }

    /**
     * Ex6: Тест: assert title
     *
     * Написать тест, который открывает статью и убеждается, что у нее есть элемент title.
     * Важно: тест не должен дожидаться появления title, проверка должна производиться сразу.
     * Если title не найден - тест падает с ошибкой. Метод можно назвать assertElementPresent.
     */
    @Test
    fun articleShouldHaveTitleWithQuickFind() {
        val searchLine = "Java"
        mainPage.actions(xpath("//*[contains(@text, 'Search Wikipedia')]"),
                WebElement::click)
        mainPage.actions(xpath("//*[contains(@text, 'Search…')]"),
                { it.sendKeys(searchLine) })
        mainPage.actions(xpath("//*[@resource-id='org.wikipedia:id/page_list_item_container']//*[contains(@text, 'Island of Indonesia')]"),
                WebElement::click)
        mainPage.assertElementPresent(id("org.wikipedia:id/view_page_title_text"), "Article doesn't have title")
    }
}

