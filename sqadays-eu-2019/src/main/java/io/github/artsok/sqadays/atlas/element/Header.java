package io.github.artsok.sqadays.atlas.element;


import io.qameta.atlas.webdriver.AtlasWebElement;
import io.qameta.atlas.webdriver.extension.FindBy;

/**
 * Header of web page.
 */
public interface Header extends AtlasWebElement {

    @FindBy(".//input[contains(@class,'header-search-input')]")
    AtlasWebElement searchInput();

}


