package io.github.artsok.training.utils

import io.appium.java_client.MobileElement
import io.github.artsok.training.rules.driver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait

/**
 * Click at the element with WebDriverWait condition
 * @param errorMassage message if element can't find
 * @param timeOut time to find element
 */
fun MobileElement.lateClick(errorMassage: String = "Can't find element '$this'", timeOut: Long = 5) {
    val driverWait = WebDriverWait(driver, timeOut).withMessage("$errorMassage\n")
    val element = driverWait.until(ExpectedConditions.visibilityOf(this))
    element.click()
}

fun MobileElement.lateSendKeys(text:String, errorMassage: String = "Can't find element '$this'", timeOut: Long = 5) {
    val driverWait = WebDriverWait(driver, timeOut).withMessage("$errorMassage\n")
    val element = driverWait.until(ExpectedConditions.visibilityOf(this))
    element.sendKeys(text)
}
