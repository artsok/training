package io.github.artsok.sqadays.atlas.page;

import io.github.artsok.sqadays.atlas.element.RepositoryCard;
import io.github.artsok.sqadays.atlas.layout.WithHeader;
import io.qameta.atlas.webdriver.ElementsCollection;
import io.qameta.atlas.webdriver.WebPage;
import io.qameta.atlas.webdriver.extension.FindBy;

public interface SearchPage extends WebPage, WithHeader {

    @FindBy(".//ul[contains(@class, 'repo-list')]//li[contains(@class, 'repo-list-item')]")
    ElementsCollection<RepositoryCard> repositories();

}
