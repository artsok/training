package io.github.artsok.sqadays.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;


public class SearchPage {

    private WebDriver driver;

    private static final String REPOSITORIES = ".//ul[contains(@class, 'repo-list')]//li[contains(@class, 'repo-list-item')]//h3";

    public SearchPage(WebDriver driver) {
        PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(driver)), this);
        this.driver = driver;
    }

    public List<WebElement> getRepositories() {
        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(10))
                .pollingEvery(Duration.ofSeconds(1))
                .ignoring(NoSuchElementException.class);
        wait.until(it ->
                !driver.findElements(By.xpath(REPOSITORIES)).isEmpty());

        return driver.findElements(By.xpath(REPOSITORIES));
    }
}
