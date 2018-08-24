package io.github.artsok.training.rules

import io.appium.java_client.AppiumDriver
import io.appium.java_client.MobileElement
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.ios.IOSDriver
import io.github.artsok.training.utils.MobileConfig
import org.aeonbits.owner.ConfigFactory
import org.junit.jupiter.api.extension.*
import org.openqa.selenium.ScreenOrientation
import org.openqa.selenium.remote.DesiredCapabilities
import java.net.URL


lateinit var driver: AppiumDriver<MobileElement>


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
class RotateCondition() : BeforeEachCallback {
    override fun beforeEach(context: ExtensionContext?) {
        driver.rotate(ScreenOrientation.PORTRAIT) //Установка в Rule нужного положения
    }
}


class DriverExtension : BeforeAllCallback, AfterAllCallback {

    private lateinit var desiredCapabilities: DesiredCapabilities
    private val config: MobileConfig = ConfigFactory.create(MobileConfig::class.java)

    /**
     * Тушим драйвер
     */
    override fun afterAll(context: ExtensionContext) {
        driver.quit()
    }

    /**
     * Запускаем драйвер перед всеми тестами.
     */
    override fun beforeAll(context: ExtensionContext) {
        val url = URL(config.url())
        val platform = config.platformName().trim()

        when {
            "android".equals(platform, true) -> {
                desiredCapabilities = DesiredCapabilities().apply {
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
            "ios".equals(platform, true) -> {
                desiredCapabilities = DesiredCapabilities().apply {
                    setCapability("platformName", config.platformName())
                    setCapability("deviceName", config.deviceIOSName())
                    setCapability("platformVersion", config.platformIOSVersion())
                    setCapability("newCommandTimeout", config.newCommandTimeout());
                    setCapability("app", config.appFile())
                }
                driver = IOSDriver(url, desiredCapabilities)
            }
            else -> throw IllegalArgumentException("Cannot run platfrom for unrecognize type. Only IOSTest and AndroidTest available!")
        }
    }

}


class DriverInjectResolver : ParameterResolver {

    /**
     * Проверяем тип объекта, который хотим внедрить в тест
     */
    override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean {
        val type = parameterContext.parameter.type
        return (AppiumDriver::class.java.isAssignableFrom(type))
    }

    /**
     *  Передаем драйвер в тест, если потребуется
     */
    override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any {
        return driver
    }

}
