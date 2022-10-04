package com.udacity.reminder

import android.app.Activity
import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.udacity.reminder.locationreminders.RemindersActivity
import com.udacity.reminder.locationreminders.data.ReminderDataSource
import com.udacity.reminder.locationreminders.data.local.LocalDB
import com.udacity.reminder.locationreminders.data.local.RemindersLocalRepository
import com.udacity.reminder.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.reminder.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.reminder.utils.DataBindingIdlingResource
import com.udacity.reminder.utils.EspressoIdlingResource
import com.udacity.reminder.utils.monitorActivity
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get


@RunWith(AndroidJUnit4::class)
@LargeTest
class RemindersActivityTest :
    AutoCloseKoinTest() {

    private lateinit var repository: ReminderDataSource
    private lateinit var context: Application

    // An Idling Resource that waits for Data Binding to have no pending bindings
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun init() {
        stopKoin()
        context = getApplicationContext()

        val mModule = module {
            viewModel { RemindersListViewModel(context, get() as ReminderDataSource) }
            single { SaveReminderViewModel(context, get() as ReminderDataSource) }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(context) }
        }

        startKoin { modules(listOf(mModule)) }
        repository = get()
        runBlocking { repository.deleteAllReminders() }
    }

    /**
     * Idling resources tell Espresso that the app is idle or busy. This is needed when operations
     * are not scheduled in the main Looper (for example when executed on a different thread).
     */
    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    private fun getActivity(activityScenario: ActivityScenario<RemindersActivity>): Activity {
        lateinit var activity: Activity
        activityScenario.onActivity {
            activity = it
        }
        return activity
    }

    @Test
    fun saveReminderScreen_showSnackBarTitleError() {

        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.addReminderFAB)).perform(click())
        onView(withId(R.id.saveReminder)).perform(click())

        val snackBarMessage = context.getString(R.string.err_enter_title)
        onView(withText(snackBarMessage))
            .check(ViewAssertions.matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun saveReminderScreen_showSnackBarLocationError() {

        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.addReminderFAB)).perform(click())
        onView(withId(R.id.reminderTitle)).perform(typeText("Title"))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.saveReminder)).perform(click())

        val snackBarMessage = context.getString(R.string.err_select_location)
        onView(withText(snackBarMessage))
            .check(ViewAssertions.matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun saveReminderScreen_showToastMessage() {

        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.addReminderFAB)).perform(click())
        onView(withId(R.id.reminderTitle)).perform(typeText("Title"))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.reminderDescription)).perform(typeText("Description"))
        Espresso.closeSoftKeyboard()

        onView(withId(R.id.selectLocation)).perform(click())
        onView(withId(R.id.mapFragment)).perform(ViewActions.longClick())

        onView(withId(R.id.save_btn)).perform(click())
        onView(withId(R.id.saveReminder)).perform(click())

        onView(withText(R.string.reminder_saved))
            .inRoot(withDecorView(Matchers.not(`is`(getActivity(activityScenario).window.decorView))))
            .check(matches(isDisplayed()))

        activityScenario.close()
    }


}