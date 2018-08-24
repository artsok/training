package io.github.artsok.training.matchers

import io.github.artsok.training.ui.pageobjects.MainPage
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.openqa.selenium.By

class WikiMatchers(val mainPage: MainPage) {


    /**
     * Custom matcher that match special word in each item of result list
     */

    fun containsInResultList(): Matcher<String> {
        return object : TypeSafeMatcher<String>() {
            var swipeDepth = 256
            var errorItem: String = ""

            override fun describeTo(description: Description) {
                description.appendText("Item in Search List must contain 'Java/java'. Item with error '$errorItem'")
            }

            override fun matchesSafely(value: String): Boolean {
                repeat(swipeDepth) {
                    val searchResultsList = mainPage.getListViewElement(By.id("org.wikipedia:id/search_results_list"),
                            By.id("org.wikipedia:id/page_list_item_title")).forEach {

                        val regex = """\bjava\b""".toRegex(RegexOption.IGNORE_CASE)
                        if (!regex.containsMatchIn(it.text)) {
                            errorItem = it.text
                            return false
                        }
                    }
                    mainPage.swipeDown()
                    swipeDepth = swipeDepth.dec()
                }
                return true
            }
        }
    }
}