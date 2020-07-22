package com.teamnusocial.nusocial

import android.view.View
import android.widget.Button
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.teamnusocial.nusocial.data.model.User
import com.teamnusocial.nusocial.ui.buddymatch.Adapter
import com.teamnusocial.nusocial.ui.buddymatch.BuddyMatchFragment
import org.hamcrest.Matcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BuddyMatchFragmentTest {
    @get:Rule
    val activityRule = ActivityTestRule(HomeActivity::class.java)
    private fun startBuddyMatchFragment(): BuddyMatchFragment {
        val homeActivity = activityRule.activity
        val fragmentTransaction = homeActivity.supportFragmentManager.beginTransaction()
        val buddyMatchFragment = BuddyMatchFragment()
        fragmentTransaction.add(buddyMatchFragment, "buddyMatchFragment")
        fragmentTransaction.commit()
        return buddyMatchFragment
    }
    @Test
    fun test_aboutButton() {
        activityRule.activity.runOnUiThread {
            val buddyMatchFragment = startBuddyMatchFragment()
            buddyMatchFragment.populateMatchedUsers(mutableListOf(User(), User()))
            //
        }
        onView(withId(R.id.title_buddymatch)).check(matches(withText("BuddyMatch")))
        //onView(withId(R.id.match_swipe)).perform(RecyclerViewActions.actionOnItemAtPosition<Adapter.MatchHolder>(1, clickButtons()))
        //onView(withId(R.id.more_info_image)).check(matches(isDisplayed()))
        //onView(withId(R.id.match_swipe)).perform(RecyclerViewActions.actionOnItemAtPosition<Adapter.MatchHolder>(1, clickButtons()))
        //
    }
    fun clickButtons(): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View>? {
                return null
            }
            override fun getDescription(): String {
                return "Click on About button."
            }

            override fun perform(uiController: UiController, view: View) {
                val more_info_button = view.findViewById<Button>(R.id.more_info_buddymatch)
                val match_button = view.findViewById<Button>(R.id.match_button)
                more_info_button.performClick()
            }
        }
    }
}