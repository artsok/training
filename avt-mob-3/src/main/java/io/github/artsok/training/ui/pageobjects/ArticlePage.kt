package io.github.artsok.training.ui.pageobjects

import io.appium.java_client.AppiumDriver
import io.appium.java_client.MobileElement
import io.appium.java_client.pagefactory.AndroidFindBy
import org.openqa.selenium.By

class ArticlePage(driver: AppiumDriver<*>) : Page(driver) {

    private var footerElement = "//*[@text='View page in browser']"

    @AndroidFindBy(id = "org.wikipedia:id/view_page_title_text")
    lateinit var articleTitle: MobileElement

    /**
     * Get article title
     * @return text attribute of element
     */
    fun getArticleTitle() : String {
        return articleTitle.getAttribute("text")
    }

    /**
     * Swipe to footer element
     */
    fun swipeToFooter() {
        swipeToElement(By.xpath(footerElement), 12, "Cannot find the end of the article")
    }
}