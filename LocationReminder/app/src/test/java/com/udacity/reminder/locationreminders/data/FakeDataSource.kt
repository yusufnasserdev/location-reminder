package com.udacity.reminder.locationreminders.data

import com.udacity.reminder.locationreminders.data.dto.ReminderDTO
import com.udacity.reminder.locationreminders.data.dto.Result

class FakeDataSource(var remindersList: MutableList<ReminderDTO>? = mutableListOf()) : ReminderDataSource {

    private var shouldReturnError = false


    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (shouldReturnError) {
            return Result.Error(
                "Error getting reminders"
            )
        }
        remindersList?.let { return Result.Success(it) }
        return Result.Error("Reminders not found")
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        remindersList?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        val reminder = remindersList?.find { reminderDTO ->
            reminderDTO.id == id
        }

        return when {
            shouldReturnError -> {
                Result.Error("Reminder not found!")
            }

            reminder != null -> {
                Result.Success(reminder)
            }
            else -> {
                Result.Error("Reminder not found!")
            }
        }
    }

    override suspend fun deleteAllReminders() {
        remindersList?.clear()
    }

}