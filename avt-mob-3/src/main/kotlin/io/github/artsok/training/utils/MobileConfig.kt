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

    @Key("appium.capability.platformName")
    fun platformName(): String

    @Key("appium.capability.newCommandTimeout")
    fun newCommandTimeout(): Int

    @Key("android.capability.deviceName")
    fun deviceName(): String

    @Key("android.capability.platformVersion")
    fun platformVersion(): String

    @Key("android.capability.appPackage")
    fun appPackage(): String

    @Key("android.capability.appActivity")
    fun appActivity(): String

    @Key("android.capability.unicodeKeyboard")
    fun unicodeKeyboard(): Boolean

    @Key("android.capability.resetKeyboard")
    fun resetKeyboard(): Boolean

    @Key("android.capability.automationName")
    fun automationName(): String

    @ConverterClass(FileConverter::class)
    @Key("android.capability.apk")
    fun apkFile(): File

    @Key("ios.capability.deviceName")
    fun deviceIOSName(): String

    @Key("ios.capability.platformVersion")
    fun platformIOSVersion(): String

    @ConverterClass(FileConverter::class)
    @Key("ios.capability.app")
    fun appFile(): File
}


/**
 * Class to convert apk name to file
 */
class FileConverter : Converter<File> {
    override fun convert(method: Method, apkName: String): File {
        return File("src/test/resources/apk/$apkName").absoluteFile
    }
}
