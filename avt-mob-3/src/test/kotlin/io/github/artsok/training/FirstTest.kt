package io.github.artsok.training


import io.appium.java_client.MobileElement
import io.appium.java_client.android.AndroidDriver
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.not
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
        actions(xpath("//*[contains(@text, 'Search Wikipedia')]"), WebElement::click, "Can't find Search Wikipedia input")
        actions(xpath("//*[contains(@text, 'Search…')]"), { element: WebElement -> element.sendKeys("Java") })
        assertThat(driver, decorateMatcherWithWaiter(
                canFindElement(By.xpath("//*[@resource-id='org.wikipedia:id/page_list_item_container']" +
                        "//*[contains(@text, 'Object-oriented programming language')]")), timeoutHasExpired(5000L)))
    }

    @Test
    fun searchCloseBtnShouldNotExistOnMainPage() {
        actions(xpath("//*[contains(@text, 'Search Wikipedia')]"), WebElement::click, "Can't find Search Wikipedia input")
        actions(xpath("//*[contains(@text, 'Search…')]"), { element: WebElement -> element.sendKeys("Allure") })
        actions(id("org.wikipedia:id/search_src_text"), { element: WebElement -> element.clear() })
        val searchCloseBtn = actions(id("search_close_btn"), WebElement::click, "Can't find Search Close Bottom")
        assertThat(searchCloseBtn, should(not(exists())))
    }

    /**
     * Checking that an webelement is present on the DOM of a page and then make actions with it
     */
    private fun actions(by: By, function: (WebElement) -> Unit, errorMassage: String = "Can't find element", timeOut: Long = 5): WebElement {
        val driverWait = WebDriverWait(driver, timeOut).withMessage("$errorMassage\n")
        val element = driverWait.until(ExpectedConditions.presenceOfElementLocated(by))
        function.invoke(element)
        return element
    }

    /**
     * Check if webelement is present or not
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
