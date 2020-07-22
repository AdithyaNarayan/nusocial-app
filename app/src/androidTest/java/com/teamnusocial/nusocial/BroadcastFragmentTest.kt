package com.teamnusocial.nusocial

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.teamnusocial.nusocial.ui.broadcast.BroadcastFragment
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BroadcastFragmentTest {
    @get:Rule
    val activityRule = ActivityTestRule(HomeActivity::class.java)
    private fun startBroadcastFragment(): BroadcastFragment {
        val homeActivity = activityRule.activity
        val fragmentTransaction = homeActivity.supportFragmentManager.beginTransaction()
        val broadcastFragment = BroadcastFragment()
        fragmentTransaction.add(broadcastFragment, "broadcastFragment")
        fragmentTransaction.commit()
        return broadcastFragment
    }
    @Test
    fun test_swipeUpButton() {
        activityRule.activity.runOnUiThread {
            val broadcastFragment = startBroadcastFragment()
        }
        onView(withId(R.id.broadcastArrow)).perform(click())
        onView(withId(R.id.bottom_sheet)).check(matches(isDisplayed()))
    }
}