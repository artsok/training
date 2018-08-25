package io.github.artsok.training.ui.pageobjects

import io.appium.java_client.AppiumDriver
import io.appium.java_client.MobileElement
import io.appium.java_client.pagefactory.AndroidFindBy
import io.appium.java_client.pagefactory.iOSFindBy
import org.openqa.selenium.By
import org.openqa.selenium.WebElement


class SearchPage(driver: AppiumDriver<*>) : Page(driver) {

    @iOSFindBy(xpath = "//XCUIElementTypeSearchField[@value='Search Wikipedia']")
    @AndroidFindBy(xpath = "//*[contains(@text, 'Search…')]")
    lateinit var searchInput: MobileElement

    @iOSFindBy(id = "Close")
    @AndroidFindBy(id = "search_close_btn")
    lateinit var closeBtn: MobileElement


    @iOSFindBy(id = "//XCUIElementTypeStaticText[@name='No results found']")
    @AndroidFindBy(id = "//*[@text='o results found']")
    lateinit var emptyResultText : MobileElement

    //************************* Test
    @iOSFindBy(xpath = "//XCUIElementTypeLink[contains(@name, '%s')]")
    @AndroidFindBy(xpath = "//*[@resource-id='org.wikipedia:id/page_list_item_container']//*[contains(@text, '%s')]")
    lateinit var searchResultTestList:String
    //************************* Test

    private var searchResultsList = "org.wikipedia:id/search_results_list"

    private var pageListItemContainer = "org.wikipedia:id/page_list_item_container"

    /**
     * TPL = Template
     */
    var searchResultTPL = "//*[@resource-id='org.wikipedia:id/page_list_item_container']//*[contains(@text, '%s')]"
        set(value) {
            field = field.format(value)
        }

    private var searchResultWithDescription = "//android.widget.LinearLayout[android.widget.TextView[@text='%s'] and android.widget.TextView[@text='%s']]"


    fun clickByArticleWithSubString(subString: String) {
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

    /**
     * Wait article with title and description
     */
    fun waitForElementByTitleAndDescription(title: String, description: String) {
        actions(By.xpath(searchResultWithDescription.format(title, description)),
                errorMassage = "Cannot find and click search result with title '$title' and description '$description'")
    }

}

