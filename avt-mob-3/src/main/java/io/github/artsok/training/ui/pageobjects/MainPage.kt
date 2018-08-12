package io.github.artsok.training.ui.pageobjects

import io.appium.java_client.AppiumDriver
import io.appium.java_client.MobileElement
import io.appium.java_client.pagefactory.AndroidFindBy

class MainPage(driver: AppiumDriver<*>) : Page(driver) {

    @AndroidFindBy(xpath = "//*[contains(@text, 'Search Wikipedia')]")
    lateinit var searchWikipediaInputInit: MobileElement


}