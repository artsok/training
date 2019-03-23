package io.github.artsok.sqadays;

import io.github.artsok.sqadays.htmelements.pages.MainPage;
import io.github.artsok.sqadays.htmelements.pages.SearchPage;
import io.github.bonigarcia.seljup.SeleniumExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

@ExtendWith(SeleniumExtension.class)
public class PageElementWayTest {

    @Test
    void simpleTestWithPageElementPattern(ChromeDriver driver)  {
        MainPage mainPage = new MainPage(driver);
        mainPage.openPage().getHeader().getSearch().sendKeys("Atlas");
        mainPage.getHeader().getSearch().submit();
        SearchPage searchPage = new SearchPage(driver);
        assertThat(searchPage.getRepositories(), hasSize(10));
    }
}
