package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.utils.MainCoroutineRule
import com.udacity.project4.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

/**
 * A testing class for the [RemindersListViewModel] unit testing
 */

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    // Subject under test
    private lateinit var viewModel: RemindersListViewModel

    // Uses a fake repository to be injected into the viewmodel
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
            RemindersListViewModel(ApplicationProvider.getApplicationContext(), remindersRepository)
    }

    /**
     * Cleans up after testing by stopping the Koin application
     */

    @After
    fun cleanUp() {
        stopKoin()
    }

    @Test
    fun getReminders_nullRemindersList() = mainCoroutineRule.runBlockingTest {
        val reminders = remindersRepository.getReminders()
        assertThat(reminders).isNotNull()
    }

    /**
     * Tests [RemindersListViewModel.loadReminders] and [RemindersListViewModel.showLoading] functionality
     * to verify that [RemindersListViewModel.showLoading] is true while loading the reminders
     * and then false after they're loaded.
     */

    @Test
    fun loadReminders_showLoading() {
        // Pauses dispatcher to test the values before executing the coroutines
        mainCoroutineRule.pauseDispatcher()

        // Loading the reminders
        viewModel.loadReminders()

        // Verify that the showLoading value is true while it's still loading
        assertThat(viewModel.showLoading.getOrAwaitValue()).isTrue()

        // Resumes dispatcher to execute the coroutines in loadReminders()
        mainCoroutineRule.resumeDispatcher()

        // Verify that the showLoading value is false after the reminders are loaded
        assertThat(viewModel.showLoading.getOrAwaitValue()).isFalse()

    }

    /**
     * Tests [RemindersListViewModel.loadReminders] and [FakeDataSource.saveReminder] functionality
     * to verify that [RemindersListViewModel.remindersList] is not empty after adding a reminder to
     * it.
     */

    @Test
    fun loadReminders_remainderListNotEmpty() = mainCoroutineRule.runBlockingTest {
        // GIVEN - save a reminder to the database
        val reminder = ReminderDTO(
            "Wedding Photo Session",
            "Nice lovely park with green areas.",
            "Family Park",
            30.140786494237776,
            31.493407822401
        )
        remindersRepository.saveReminder(reminder)

        // WHEN - loading the reminders list
        viewModel.loadReminders()

        // Then - the returned list is not empty
        assertThat(viewModel.remindersList.getOrAwaitValue()).isNotEmpty()
    }

    /**
     * Tests [RemindersListViewModel.loadReminders] behaviour if an error occurred while retrieving
     * the reminders.
     * Verifies that [RemindersListViewModel.showSnackBar] value matches the error message expected
     * when an error occurs while retrieving the reminders
     */

    @Test
    fun loadReminders_updateSnackBarValue() {
        // Pauses dispatcher to test the values before executing the coroutines
        mainCoroutineRule.pauseDispatcher()

        // Setting the data source to return an error when retrieving the reminders
        remindersRepository.setReturnError(true)

        // Loading the reminders
        viewModel.loadReminders()

        // Resumes dispatcher to execute the coroutines in loadReminders()
        mainCoroutineRule.resumeDispatcher()

        // Verify that the showSnackBar value matches the error message expected when an error occurs
        assertThat(viewModel.showSnackBar.getOrAwaitValue()).isEqualTo("Error retrieving reminders")
    }
}