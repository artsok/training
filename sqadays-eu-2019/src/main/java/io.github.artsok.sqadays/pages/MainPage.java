package io.github.artsok.sqadays.pages;

import io.github.artsok.sqadays.element.Header;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

public class MainPage {

    private final WebDriver driver;

    @FindBy(xpath = "//header[contains(@class,'Header')]")
    private Header header;

    public MainPage(WebDriver driver) {
        PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(driver)), this);
        this.driver = driver;
    }

    public MainPage openPage() {
        driver.get("https://github.com");
        return this;
    }

    public Header getHeader() {
        return header;
    }
}
