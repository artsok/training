package io.github.artsok.training.ui.pageobjects.element

import io.appium.java_client.AppiumDriver
import io.appium.java_client.MobileElement
import io.appium.java_client.pagefactory.AndroidFindBy
import io.github.artsok.training.ui.pageobjects.Page
import org.openqa.selenium.By

class ActionBarRootElement(driver: AppiumDriver<*>) : Page(driver) {

    init {
        actions(By.id("org.wikipedia:id/action_bar_root"))
    }

    @AndroidFindBy(id = "org.wikipedia:id/text_input")
    lateinit var textInput : MobileElement

    @AndroidFindBy(xpath = "//*[@text='OK']")
    lateinit var okBtn : MobileElement


}