package io.github.artsok.training.ui.pageobjects

import io.appium.java_client.AppiumDriver
import org.openqa.selenium.By.xpath
import org.openqa.selenium.WebElement

class MyListPage(driver: AppiumDriver<*>) : Page(driver) {

    private val folderTPL = "//*[@text='%s']"

    /**
     * Open folder with special name
     */
    fun openFolderByName(folderName: String) {
        actions(xpath(folderTPL.format(folderName)),
                WebElement::click, "Cannot find folder by folderName '$folderName'")
    }

    fun swipeArticleToDelete(articleTitle: String) {
        swipeElementToLeft(xpath(folderTPL.format(articleTitle)), "Cannot find saved article '$articleTitle'")
    }
}