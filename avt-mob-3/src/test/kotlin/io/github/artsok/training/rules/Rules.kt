package io.github.artsok.training.rules

import io.appium.java_client.MobileElement
import io.appium.java_client.android.AndroidDriver
import io.github.artsok.training.MobileConfig
import org.aeonbits.owner.ConfigFactory
import org.junit.rules.ExternalResource
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import org.openqa.selenium.ScreenOrientation
import org.openqa.selenium.remote.DesiredCapabilities
import java.net.URL


lateinit var driver: AndroidDriver<MobileElement>

class DriverRule : ExternalResource() {

    private val config: MobileConfig = ConfigFactory.create(MobileConfig::class.java)

    override fun before() {
        val url = URL(config.url())
        val desiredCapabilities = DesiredCapabilities().apply {
            setCapability("platformName", config.platformName())
            setCapability("deviceName", config.deviceName())
            setCapability("platformVersion", config.platformVersion())
            setCapability("appPackage", config.appPackage())
            setCapability("appActivity", config.appActivity())
            setCapability("unicodeKeyboard", config.unicodeKeyboard())
            setCapability("resetKeyboard", config.resetKeyboard())
            setCapability("automationName", config.automationName())
            setCapability("newCommandTimeout", config.newCommandTimeout());
            setCapability("app", config.apkFile())
        }
        driver = AndroidDriver(url, desiredCapabilities)
    }

    override fun after() {
        driver.quit()
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
