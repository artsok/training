package io.github.artsok.training.ui.pageobjects

import io.appium.java_client.AppiumDriver
import io.appium.java_client.MobileElement
import io.appium.java_client.pagefactory.WithTimeout
import io.appium.java_client.pagefactory.iOSFindBy
import io.github.artsok.training.utils.exist
import io.github.artsok.training.utils.lateClick
import java.time.temporal.ChronoUnit

class WelcomePage(driver: AppiumDriver<*>) : Page(driver) {

    @iOSFindBy(id = "Learn more about Wikipedia")
    @WithTimeout(time = 10, chronoUnit = ChronoUnit.SECONDS)
    private lateinit var welcomeTextLink: MobileElement

    @iOSFindBy(id = "Next")
    private lateinit var nextBtn: MobileElement

    @iOSFindBy(id = "Skip")
    private lateinit var skipBtn: MobileElement

    @iOSFindBy(id = "New ways to explore")
    @WithTimeout(time = 10, chronoUnit = ChronoUnit.SECONDS)
    private lateinit var exploreText: MobileElement

    @iOSFindBy(id = "Add or edit preferred languages")
    @WithTimeout(time = 10, chronoUnit = ChronoUnit.SECONDS)
    private lateinit var preferredLanguagesText: MobileElement

    @iOSFindBy(id = "Learn more about data collected")
    @WithTimeout(time = 10, chronoUnit = ChronoUnit.SECONDS)
    private lateinit var learnText: MobileElement

    @iOSFindBy(id = "Get started")
    @WithTimeout(time = 10, chronoUnit = ChronoUnit.SECONDS)
    private lateinit var getStartedBtn: MobileElement

    fun skipWelcomeText() {
        skipBtn.lateClick("Cant find Skip Button")
    }

    /**
     * Ознакомиться с первой страницей приветствия
     */
    fun readWelcomeTextAndClick() {
        welcomeTextLink.exist()
        nextBtn.lateClick()
    }

    /**
     * Ознакомиться со второй страницей приветствия
     */
    fun readExploreTextAndClick() {
        exploreText.exist()
        nextBtn.lateClick()

    }

    /**
     * Ознакомиться с третьей страницей приветствия
     */
    fun readPreferredLanguagesTextAndClick() {
        preferredLanguagesText.exist()
        nextBtn.lateClick()
    }

    /**
     * Ознакомиться с четвертой страницей приветствия
     */
    fun readLearnTextAndClick() {
        learnText.exist()
        getStartedBtn.lateClick()
    }


}