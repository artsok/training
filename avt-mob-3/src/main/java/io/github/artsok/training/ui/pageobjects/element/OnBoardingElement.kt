package io.github.artsok.training.ui.pageobjects.element

import io.appium.java_client.AppiumDriver
import io.appium.java_client.MobileElement
import io.appium.java_client.pagefactory.AndroidFindBy
import io.github.artsok.training.ui.pageobjects.Page
import org.openqa.selenium.By.id

class OnBoardingElement(driver: AppiumDriver<*>) : Page(driver) {

    init {
        actions(id("org.wikipedia:id/onboarding_container"))
    }

    @AndroidFindBy(id = "org.wikipedia:id/onboarding_button")
    lateinit var onBoardingBtn : MobileElement


}