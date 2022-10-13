package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
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
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    // Subject under test
    private lateinit var remindersLocalRepository: RemindersLocalRepository

    // Local database used in the test
    private lateinit var database: RemindersDatabase

    // Executes each task synchronously using Architecture Components
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    /**
     * Prepares for testing by initializing [database] and [remindersLocalRepository]
     */
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

    /**
     * Cleans up after testing by closing the database
     */

    @After
    fun cleanUp() {
        database.close()
    }

    /**
     * Tests [RemindersLocalRepository.saveReminder] and [RemindersLocalRepository.getReminder]
     * functionality to verify that a reminder saved to the database can be retrieved by its id
     */

    @Test
    fun saveReminder_retrieveReminderById() = runBlocking {
        // GIVEN - save a reminder to the local repository
        val reminder = gimmeReminder()
        remindersLocalRepository.saveReminder(reminder)

        // WHEN - reminder is retrieved
        val result = remindersLocalRepository.getReminder(reminder.id)

        // THEN - the reminder is retrieved successfully
        assertThat(result is Result.Success, `is`(true))

        // THEN - the reminder matches the one was saved
        result as Result.Success
        assertThat(result.data.title, `is`(reminder.title))
        assertThat(result.data.description, `is`(reminder.description))
        assertThat(result.data.latitude, `is`(reminder.latitude))
        assertThat(result.data.longitude, `is`(reminder.longitude))
        assertThat(result.data.location, `is`(reminder.location))
    }

    /**
     * Tests [RemindersLocalRepository.saveReminder], [RemindersLocalRepository.deleteAllReminders],
     * and [RemindersLocalRepository.getReminders] functionality to verify that after a reminder saved,
     * then deleted, the retrieved list of reminders would be empty.
     */

    @Test
    fun deleteReminders_emptyList()= runBlocking {
        // GIVEN - save a reminder then delete it
        val reminder = gimmeReminder() // getting the reminder

        remindersLocalRepository.saveReminder(reminder) // Saving it
        remindersLocalRepository.deleteAllReminders() // Deleting it

        // WHEN - retrieving all reminders in the repo
        val result = remindersLocalRepository.getReminders()

        // THEN - the reminders are retrieved successfully
        assertThat(result is Result.Success, `is`(true))
        result as Result.Success

        // THEN - the reminders list retrieved is empty
        assertThat(result.data, `is` (emptyList()))
    }

    /**
     * Tests [RemindersLocalRepository.saveReminder], [RemindersLocalRepository.deleteAllReminders],
     * and [RemindersLocalRepository.getReminder] functionality to verify that after a
     * reminder saved, then all reminders deleted, when attempting to retrieve the saved
     * reminder by id, an error message is received.
     */

    @Test
    fun retrieveReminderById_returnError() = runBlocking {
        // GIVEN - save a reminder then delete all reminders
        val reminder = gimmeReminder() // getting the reminder

        remindersLocalRepository.saveReminder(reminder) // saving it
        remindersLocalRepository.deleteAllReminders() // deleting all reminders

        // WHEN - retrieving the reminders from the repo by its id
        val result = remindersLocalRepository.getReminder(reminder.id)

        // THEN - an error result is returned
        assertThat(result is Result.Error, `is`(true))
        result as Result.Error

        // THEN - the error message matches the expected one for this error
        assertThat(result.message, `is`("Reminder not found!"))
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