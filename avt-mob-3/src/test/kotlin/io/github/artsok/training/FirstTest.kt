package io.github.artsok.training


import io.appium.java_client.MobileElement
import io.appium.java_client.android.AndroidDriver
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.openqa.selenium.By
import org.openqa.selenium.By.id
import org.openqa.selenium.By.xpath
import org.openqa.selenium.WebElement
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import ru.yandex.qatools.matchers.decorators.MatcherDecorators.should
import ru.yandex.qatools.matchers.decorators.TimeoutWaiter.timeoutHasExpired
import ru.yandex.qatools.matchers.decorators.WaiterMatcherDecorator.decorateMatcherWithWaiter
import ru.yandex.qatools.matchers.webdriver.ExistsMatcher.exists
import ru.yandex.qatools.matchers.webdriver.driver.CanFindElementMatcher.canFindElement
import java.io.File
import java.net.URL

class FirstTest {

    lateinit var apkFile: File
    lateinit var driver: AndroidDriver<MobileElement>

    @Before
    fun setUp() {
        apkFile = File(this.javaClass.getResource("/apk/org.wikipedia.apk").file)

        val capabilities = DesiredCapabilities().apply {
            setCapability("platformName", "Android")
            setCapability("deviceName", "Nexus 6P API 27")
            setCapability("platformVersion", "8.1")
            setCapability("appPackage", "org.wikipedia")
            setCapability("appActivity", ".main.MainActivity")
            setCapability("app", apkFile.absoluteFile) //Можно также ссылкой. Пример: "http://appium.s3.amazonaws.com/TestApp6.0.app.zip"
        }
        driver = AndroidDriver(URL("http://127.0.0.1:4723/wd/hub"), capabilities)
    }

    @Test
    fun shouldFindSpecialWordInSearchResultList() {
        actions(xpath("//*[contains(@text, 'Search Wikipedia')]"),
                WebElement::click,
                "Can't find Search Wikipedia input")
        actions(xpath("//*[contains(@text, 'Search…')]"),
                { element: WebElement -> element.sendKeys("Java") })
        assertThat(driver, decorateMatcherWithWaiter(
                canFindElement(By.xpath("//*[@resource-id='org.wikipedia:id/page_list_item_container']" +
                        "//*[contains(@text, 'Object-oriented programming language')]")), timeoutHasExpired(5000L)))
    }

    @Test
    fun searchCloseBtnShouldNotExistOnMainPage() {
        actions(xpath("//*[contains(@text, 'Search Wikipedia')]"),
                WebElement::click,
                "Can't find Search Wikipedia input")
        actions(xpath("//*[contains(@text, 'Search…')]"),
                { element: WebElement -> element.sendKeys("Allure") })
        actions(id("org.wikipedia:id/search_src_text"),
                { element: WebElement -> element.clear() })
        val searchCloseBtn = actions(id("search_close_btn"),
                WebElement::click,
                "Can't find Search Close Bottom")
        assertThat(searchCloseBtn, should(not(exists())))
    }

    @Test
    fun articleShouldHaveSpecialTitle() {
        actions(xpath("//*[contains(@text, 'Search Wikipedia')]"),
                WebElement::click,
                "Can't find Search Wikipedia input")
        actions(xpath("//*[contains(@text, 'Search…')]"),
                { element: WebElement -> element.sendKeys("Java") })
        actions(xpath("//*[@resource-id='org.wikipedia:id/page_list_item_container']//*[contains(@text, 'Object-oriented programming language')]"),
                WebElement::click,
                "Can't find element with text 'Object-oriented programming language'")
        val titleElement = actions(id("org.wikipedia:id/view_page_title_text"))
        val articleTitle = titleElement.getAttribute("text")
        assertThat(articleTitle, equalTo("Java (programming language)"))
    }

    /**
     * Написать функцию, которая проверяет наличие текста “Search…” в строке поиска перед вводом текста и помечает тест упавшим, если такого текста нет.
     */
    @Test
    fun specialWordShouldExistInSearchInput() {
        actions(xpath("//*[contains(@text, 'Search Wikipedia')]"),
                WebElement::click,
                "Can't find Search Wikipedia input")
        val searchElement = actions(xpath("//*[contains(@text, 'Search…')]"))
        assertThat ("Special Word 'Search…' is missed",
                searchElement.text, should(containsString("Search…")))
    }

    /**
     * Написать тест, который:
        1. Ищет какое-то слово
        2. Убеждается, что найдено несколько статей
        3. Отменяет поиск
        4. Убеждается, что результат поиска пропал
     */
    @Test
    fun afterCancelSearchesListShouldBeEmpty() {
        actions(xpath("//*[contains(@text, 'Search Wikipedia')]"),
                WebElement::click,
                "Can't find Search Wikipedia input")
        actions(xpath("//*[contains(@text, 'Search…')]"),
                { element: WebElement -> element.sendKeys("C++") })

        val searchResultsList = getListViewElement(id("org.wikipedia:id/search_results_list"),
                id("org.wikipedia:id/page_list_item_container"))
        assertThat("It's no results with you search text", searchResultsList.size, greaterThanOrEqualTo(1))

        actions(id("search_close_btn"),
                WebElement::click,
                "Can't find Search Close Bottom")
        assertThat("Searche list not empty", driver, should(not(canFindElement(id("org.wikipedia:id/search_results_list")))))
    }

    /**
     * Clear previous searchs
     */
    private fun clearRecentSearches() {
        actions(xpath("//*[contains(@text, 'Search Wikipedia')]"),
                WebElement::click)
    }


    /**
     * Return list of web elements
     */
    private fun getListViewElement(listBy: By, listElementBy:By, timeOut:Long = 5) :List<WebElement> {
        val listView = WebDriverWait(driver, timeOut).until(ExpectedConditions.presenceOfElementLocated(listBy))
        return listView.findElements<WebElement>(listElementBy);
    }

    /**
     * Checking that an web element is present on the DOM of a page and then make actions with it
     */
    private fun actions(by: By, function: ((WebElement) -> Unit)? = null, errorMassage: String = "Can't find element '$by'", timeOut: Long = 5): WebElement {
        val driverWait = WebDriverWait(driver, timeOut).withMessage("$errorMassage\n")
        val element = driverWait.until(ExpectedConditions.presenceOfElementLocated(by))
        val action = function ?: return element
        action.invoke(element)
        return element
    }

    /**
     * Check if web element is present or not
     */
    private fun waitForElementNotPresent(by: By, errorMessage: String, timeOut: Long): Boolean {
        val driverWait = WebDriverWait(driver, timeOut).withMessage("$errorMessage\n")
        return driverWait.until(ExpectedConditions.invisibilityOfElementLocated(by))
    }

    @After
    fun tearDown() {
        if (driver != null) {
            driver.quit()
        }
    }
}
