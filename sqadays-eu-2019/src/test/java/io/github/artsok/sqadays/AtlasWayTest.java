package io.github.artsok.sqadays;

import io.github.artsok.sqadays.atlas.site.GitHubSite;
import io.github.bonigarcia.seljup.SeleniumExtension;
import io.qameta.atlas.core.Atlas;
import io.qameta.atlas.webdriver.WebDriverConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.hamcrest.Matchers.hasSize;

@ExtendWith(SeleniumExtension.class)
class AtlasWayTest {

    @Test
    void simpleTestWithPageElementPattern(ChromeDriver driver) {
        Atlas atlas = new Atlas(new WebDriverConfiguration(driver, "https://github.com"));
        GitHubSite site = atlas.create(driver, GitHubSite.class);
        site.onSearchPage("Atlas").repositories().waitUntil(hasSize(10));
    }
}
