package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository

/**
 * A test double for [RemindersLocalRepository] to be used in testing the viewModels
 */

class FakeDataSource(private var reminders: MutableList<ReminderDTO> = mutableListOf()) :
    ReminderDataSource {

    /**
     * If set to ture, it indicates that something went wrong with
     * the data source, basically it imitates an unknown exception
     */

    private var shouldReturnError = false


    /**
     * Sets [shouldReturnError] value
     */

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    /**
     * @return [Result.Error] if [shouldReturnError] is set to true;
     * otherwise, [Result.Success] with the list of reminders found.
     */

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        try {
            /**
             * Tests for unknown errors handling, if errors existed,
             * should return [Result.Error] alongside the error message
             */

            if (shouldReturnError) {
                return Result.Error(
                    "Error retrieving reminders"
                )
            }

            /**
             * If no errors were detected, returns [Result.Success] with the reminders list
             */

            return Result.Success(reminders)

        } catch (exception: Exception) {
            return Result.Error(exception.localizedMessage)
        }
    }

    /**
     * Saves a [ReminderDTO] to the list.
     */

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders.add(reminder)
    }

    /**
     * Retrieves a [ReminderDTO] from the list via an id
     */

    override suspend fun getReminder(id: String): Result<ReminderDTO> {

        try {
            /**
             * Tests for unknown errors handling, if errors existed,
             * should return [Result.Error] alongside the error message
             */

            if (shouldReturnError) {
                return Result.Error("An error occurred!")
            }

            /**
             * After verifying no errors existed, tries to retrieve the reminder if existed
             */

            val reminder = reminders.find { reminderDTO ->
                reminderDTO.id == id
            }

            /**
             * Tests for reminder not found error handling, if the reminder value is null,
             * returns [Result.Error]; otherwise, returns [Result.Success] with the reminder
             */

            return if (reminder != null) {
                Result.Success(reminder)
            } else {
                Result.Error("Reminder not found!")
            }

        } catch (exception: Exception) {
            return Result.Error(exception.localizedMessage)
        }
    }

    /**
     * Deletes all the reminders in the list
     */

    override suspend fun deleteAllReminders() {
        reminders.clear()
    }

}