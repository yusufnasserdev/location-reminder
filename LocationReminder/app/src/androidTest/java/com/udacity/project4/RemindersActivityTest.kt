package com.udacity.project4

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
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.DataBindingIdlingResource
import com.udacity.project4.utils.EspressoIdlingResource
import com.udacity.project4.utils.monitorActivity
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
//END TO END test to black box test the app
class RemindersActivityTest :
    AutoCloseKoinTest() {

    // Local repository to used in tests
    private lateinit var repository: ReminderDataSource

    // application context to used in tests
    private lateinit var context: Application

    // An Idling Resource that waits for Data Binding to have no pending bindings
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    /**
     * Prepares for testing by initializing [context], [repository] and Koin module
     */

    @Before
    fun init() {
        stopKoin() // Stops current Koin application preparing for starting a new one
        context = getApplicationContext() // Initializing app context

        // Initializing Koin module
        val mModule = module {
            viewModel { RemindersListViewModel(context, get() as ReminderDataSource) }
            single { SaveReminderViewModel(context, get() as ReminderDataSource) }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(context) }
        }

        // Starting koin application
        startKoin { modules(listOf(mModule)) }

        // Initializing the local repo
        repository = get()

        // Cleaning up the repo
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

    /**
     * @return an activity from a provided activityScenario
     */

    private fun getActivity(activityScenario: ActivityScenario<RemindersActivity>): Activity {
        lateinit var activity: Activity
        activityScenario.onActivity {
            activity = it
        }
        return activity
    }

    /**
     * Tests [RemindersActivity] to verify it shows a snackBarMessage that
     * the added reminder missing a title.
     */
    @Test
    fun saveReminderScreen_showSnackBarTitleError() {

        // GIVEN - Starts the remindersActivity
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // WHEN - Clicks on the addReminderFAB then Clicks on saveReminder button
        onView(withId(R.id.addReminderFAB)).perform(click())
        onView(withId(R.id.saveReminder)).perform(click())

        // THEN - Shows snackBarMessage err_enter_title
        val snackBarMessage = context.getString(R.string.err_enter_title)
        onView(withText(snackBarMessage))
            .check(ViewAssertions.matches(isDisplayed()))

        // Closes the activity
        activityScenario.close()
    }

    /**
     * Tests [RemindersActivity] to verify it shows a snackBarMessage that
     * the added reminder missing the location.
     */

    @Test
    fun saveReminderScreen_showSnackBarLocationError() {

        // GIVEN - Starts the remindersActivity
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // WHEN - Clicks on the addReminderFAB, fill in the title then Clicks on saveReminder button

        // Clicks on the addReminderFAB
        onView(withId(R.id.addReminderFAB)).perform(click())

        // Writes in the reminder title
        onView(withId(R.id.reminderTitle)).perform(typeText("Title"))

        // Closes the keyboard
        Espresso.closeSoftKeyboard()

        // Clicks on saveReminder button
        onView(withId(R.id.saveReminder)).perform(click())

        // THEN - Shows snackBarMessage err_select_location
        val snackBarMessage = context.getString(R.string.err_select_location)
        onView(withText(snackBarMessage))
            .check(ViewAssertions.matches(isDisplayed()))

        // Closes the activity
        activityScenario.close()
    }

    /**
     * Tests [RemindersActivity] to verify it shows a toastMessage that
     * the added reminder has been saved successfully.
     *
     * NOTE: Make sure the location permissions are granted before running this test
     * NOTE: Test fails on APIs after API [29], known issue with espresso.
     */
    @Test
    fun saveReminderScreen_showToastMessage() {

        // GIVEN - Starts the remindersActivity
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // WHEN - Clicks on the addReminderFAB, fill in the details then Clicks on saveReminder button

        // Clicks on the addReminderFAB
        onView(withId(R.id.addReminderFAB)).perform(click())

        // Writes in the reminder title
        onView(withId(R.id.reminderTitle)).perform(typeText("Title"))

        // Closes keyboard
        Espresso.closeSoftKeyboard()

        // Writes in the reminder description
        onView(withId(R.id.reminderDescription)).perform(typeText("Description"))

        // Closes the keyboard
        Espresso.closeSoftKeyboard()

        // Clicks on the selectLocation textView
        onView(withId(R.id.selectLocation)).perform(click())

        // Long clicks on the mapFragment to select the location
        onView(withId(R.id.mapFragment)).perform(ViewActions.longClick())

        // Clicks on the save location button
        onView(withId(R.id.save_btn)).perform(click())

        // Clicks on the save reminder button
        onView(withId(R.id.saveReminder)).perform(click())


        // THEN - Shows a "Reminder Saved !" toast message
        onView(withText(R.string.reminder_saved))
            .inRoot(withDecorView(Matchers.not(`is`(getActivity(activityScenario).window.decorView))))
            .check(matches(isDisplayed()))

        // Closes the activity
        activityScenario.close()
    }


}