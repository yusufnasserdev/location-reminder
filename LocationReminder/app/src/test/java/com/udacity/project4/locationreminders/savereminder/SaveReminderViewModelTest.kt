package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.utils.MainCoroutineRule
import com.udacity.project4.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

/**
 * A testing class for the [SaveReminderViewModel] unit testing
 */

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class SaveReminderViewModelTest {

    // Subject under test
    private lateinit var viewModel: SaveReminderViewModel

    // Use a fake repository to be injected into the viewmodel
    private lateinit var remindersRepository: FakeDataSource


    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    /**
     * Sets up the viewModel for testing by initializing the
     * [remindersRepository] with an instance of [FakeDataSource]
     * and then using it to initialize the viewModel.
     */

    @Before
    fun setupViewModel() {
        remindersRepository = FakeDataSource()
        viewModel =
            SaveReminderViewModel(ApplicationProvider.getApplicationContext(), remindersRepository)
    }

    /**
     * Cleans up after testing by stopping the Koin application
     */

    @After
    fun cleanUp() {
        stopKoin()
    }

    /**
     * Tests [SaveReminderViewModel.validateEnteredData] behaviour if a null title was entered
     */

    @Test
    fun validateEnteredData_nullTitleAndUpdateSnackBar() {
        // GIVEN - a reminder with a null title
        val reminder = ReminderDataItem(
            title = "",
            description = "Description",
            location = "My School",
            latitude = 30.140786494237776,
            longitude = 31.493407822401
        )

        // WHEN - user enters it
        val result = viewModel.validateEnteredData(reminder)

        // THEN - the returned value from validateEnteredData is false
        assertThat(result).isFalse()

        // And showSnackBarInt value matches the string resource id expected.
        assertThat(viewModel.showSnackBarInt.getOrAwaitValue()).isEqualTo(R.string.err_enter_title)
    }

    /**
     * Tests [SaveReminderViewModel.validateEnteredData] behaviour if a null location was entered
     */

    @Test
    fun validateEnteredData_nullLocationAndUpdateSnackBar() {
        // GIVEN - a reminder with a null location
        val reminder = ReminderDataItem(
            title = "Title",
            description = "Description",
            location = "",
            latitude = 30.140786494237776,
            longitude = 31.493407822401
        )

        // WHEN - user enters it
        val result = viewModel.validateEnteredData(reminder)

        // THEN - the returned value from validateEnteredData is false
        assertThat(result).isFalse()

        // And showSnackBarInt value matches the string resource id expected.
        assertThat(viewModel.showSnackBarInt.getOrAwaitValue()).isEqualTo(R.string.err_select_location)
    }


    /**
     * Tests [SaveReminderViewModel.showLoading] and [SaveReminderViewModel.saveReminder] functionality
     * to verify that [RemindersListViewModel.showLoading] is true while saving the reminder
     * and then false after they're loaded.
     */

    @Test
    fun saveReminder_showLoading() {
        // GIVEN - a reminder instance to be saved
        val reminder = ReminderDataItem(
            title = "Title",
            description = "Description",
            location = "Location",
            latitude = 30.140786494237776,
            longitude = 31.493407822401
        )

        // Pauses dispatcher to test the values before executing the coroutines
        mainCoroutineRule.pauseDispatcher()

        // WHEN - Saving the reminder
        viewModel.saveReminder(reminder)

        // THEN - showLoading value is true
        assertThat(viewModel.showLoading.getOrAwaitValue()).isTrue()

        // Resumes dispatcher to execute the coroutines in saveReminder()
        // When - the reminder is saved
        mainCoroutineRule.resumeDispatcher()

        // THEN - showLoading value is false
        assertThat(viewModel.showLoading.getOrAwaitValue()).isFalse()
    }


}