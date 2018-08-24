package io.github.artsok.training.utils

import io.github.artsok.training.rules.driver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait

/**
 * Click at the element with WebDriverWait condition
 * @param errorMassage message if element can't find
 * @param timeOut time to find element
 */
fun WebElement.lateClick(errorMassage: String = "Can't find element '$this'", timeOut: Long = 5) {
    val driverWait = WebDriverWait(driver, timeOut).withMessage("$errorMassage\n")
    val element = driverWait.until(ExpectedConditions.visibilityOf(this))
    element.click()
}

fun WebElement.lateSendKeys(text: String, errorMassage: String = "Can't find element '$this'", timeOut: Long = 5) {
    val driverWait = WebDriverWait(driver, timeOut).withMessage("$errorMassage\n")
    val element = driverWait.until(ExpectedConditions.visibilityOf(this))
    element.sendKeys(text)
}

fun WebElement.wait(errorMassage: String = "Can't find element '$this'", timeOut: Long = 5) {
    val driverWait = WebDriverWait(driver, timeOut).withMessage("$errorMassage\n")
    val element = driverWait.until(ExpectedConditions.visibilityOf(this))
}

fun WebElement.getLateAttribute(nameOfAttribute: String, errorMassage: String = "Can't find element '$this'", timeOut: Long = 5): String {
    val driverWait = WebDriverWait(driver, timeOut).withMessage("$errorMassage\n")
    val element = driverWait.until(ExpectedConditions.visibilityOf(this))
    return element.getAttribute(nameOfAttribute)
}
