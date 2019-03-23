package io.github.artsok.sqadays.atlas.element;

import io.qameta.atlas.webdriver.AtlasWebElement;
import io.qameta.atlas.webdriver.extension.FindBy;

public interface RepositoryCard extends AtlasWebElement<RepositoryCard> {

    @FindBy(".//h3")
    AtlasWebElement title();

}
