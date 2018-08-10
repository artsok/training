package io.github.artsok.training.rules

import io.appium.java_client.MobileElement
import io.appium.java_client.android.AndroidDriver
import org.junit.rules.ExternalResource
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import org.openqa.selenium.ScreenOrientation
import org.openqa.selenium.remote.DesiredCapabilities
import java.io.File
import java.net.URL

lateinit var driver: AndroidDriver<MobileElement>

class DriverRule(private val file: File) : ExternalResource()  {
    override fun before() {
        val url = URL("http://127.0.0.1:4723/wd/hub")
        val desiredCapabilities = DesiredCapabilities().apply {
            setCapability("platformName", "Android")
            setCapability("deviceName", "Nexus 6P API 27")
            setCapability("platformVersion", "8.1")
            setCapability("appPackage", "org.wikipedia")
            setCapability("appActivity", ".main.MainActivity")
            setCapability("unicodeKeyboard", true)
            setCapability("resetKeyboard", true)
            setCapability("newCommandTimeout", 600 * 5);
            setCapability("app", file.absoluteFile)
            setCapability("automationName", "UiAutomator2")
        }
        driver = AndroidDriver(url, desiredCapabilities)
    }

    override fun after() {
        if (driver != null) {
            driver.quit()
        }
    }

    fun getDriver(): AndroidDriver<MobileElement> {
        return driver
    }
}

/**
 * Appium устроен так, что может сохранить у себя в памяти поворот экрана, который использовался в предыдущем тесте,
 * и начать новый тест с тем же поворотом. Мы написали тест на поворот экрана, и он может сломаться до того,
 * как положение экрана восстановится.
 * Следовательно, если мы запустим несколько тестов одновременно, последующие тесты будут выполняться в неправильном
 * положении экрана, что может привести к незапланированным проблемам.
 *
 * Как нам сделать так, чтобы после теста на поворот экрана сам экран всегда оказывался в правильном положении,
 * даже если тест упал в тот момент, когда экран был наклонен?
 */
class RotateRule() : TestRule {
    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                driver.rotate(ScreenOrientation.PORTRAIT) //Установка в Rule нужного положения
                base.evaluate()
            }
        }
    }
}
