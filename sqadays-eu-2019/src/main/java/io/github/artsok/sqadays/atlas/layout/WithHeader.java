package io.github.artsok.sqadays.atlas.layout;

import io.github.artsok.sqadays.atlas.element.Header;
import io.qameta.atlas.webdriver.extension.FindBy;


public interface WithHeader {

    @FindBy("//header[contains(@class,'Header')]")
    Header header();

}
