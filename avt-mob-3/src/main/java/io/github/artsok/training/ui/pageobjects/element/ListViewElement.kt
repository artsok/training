package io.github.artsok.training.ui.pageobjects.element

import io.appium.java_client.AppiumDriver
import io.github.artsok.training.ui.pageobjects.Page
import io.github.artsok.training.utils.lateClick
import org.openqa.selenium.By
import org.openqa.selenium.By.xpath
import org.openqa.selenium.WebElement

class ListViewElement(driver: AppiumDriver<*>) : Page(driver) {

    init {
        actions(By.xpath("//android.widget.ListView"))
    }

    /**
     * Click of element list which have special substring/text
     */
    fun selectElementWithText(text : String) {
        val elements : List<WebElement> = getListViewElement(xpath("//android.widget.ListView"),
                By.id("org.wikipedia:id/title"))
        elements.forEach {
            if(it.text == text) {
                it.lateClick()
                return
            }
        }
    }
}