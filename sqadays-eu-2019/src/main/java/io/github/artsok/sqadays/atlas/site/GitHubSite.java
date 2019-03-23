package io.github.artsok.sqadays.atlas.site;

import io.github.artsok.sqadays.atlas.page.MainPage;
import io.github.artsok.sqadays.atlas.page.ProjectPage;
import io.github.artsok.sqadays.atlas.page.SearchPage;
import io.qameta.atlas.webdriver.WebSite;
import io.qameta.atlas.webdriver.extension.Page;
import io.qameta.atlas.webdriver.extension.Path;
import io.qameta.atlas.webdriver.extension.Query;

/**
 * Point of testing WebSite.
 */
public interface GitHubSite extends WebSite {

    @Page
    MainPage onMainPage();

    @Page(url = "search")
    SearchPage onSearchPage(@Query("q") String value);

    @Page(url = "{profile}/{project}/tree/master/")
    ProjectPage onProjectPage(@Path("profile") String profile, @Path("project") String project);

}
