package com.udacity.reminder.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.reminder.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class RemindersDaoTest {

    private lateinit var database: RemindersDatabase

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDb() {
        // using an in-memory database because the information stored here disappears when the
        // process is killed
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun saveReminderAndGetById() = runBlockingTest {
        // GIVEN - save a reminder
        val reminder = ReminderDTO(
            "Wedding Photo Session",
            "Nice lovely park with green areas.",
            "Family Park",
            30.140786494237776,
            31.493407822401
        )
        database.reminderDao().saveReminder(reminder)

        // WHEN - Get the reminder by id from the database
        val result = database.reminderDao().getReminderById(reminder.id)

        // THEN - the retrieved reminder contains the expected values
        MatcherAssert.assertThat(result as ReminderDTO, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(result.id, CoreMatchers.`is`(reminder.id))
        MatcherAssert.assertThat(result.title, CoreMatchers.`is`(reminder.title))
        MatcherAssert.assertThat(result.description, CoreMatchers.`is`(reminder.description))
        MatcherAssert.assertThat(result.location, CoreMatchers.`is`(reminder.location))
        MatcherAssert.assertThat(result.latitude, CoreMatchers.`is`(reminder.latitude))
        MatcherAssert.assertThat(result.longitude, CoreMatchers.`is`(reminder.longitude))

    }

    @Test
    fun getAllRemindersFromDb() = runBlockingTest {
        // GIVEN - save a few reminders to the database
        val reminder = ReminderDTO(
            "Home",
            "Home sweet home",
            "Home",
            29.140786494237776,
            30.493407822401
        )
        val reminder2 = ReminderDTO(
            "Wedding Photo Session",
            "Nice lovely park with green areas.",
            "Family Park",
            28.140786494237776,
            27.493407822401
        )
        val reminder3 = ReminderDTO(
            "Wedding Location",
            "Nice areas.",
            "Family",
            26.140786494237776,
            27.493407822401
        )

        database.reminderDao().saveReminder(reminder)
        database.reminderDao().saveReminder(reminder2)
        database.reminderDao().saveReminder(reminder3)

        // WHEN - Retrieving the reminders from the database
        val remindersList = database.reminderDao().getReminders()

        // THEN - the retrieved list of reminders is not null
        MatcherAssert.assertThat(remindersList, CoreMatchers.`is`(CoreMatchers.notNullValue()))
    }

    @Test
    fun insertRemindersAndDeleteAllReminders() = runBlockingTest {
        // GIVEN - save a few reminders to the database
        val reminder = ReminderDTO(
            "Home",
            "Home sweet home",
            "Home",
            29.140786494237776,
            30.493407822401
        )
        val reminder2 = ReminderDTO(
            "Wedding Photo Session",
            "Nice lovely park with green areas.",
            "Family Park",
            28.140786494237776,
            27.493407822401
        )
        val reminder3 = ReminderDTO(
            "Wedding Location",
            "Nice areas.",
            "Family",
            26.140786494237776,
            27.493407822401
        )

        database.reminderDao().saveReminder(reminder)
        database.reminderDao().saveReminder(reminder2)
        database.reminderDao().saveReminder(reminder3)

        // WHEN - Deleting the reminders from the database
        database.reminderDao().deleteAllReminders()

        val remindersList = database.reminderDao().getReminders()

        // THEN - the retrieved list of reminders is empty
        MatcherAssert.assertThat(remindersList, CoreMatchers.`is`(emptyList()))
    }
}