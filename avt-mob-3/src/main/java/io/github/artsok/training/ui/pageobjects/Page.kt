package io.github.artsok.training.ui.pageobjects

import io.appium.java_client.AppiumDriver
import io.appium.java_client.android.AndroidTouchAction
import io.appium.java_client.pagefactory.AppiumFieldDecorator
import io.appium.java_client.touch.WaitOptions
import io.appium.java_client.touch.offset.ElementOption
import io.appium.java_client.touch.offset.PointOption
import org.openqa.selenium.By
import org.openqa.selenium.Dimension
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.PageFactory
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration

abstract class Page(protected val driver: AppiumDriver<*>) {

    init {
        PageFactory.initElements(AppiumFieldDecorator(driver), this)
    }

    /**
     * Check that element present
     */
    fun assertElementPresent(by: By, errorMassage: String) {
        val element = driver.findElements(by)
        if (element.isEmpty()) {
            throw AssertionError("An element '$by' supposed to be present. $errorMassage")
        }
    }

    /**
     * Swipe Left
     */
    fun swipeElementToLeft(by: By, errorMassage: String) {
        val webElement = actions(by, errorMassage = errorMassage)
        //1. Нужно обнаружить этот элемент
        //2. Установить его место положения по осям х и у
        //3. Далее передвинуть его по оси Х справа на лева
        //4. По оси y движение происходить не будет
        val leftX = webElement.location.x //Запишим самую левую точку элемента по оси Х
        val rightX = leftX + webElement.size.width //Берем ранее найденную точку элемента 'leftX', берем размер нашего элемента по ширине и прибавляем к нашей точке 'leftX'. Получаем точку которая находится у правой границы экрана
        val upperY = webElement.location.y
        val lowerY = upperY + webElement.size.height
        val middleY = (upperY + lowerY) / 2 //Таким образом получаем самую верхнию точку нашего элемента, самую нижнию, складываем их, делим на два и получаем середину нашего элемента по оси Y

        val touchAction = AndroidTouchAction(driver)
        touchAction
                .press(PointOption<ElementOption>().withCoordinates(rightX, middleY))
                .waitAction(WaitOptions.waitOptions(Duration.ofMillis(300)))
                .moveTo(PointOption<ElementOption>().withCoordinates(leftX, middleY))
                .release()
                .perform()

    }

    /**
     * Down swipe
     */
    fun swipeDown(waitSeconds: Long = 3) {
        val (width, height) = driver.manage().window().size
        val startX = width / 2
        val startY = (height * 0.80).toInt()
        val endX = width / 2
        val endY = (height * 0.20).toInt()
        val touchAction = AndroidTouchAction(driver)
        touchAction
                .press(PointOption<ElementOption>().withCoordinates(startX, startY))
                .waitAction(WaitOptions.waitOptions(Duration.ofSeconds(waitSeconds)))
                .moveTo(PointOption<ElementOption>().withCoordinates(endX, endY))
                .release()
                .perform()
    }

    /**
     * Down swipe to element
     */
    fun swipeToElement(by: By, depthOfSwiped: Int = 10, errorMassage: String = "error") {
        var alreadySwiped = 0
        while (driver.findElements(by).size == 0) {
            if (alreadySwiped > depthOfSwiped) {
                actions(by, errorMassage = "Cannot find element by swipping up '$errorMassage'")
                return
            }
            swipeDown(0)
            alreadySwiped = alreadySwiped.inc()
        }
    }

    /**
     * Return list of web elements
     */
    fun getListViewElement(listBy: By, listElementBy: By, timeOut: Long = 5): List<WebElement> {
        val listView = WebDriverWait(driver, timeOut).until(ExpectedConditions.presenceOfElementLocated(listBy))
        return listView.findElements<WebElement>(listElementBy);
    }

    /**
     * Checking that an web element is present on the DOM of a page and then make actions with it
     */
    fun actions(by: By, function: ((WebElement) -> Unit)? = null, errorMassage: String = "Can't find element '$by'", timeOut: Long = 5): WebElement {
        val driverWait = WebDriverWait(driver, timeOut).withMessage("$errorMassage\n")
        val element = driverWait.until(ExpectedConditions.presenceOfElementLocated(by))
        val action = function ?: return element
        action.invoke(element)
        return element
    }

    /**
     * Check if web element is present or not
     */
    fun waitForElementNotPresent(by: By, errorMessage: String, timeOut: Long): Boolean {
        val driverWait = WebDriverWait(driver, timeOut).withMessage("$errorMessage\n")
        return driverWait.until(ExpectedConditions.invisibilityOfElementLocated(by))
    }

    /**
     * Search element by and get attribute
     */
    fun waitForElementAndGetAttribute(by: By, attribute: String, errorMassage: String = "Error in waitForElementAndGetAttribute", timeOut: Long = 5): String {
        val element = actions(by, errorMassage = errorMassage, timeOut = timeOut)
        return element.getAttribute(attribute)
    }

    /**
     * MultiDeclaration for Dimension.width
     */
    private operator fun Dimension.component1(): Int {
        return this.width
    }

    /**
     * MultiDeclaration for Dimension.height
     */
    private operator fun Dimension.component2(): Int {
        return this.height
    }
}

