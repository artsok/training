package io.github.artsok.sqadays.atlas.page;

import io.github.artsok.sqadays.atlas.layout.WithHeader;
import io.qameta.atlas.webdriver.AtlasWebElement;
import io.qameta.atlas.webdriver.WebPage;
import io.qameta.atlas.webdriver.extension.FindBy;

/**
 * Main page of site.
 */
public interface MainPage extends WebPage, WithHeader {

    @FindBy("//a[contains(text(), 'Or start a free trial of Enterprise Server')]")
    AtlasWebElement trial();

}
