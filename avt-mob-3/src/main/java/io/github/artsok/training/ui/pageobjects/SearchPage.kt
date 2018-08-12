package io.github.artsok.training.ui.pageobjects

import io.appium.java_client.AppiumDriver
import io.appium.java_client.MobileElement
import io.appium.java_client.pagefactory.AndroidFindBy


class SearchPage(driver: AppiumDriver<*>) : Page(driver) {

    //TPL = Template
    var searchResultTPL = "//*[@resource-id='org.wikipedia:id/page_list_item_container']" +
            "//*[contains(@text, '%s')]"
    set(value) {
        field = field.format(value)
    }


    @AndroidFindBy(xpath = "//*[contains(@text, 'Search…')]")
    lateinit var searchInput: MobileElement
}

