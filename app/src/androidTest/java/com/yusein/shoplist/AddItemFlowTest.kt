package com.yusein.shoplist

import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddItemFlowTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun addedItemAppearsInList() {
        // AAA го подрежда най-горе, а часът прави името уникално
        val itemName = "AAA Test ${System.currentTimeMillis()}"

        onView(withId(R.id.fabAdd)).perform(click())
        onView(withId(R.id.editName)).perform(typeText(itemName))
        onView(withId(R.id.editQuantity)).perform(typeText("2"))
        closeSoftKeyboard()
        onView(withId(R.id.buttonSave)).perform(click())

        onView(withText(itemName)).check(matches(isDisplayed()))
    }
}