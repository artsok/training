package io.github.artsok.training.ui.pageobjects

import io.appium.java_client.AppiumDriver
import org.openqa.selenium.By.xpath
import org.openqa.selenium.WebElement

class NavigationUIPage(driver: AppiumDriver<*>) : Page(driver) {

    private val myListLink = "//android.widget.FrameLayout[@content-desc='My lists']"

    fun clickMyList() {
        actions(xpath(myListLink), WebElement::click, "Cannot find navigation button to My lists")
    }
}