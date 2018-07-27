package io.github.artsok.training


import io.appium.java_client.MobileElement
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.android.AndroidTouchAction
import io.appium.java_client.touch.WaitOptions.waitOptions
import io.appium.java_client.touch.offset.ElementOption
import io.appium.java_client.touch.offset.PointOption
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.openqa.selenium.By
import org.openqa.selenium.By.id
import org.openqa.selenium.By.xpath
import org.openqa.selenium.Dimension
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
import java.time.Duration.ofSeconds
import kotlin.text.RegexOption.IGNORE_CASE


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
            setCapability("unicodeKeyboard", true)
            setCapability("resetKeyboard", true)
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
     * Написать тест, который проверяет наличие текста “Search…” в строке поиска перед вводом текста
     * и помечает тест упавшим, если такого текста нет.
     */
    @Test
    fun specialWordShouldExistInSearchInput() {
        actions(xpath("//*[contains(@text, 'Search Wikipedia')]"),
                WebElement::click,
                "Can't find Search Wikipedia input")
        val searchElement = actions(xpath("//*[contains(@text, 'Search…')]"))
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
     * Написать тест, который:
     * Ищет какое-то слово
     * Убеждается, что в каждом результате поиска есть это слово.
     */
    @Test
    fun resultListShouldContainSpecialWordS() {
        actions(xpath("//*[contains(@text, 'Search Wikipedia')]"),
                WebElement::click,
                "Can't find Search Wikipedia input")
        actions(xpath("//*[contains(@text, 'Search…')]"),
                { element: WebElement -> element.sendKeys("Java") })
        assertThat("Java", should(containsInResultList()))
    }


    /**
     * III. Сложные тесты
     */

    /**
     * 1. Swipe: start_x, Touch action, dimensions
     */
    @Test
    fun articleShouldBeWithSwipeAction() {
        actions(xpath("//*[contains(@text, 'Search Wikipedia')]"),
                WebElement::click)
        actions(xpath("//*[contains(@text, 'Search…')]"),
                { element: WebElement -> element.sendKeys("Java") })
        actions(xpath("//*[@resource-id='org.wikipedia:id/page_list_item_container']//*[contains(@text, 'Object-oriented programming language')]"),
                WebElement::click)
        actions(id("org.wikipedia:id/view_page_title_text"))
        repeat(2) {
            downSwipe()
        }
    }


    /**
     * Custom matcher that match special word in each item of result list
     */
    private fun containsInResultList(): Matcher<String> {
        return object : TypeSafeMatcher<String>() {
            val swipeDepth = 256
            var errorItem: String = ""

            override fun describeTo(description: Description) {
                description.appendText("Item in Search List must contain 'Java/java'. Item with error '$errorItem'")
            }

            override fun matchesSafely(value: String): Boolean {
                repeat(swipeDepth) {
                    val searchResultsList = getListViewElement(id("org.wikipedia:id/search_results_list"),
                            id("org.wikipedia:id/page_list_item_title")).forEach {

                        val regex = """\bjava\b""".toRegex(IGNORE_CASE)
                        if (!regex.containsMatchIn(it.text)) {
                            errorItem = it.text
                            return false
                        }
                    }
                    downSwipe()
                    swipeDepth.dec()
                }
                return true
            }
        }
    }

    /**
     * Down swipe
     */
    private fun downSwipe(waitSeconds: Long = 3) {
        val (width, height) = driver.manage().window().size
        val startX = width / 2
        val startY = (height * 0.80).toInt()
        val endX = width / 2
        val endY = (height * 0.20).toInt()
        val touchAction = AndroidTouchAction(driver)
        touchAction.press(PointOption<ElementOption>().withCoordinates(startX, startY))
                .waitAction(waitOptions(ofSeconds(waitSeconds)))
                .moveTo(PointOption<ElementOption>().withCoordinates(endX, endY))
                .release().perform()


    }


    /**
     * Return list of web elements
     */
    private fun getListViewElement(listBy: By, listElementBy: By, timeOut: Long = 5): List<WebElement> {
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

/**
 * MultiDeclaration for Dimension.width
 */
private operator fun Dimension.component1(): Int {
    return this.width
}

/**
 * MultiDeclaration for Dimension.height
 */
private operator fun Dimension.component2(): Int {
    return this.height
}




