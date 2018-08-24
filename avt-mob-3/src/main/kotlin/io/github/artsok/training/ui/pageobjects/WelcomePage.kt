package io.github.artsok.training.ui.pageobjects

import io.appium.java_client.AppiumDriver
import io.appium.java_client.MobileElement
import io.appium.java_client.pagefactory.WithTimeout
import io.appium.java_client.pagefactory.iOSFindBy
import java.time.temporal.ChronoUnit

class WelcomePage(driver: AppiumDriver<*>) : Page(driver) {

    @iOSFindBy(id = "Learn more about Wikipedia")
    @WithTimeout(time = 10, chronoUnit = ChronoUnit.SECONDS)
    lateinit var welcomeTextLink: MobileElement

    @iOSFindBy(id = "Next")
    lateinit var nextBtn: MobileElement

    @iOSFindBy(id = "New ways to explore")
    @WithTimeout(time = 10, chronoUnit = ChronoUnit.SECONDS)
    lateinit var exploreText: MobileElement

    @iOSFindBy(id = "Add or edit preferred languages")
    @WithTimeout(time = 10, chronoUnit = ChronoUnit.SECONDS)
    lateinit var preferredLanguagesText: MobileElement

    @iOSFindBy(id = "Learn more about data collected")
    @WithTimeout(time = 10, chronoUnit = ChronoUnit.SECONDS)
    lateinit var learnText: MobileElement

    @iOSFindBy(id = "Get started")
    @WithTimeout(time = 10, chronoUnit = ChronoUnit.SECONDS)
    lateinit var getStartedBtn: MobileElement

}