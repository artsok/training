package io.github.artsok.training.ui.pageobjects

import io.appium.java_client.AppiumDriver
import org.openqa.selenium.By
import org.openqa.selenium.By.xpath
import org.openqa.selenium.WebElement

class MyListPage(driver: AppiumDriver<*>) : Page(driver) {

    /**
     * Open folder with special name
     */
    fun openFolderByName(folderName: String) {
        actions(xpath(textTPL.format(folderName)),
                WebElement::click, "Cannot find folder by folderName '$folderName'")
    }

    fun swipeArticleToDelete(articleTitle: String) {
        swipeElementToLeft(xpath(textTPL.format(articleTitle)), "Cannot find saved article '$articleTitle'")
    }

    /**
     * Get count of articles in favorite list
     */
    fun getAmountOfArticles() : Int {
        return getListViewElement(By.xpath("//android.widget.ScrollView"),
                By.id("org.wikipedia:id/page_list_item_description")).size
    }

    /**
     * Select article in my list
     */
    fun clickByArticlesInMyList(text:String) {
        actions(xpath(textTPL.format(text)), WebElement::click)
    }
}