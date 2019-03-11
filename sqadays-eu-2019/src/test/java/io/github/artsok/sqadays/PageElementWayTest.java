package io.github.artsok.sqadays;

import io.github.artsok.sqadays.pages.MainPage;


import io.github.artsok.sqadays.pages.SearchPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

import org.junit.jupiter.api.extension.ExtendWith;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

//@ExtendWith(SeleniumExtension.class)
public class PageElementWayTest {

    WebDriver driver;
//
//    public PageElementWayTest(ChromeDriver driver) {
//        this.driver = driver;
//    }

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
    }

    @Test
    void simpleTestWithPageElementPattern()  {
        MainPage mainPage = new MainPage(driver);
        mainPage.openPage().getHeader().getSearch().sendKeys("Atlas");
        mainPage.getHeader().getSearch().submit();
        SearchPage searchPage = new SearchPage(driver);
        assertThat(searchPage.getRepositories(), hasSize(10));
    }

    @AfterEach
    void tearDown() {
        driver.close();
    }

}
