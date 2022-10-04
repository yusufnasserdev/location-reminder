package com.udacity.reminder.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.reminder.locationreminders.data.dto.ReminderDTO
import com.udacity.reminder.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class RemindersLocalRepositoryTest {

    private lateinit var remindersLocalRepository: RemindersLocalRepository

    private lateinit var database: RemindersDatabase

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()

        remindersLocalRepository = RemindersLocalRepository(
            database.reminderDao(),
            Dispatchers.Main
        )
    }

    @After
    fun cleanUp() {
        database.close()
    }

    @Test
    fun saveReminder_retrieveReminderById() = runBlocking {
        val reminder = gimmeReminder()

        remindersLocalRepository.saveReminder(reminder)

        val result = remindersLocalRepository.getReminder(reminder.id) as? Result.Success

        assertThat(result is Result.Success, `is`(true))
        result as Result.Success


        assertThat(result.data.title, `is`(reminder.title))
        assertThat(result.data.description, `is`(reminder.description))
        assertThat(result.data.latitude, `is`(reminder.latitude))
        assertThat(result.data.longitude, `is`(reminder.longitude))
        assertThat(result.data.location, `is`(reminder.location))
    }


    @Test
    fun deleteReminders_EmptyList()= runBlocking {
        val reminder = gimmeReminder()

        remindersLocalRepository.saveReminder(reminder)

        remindersLocalRepository.deleteAllReminders()

        val result = remindersLocalRepository.getReminders()

        assertThat(result is Result.Success, `is`(true))
        result as Result.Success

        assertThat(result.data, `is` (emptyList()))
    }

    @Test
    fun retrieveReminderById_ReturnError() = runBlocking {
        val reminder = gimmeReminder()

        remindersLocalRepository.saveReminder(reminder)

        remindersLocalRepository.deleteAllReminders()

        val result = remindersLocalRepository.getReminder(reminder.id)

        assertThat(result is Result.Error, `is`(true))
        result as Result.Error
        assertThat(result.message, `is`("Reminder not found!"))
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