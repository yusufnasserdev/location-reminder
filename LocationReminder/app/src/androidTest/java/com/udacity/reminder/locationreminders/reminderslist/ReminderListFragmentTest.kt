package com.udacity.reminder.locationreminders.reminderslist

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
import com.udacity.reminder.R
import com.udacity.reminder.locationreminders.data.ReminderDataSource
import com.udacity.reminder.locationreminders.data.dto.ReminderDTO
import com.udacity.reminder.locationreminders.data.local.LocalDB
import com.udacity.reminder.locationreminders.data.local.RemindersLocalRepository
import com.udacity.reminder.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.reminder.utils.DataBindingIdlingResource
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
@MediumTest
class ReminderListFragmentTest :
    AutoCloseKoinTest() {

    private lateinit var repository: ReminderDataSource
    private lateinit var context: Application

    @get:Rule
    val instantExecutor = InstantTaskExecutorRule()

    // An Idling Resource that waits for Data Binding to have no pending bindings
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun init() {
        stopKoin()
        context = ApplicationProvider.getApplicationContext()

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


    @Test
    fun clickFab_navigateToSaveReminderFragment () {

        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        Espresso.onView(withId(R.id.addReminderFAB)).perform(ViewActions.click())

        Mockito.verify(navController).navigate(
            ReminderListFragmentDirections.toSaveReminder()
        )
    }

    @Test
    fun remindersList_Displayed(): Unit = runBlocking {

        val reminder = gimmeReminder()

        repository.saveReminder(reminder)

        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        Espresso.onView(withId(R.id.reminderssRecyclerView))
            .perform(
                RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                    ViewMatchers.hasDescendant(withText(reminder.title))
                )
            )
    }


    @Test
    fun verify_noDataDisplayed(): Unit = runBlocking {
        val reminder = gimmeReminder()

        repository.saveReminder(reminder)

        repository.deleteAllReminders()

        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        Espresso.onView(withId(R.id.noDataTextView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

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