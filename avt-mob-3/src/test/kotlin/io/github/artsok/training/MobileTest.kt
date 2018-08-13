package io.github.artsok.training


import io.appium.java_client.MobileElement
import io.appium.java_client.android.AndroidDriver
import io.github.artsok.training.matchers.WikiMatchers
import io.github.artsok.training.rules.DriverRule
import io.github.artsok.training.rules.RotateRule
import io.github.artsok.training.ui.pageobjects.MainPage
import io.github.artsok.training.ui.pageobjects.SearchPage
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
import org.openqa.selenium.WebElement
import ru.yandex.qatools.matchers.decorators.MatcherDecorators.should
import ru.yandex.qatools.matchers.webdriver.driver.CanFindElementMatcher.canFindElement


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
        assertThat("Java", should(WikiMatchers(mainPage).containsInResultList())) //TODO: refactor -> Подумать, что делать со своим матчером
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

