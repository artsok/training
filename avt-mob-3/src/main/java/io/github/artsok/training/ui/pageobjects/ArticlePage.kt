package io.github.artsok.training.ui.pageobjects

import io.appium.java_client.AppiumDriver
import io.appium.java_client.MobileElement
import io.appium.java_client.pagefactory.AndroidFindBy
import io.github.artsok.training.ui.pageobjects.element.ActionBarRootElement
import io.github.artsok.training.ui.pageobjects.element.ListViewElement
import io.github.artsok.training.ui.pageobjects.element.OnBoardingElement
import io.github.artsok.training.ui.pageobjects.element.ToolbarElement
import io.github.artsok.training.utils.lateClick
import io.github.artsok.training.utils.lateSendKeys
import org.openqa.selenium.By

class ArticlePage(driver: AppiumDriver<*>) : Page(driver) {

    private var footerElement = "//*[@text='View page in browser']"

    @AndroidFindBy(id = "org.wikipedia:id/view_page_title_text")
    lateinit var articleTitle: MobileElement

    @AndroidFindBy(xpath = "//*[@content-desc='More options']")
    lateinit var moreOptionsBtn: MobileElement

    /**
     * Get article title
     * @return text attribute of element
     */
    fun getArticleTitle(): String {
        return articleTitle.getAttribute("text")
    }

    /**
     * Swipe to footer element
     */
    fun swipeToFooter() {
        swipeToElement(By.xpath(footerElement), 12, "Cannot find the end of the article")
    }

    /**
     * Add article to my favorite list
     */
    fun addArticleToMyList(nameOfRootList: String) {
        moreOptionsBtn.lateClick()

        val listView = ListViewElement(driver)
        listView.selectElementWithText("Add to reading list")

        val onBoardingElement = OnBoardingElement(driver)
        onBoardingElement.onBoardingBtn.lateClick()

        val actionBarRootElement = ActionBarRootElement(driver)
        actionBarRootElement.textInput.clear()
        actionBarRootElement.textInput.lateSendKeys(nameOfRootList)
        actionBarRootElement.okBtn.lateClick()
    }

    /**
     * Close article
     */
    fun closeArticle() {
        val toolBarElement = ToolbarElement(driver)
        toolBarElement.navigateUpBtn.lateClick("Cannot close article, cannot find X link")
    }
}