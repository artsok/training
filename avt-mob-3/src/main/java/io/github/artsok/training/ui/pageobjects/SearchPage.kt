package io.github.artsok.training.ui.pageobjects

import io.appium.java_client.AppiumDriver
import io.appium.java_client.MobileElement
import io.appium.java_client.pagefactory.AndroidFindBy
import org.openqa.selenium.By
import org.openqa.selenium.WebElement


class SearchPage(driver: AppiumDriver<*>) : Page(driver) {

    private var searchResultsList = "org.wikipedia:id/search_results_list"

    private var pageListItemContainer = "org.wikipedia:id/page_list_item_container"

    /**
     * TPL = Template
     */
    var searchResultTPL = "//*[@resource-id='org.wikipedia:id/page_list_item_container']//*[contains(@text, '%s')]"
    set(value) {
        field = field.format(value)
    }

    @AndroidFindBy(xpath = "//*[contains(@text, 'Search…')]")
    lateinit var searchInput: MobileElement

    @AndroidFindBy(id = "search_close_btn")
    lateinit var closeBtn: MobileElement


    fun clickByArticleWithSubString(subString:String) {
        actions(By.xpath(searchResultTPL.format(subString)),
                WebElement::click,
                errorMassage = "Cannot find and click search result with substring $subString")
    }

    /**
     * Get result founded list with articles
     */
    fun getFoundArticles(): List<WebElement> {
        return getListViewElement(By.id(searchResultsList), By.id(pageListItemContainer))
    }

}

