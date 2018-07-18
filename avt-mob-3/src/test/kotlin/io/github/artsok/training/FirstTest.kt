package io.github.artsok.training

import io.appium.java_client.MobileElement
import io.appium.java_client.android.AndroidDriver
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
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
    fun shouldOpenWikApplication() {
        val findBt = driver.findElement(By.xpath("//*[contains(@text, 'Search Wikipedia')]"))
        findBt.click()
        val searchInput = waitElement(By.xpath("//*[contains(@text, 'Search…')]"), timeOut = 5)
        searchInput.sendKeys("Appium")
    }

    private fun waitElement(locator:By, errorMassage:String = "Can't find element", timeOut:Long): WebElement {
        val driverWait = WebDriverWait(driver, timeOut).withMessage("$errorMassage\n")
        return driverWait.until(ExpectedConditions.presenceOfElementLocated(locator))
    }


    @After
    fun tearDown() {
        if (driver != null) {
            driver.quit()
        }
    }
}