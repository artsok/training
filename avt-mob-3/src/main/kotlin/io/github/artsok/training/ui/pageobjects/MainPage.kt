package io.github.artsok.training.ui.pageobjects

import io.appium.java_client.AppiumDriver
import io.appium.java_client.MobileElement
import io.appium.java_client.pagefactory.AndroidFindBy
import io.appium.java_client.pagefactory.iOSFindBy

class MainPage(driver: AppiumDriver<*>) : Page(driver) {

    @iOSFindBy(xpath = "//XCUIElementTypeSearchField[@name='Search Wikipedia']")
    @AndroidFindBy(xpath = "//*[contains(@text, 'Search Wikipedia')]")
    lateinit var searchWikipediaInputInit: MobileElement

    init {
        val platform = config.platformName().trim()
        if("ios".equals(platform, true)) {
            val welcomePage = WelcomePage(driver)
            welcomePage.skipWelcomeText()
        }
    }

}