package io.github.artsok.sqadays;

import io.github.bonigarcia.seljup.SeleniumExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

@ExtendWith(SeleniumExtension.class)
class VanillaSeleniumTest {

    @Test
    void simpleVanillaTest(ChromeDriver driver)  {
        final Wait<WebDriver> wait = new WebDriverWait(driver, 5, 1000);
        driver.get("https://github.com");
        WebElement searchInput = wait.until(ExpectedConditions
                .visibilityOf(driver.findElement(By.xpath("//header[contains(@class,'Header')]//input[contains(@class,'header-search-input')]"))));
        searchInput.sendKeys("Atlas");
        searchInput.submit();

        List<WebElement> repositories = wait.until(ExpectedConditions
                .visibilityOfAllElements(driver.findElements(By.xpath(".//ul[contains(@class, 'repo-list')]//li[contains(@class, 'repo-list-item')]//h3"))));
        Assertions.assertEquals(10, repositories.size());
    }
}
