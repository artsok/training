package io.github.artsok.training.utils

import org.aeonbits.owner.Config
import org.aeonbits.owner.Config.*
import org.aeonbits.owner.Converter
import java.io.File
import java.lang.reflect.Method


/**
 * Read props from mobile.properties
 */
@Sources("classpath:mobile.properties")
interface MobileConfig : Config {

    @DefaultValue("http://127.0.0.1:4723/wd/hub")
    @Key("appium.url")
    fun url(): String

    @DefaultValue("Android")
    @Key("appium.capability.platformName")
    fun platformName(): String

    @Key("appium.capability.deviceName")
    fun deviceName(): String

    @Key("appium.capability.platformVersion")
    fun platformVersion(): String

    @Key("appium.capability.appPackage")
    fun appPackage(): String

    @Key("appium.capability.appActivity")
    fun appActivity(): String

    @Key("appium.capability.unicodeKeyboard")
    fun unicodeKeyboard(): Boolean

    @Key("appium.capability.resetKeyboard")
    fun resetKeyboard(): Boolean

    @Key("appium.capability.automationName")
    fun automationName(): String

    @Key("appium.capability.newCommandTimeout")
    fun newCommandTimeout(): Int

    @ConverterClass(FileConverter::class)
    @Key("appium.capability.apk")
    fun apkFile(): File
}


/**
 * Class to convert apk name to file
 */
class FileConverter : Converter<File> {
    override fun convert(method: Method, apkName: String): File {
        return File("src/test/resources/apk/$apkName").absoluteFile
    }
}
