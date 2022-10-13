package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.savereminder.SaveReminderFragment
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest :
    AutoCloseKoinTest() {

    // Local repository to used in tests
    private lateinit var repository: ReminderDataSource

    // application context to used in tests
    private lateinit var context: Application

    // Executes each task synchronously using Architecture Components
    @get:Rule
    val instantExecutor = InstantTaskExecutorRule()

    /**
     * Prepares for testing by initializing [context], [repository] and Koin module
     */

    @Before
    fun init() {
        stopKoin() // Stops current Koin application preparing for starting a new one
        context = ApplicationProvider.getApplicationContext() // Initializing app context

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
     * Tests [ReminderListFragment], FAB button and navigation to [SaveReminderFragment]
     * functionality to verify that after the FAB pressed, the navController takes the user to
     * the [SaveReminderFragment].
     */

    @Test
    fun clickFab_navigateToSaveReminderFragment () {

        // GIVEN - a fragment scenario and a mock navController
        // Gets a fragment scenario for the ReminderListFragment
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        // Gets a moc navController object
        val navController = Mockito.mock(NavController::class.java)

        // Associates the navController with fragment root view
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        // WHEN - the FAB is pressed
        // Performing a click on the addReminderFAB via espresso framework
        Espresso.onView(withId(R.id.addReminderFAB)).perform(ViewActions.click())

        // THEN - the mock navController navigates correctly to the SaveReminderFragment
        Mockito.verify(navController).navigate(
            ReminderListFragmentDirections.toSaveReminder()
        )
    }

    /**
     * Tests [ReminderListFragment], reminderssRecyclerView to functionality to verify that
     * after a reminder has been saved, the recyclerView will show it to the user.
     */

    @Test
    fun verifyReminderAdded_reminderDisplayed(): Unit = runBlocking {

        // GIVEN - a reminder
        val reminder = gimmeReminder() // getting it

        // WHEN - saved to the repo
        repository.saveReminder(reminder) // saving it

        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme) // Launching the fragment

        // THEN - the recyclerView will show it to the user
        Espresso.onView(withId(R.id.reminderssRecyclerView))
            .perform(
                RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                    ViewMatchers.hasDescendant(withText(reminder.title))
                )
            )
    }

    /**
     * Tests [ReminderListFragment] functionality to verify that
     * when the repo is empty, the noDataTextView will be shown to the user.
     */

    @Test
    fun verifyAllRemindersDeleted_noRemindersDisplayed(): Unit = runBlocking {

        // GIVEN - a reminder
        val reminder = gimmeReminder() // getting it

        // WHEN - saved to the repo and then deleting all reminders
        repository.saveReminder(reminder) // saving it
        repository.deleteAllReminders() // deleting all reminders

        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme) // Launching the fragment

        // THEN - the noDataTextView will be shown to the user
        Espresso.onView(withId(R.id.noDataTextView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    /**
     * @return a [ReminderDTO] instance to be used in testing
     */

    private fun gimmeReminder() : ReminderDTO {
        return ReminderDTO(
            "Wedding Photo Session",
            "Nice lovely park with green areas.",
            "Family Park",
            30.140786494237776,
            31.493407822401
        )
    }

}