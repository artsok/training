package io.github.artsok.training.ui.pageobjects.element

import io.appium.java_client.AppiumDriver
import io.appium.java_client.MobileElement
import io.appium.java_client.pagefactory.AndroidFindBy
import io.github.artsok.training.ui.pageobjects.Page
import org.openqa.selenium.By

class ToolbarElement(driver: AppiumDriver<*>) : Page(driver) {

    init {
        actions(By.id("org.wikipedia:id/page_toolbar"))
    }

    @AndroidFindBy(xpath = "//android.widget.ImageButton[@content-desc='Navigate up']")
    lateinit var navigateUpBtn : MobileElement
}