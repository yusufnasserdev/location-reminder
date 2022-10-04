package com.udacity.reminder.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.reminder.getOrAwaitValue
import com.udacity.reminder.locationreminders.MainCoroutineRule
import com.udacity.reminder.locationreminders.data.FakeDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import com.udacity.reminder.R
import com.google.common.truth.Truth.assertThat
import com.udacity.reminder.locationreminders.data.dto.ReminderDTO
import com.udacity.reminder.locationreminders.reminderslist.ReminderDataItem
import com.udacity.reminder.locationreminders.reminderslist.RemindersListViewModel
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin


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

    @Before
    fun setupViewModel() {
        remindersRepository = FakeDataSource()
        viewModel =
            SaveReminderViewModel(ApplicationProvider.getApplicationContext(), remindersRepository)
    }

    @After
    fun cleanUp() {
        stopKoin()
    }

    @Test
    fun validateEnteredData_NullTitleAndUpdateSnackBar() {
        val reminder = ReminderDataItem(
            title = "",
            description = "Description",
            location = "My School",
            latitude = 30.140786494237776,
            longitude = 31.493407822401
        )

        assertThat(viewModel.validateEnteredData(reminder)).isFalse()
        assertThat(viewModel.showSnackBarInt.getOrAwaitValue()).isEqualTo(R.string.err_enter_title)
    }

    @Test
    fun validateEnteredData_NullLocationAndUpdateSnackBar() {
        val reminder = ReminderDataItem(
            title = "Title",
            description = "Description",
            location = "",
            latitude = 30.140786494237776,
            longitude = 31.493407822401
        )

        assertThat(viewModel.validateEnteredData(reminder)).isFalse()
        assertThat(viewModel.showSnackBarInt.getOrAwaitValue()).isEqualTo(R.string.err_select_location)
    }


    @Test
    fun saveReminder_showLoading() {
        val reminder = ReminderDataItem(
            title = "Title",
            description = "Description",
            location = "Location",
            latitude = 30.140786494237776,
            longitude = 31.493407822401
        )

        mainCoroutineRule.pauseDispatcher()
        viewModel.saveReminder(reminder)
        assertThat(viewModel.showLoading.getOrAwaitValue()).isTrue()
        mainCoroutineRule.resumeDispatcher()
        assertThat(viewModel.showLoading.getOrAwaitValue()).isFalse()
    }


}