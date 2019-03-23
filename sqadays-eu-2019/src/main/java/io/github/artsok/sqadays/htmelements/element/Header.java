package io.github.artsok.sqadays.htmelements.element;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.HtmlElement;

public class Header extends HtmlElement {

    @FindBy(xpath = ".//input[contains(@class,'header-search-input')]")
    WebElement search;

    public WebElement getSearch() {
        return search;
    }
}
