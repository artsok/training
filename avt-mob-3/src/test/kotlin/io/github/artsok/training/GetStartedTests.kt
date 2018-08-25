package io.github.artsok.training

import io.appium.java_client.MobileElement
import io.appium.java_client.ios.IOSDriver
import io.github.artsok.training.rules.Driver
import io.github.artsok.training.rules.DriverResolver
import io.github.artsok.training.rules.IOSTest
import io.github.artsok.training.rules.Rotate
import io.github.artsok.training.ui.pageobjects.WelcomePage

@Driver
@Rotate
@DriverResolver
class GetStartedTests {

    @IOSTest
    fun `should Pass Through Welcome`(driver: IOSDriver<MobileElement>) {
        val mainPage = WelcomePage(driver)
        mainPage.readWelcomeTextAndClick()
        mainPage.readExploreTextAndClick()
        mainPage.readPreferredLanguagesTextAndClick()
        mainPage.readLearnTextAndClick()
    }
}