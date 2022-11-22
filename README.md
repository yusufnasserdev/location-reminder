# location-reminder

A Todo list android app with location reminders that remind the user to do something when he reaches a specific location using kotlin.

---

## Test run

https://user-images.githubusercontent.com/43588217/196063394-8eb2f267-a37c-4c1d-859c-384fd0051f9e.mp4

As Shown in the test run, the geofence and the notifications work on emulator pixel 3 API 29.

https://user-images.githubusercontent.com/43588217/196064164-6e24009e-bdef-446a-80a2-33649ee5adc0.mp4

All the unit tests as well as the instrumented tests pass.

## Dependencies

```text
1. A created project on Firebase console.
2. A created project on Google Cloud console.
```

---

## Testing

Right click on the `test` or `androidTest` packages and select Run Tests

### Tests Break Down
- All tests and test related functions are properly explained in their respective files.

---

## Project Walkthrough

- A Login screen to ask users to login using an email address or a Google account.  Upon successful login, it navigates the user to the Reminders screen.   If there is no account, it navigates to a Register screen.

- A Register screen to allow a user to register using an email address or a Google account.

- A home screen that displays the reminders retrieved from local storage. If there are no reminders, it shows a   "No Data"  indicator.  If there are any errors, it displays an error message.

- A screen that shows a map with the user's current location and asks the user to select a point of interest to create a reminder.

- An add a reminder screen when a user reaches the selected location.  Each reminder should include a title, a description, and the selected location.

- Reminder data is saved to local storage.

- For each reminder, a geofence is created that fires up a notification when the user enters the geofencing area.

- ViewModels, Coroutines and LiveData objects unit tests.

- A FakeDataSource to replace the Data Layer and test the app in isolation.

- Espresso and Mockito is used to test each screen of the app:
  - DAO (Data Access Object) and Repository classes testing.
  - Error messages testing.
  - End-To-End testing for the Fragments navigation.

---

## Built With

- [Koin](https://github.com/InsertKoinIO/koin) - A pragmatic lightweight dependency injection framework for Kotlin.
- [FirebaseUI Authentication](https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md) - FirebaseUI provides a drop-in auth solution that handles the UI flows for signing
- [JobIntentService](https://developer.android.com/reference/androidx/core/app/JobIntentService) - Run background service from the background application, Compatible with >= Android O.
