# location-reminder

A Todo list android app with location reminders that remind the user to do something when he reaches a specific location using kotlin.

## Dependencies

```text
1. A created project on Firebase console.
2. A created project on Google Cloud console.
```

## Testing

Right click on the `test` or `androidTest` packages and select Run Tests

## Tests Break Down

```text
1. androidTest
        //TODO: Students explain their testing here.
1. test
        //TODO: Students explain their testing here.
```

## Project Instructions

- [x] Create a Login screen to ask users to login using an email address or a Google account.  Upon successful login, navigate the user to the Reminders screen.   If there is no account, the app should navigate to a Register screen.

- [ ] Create a Register screen to allow a user to register using an email address or a Google account.

- [ ] Create a screen that displays the reminders retrieved from local storage. If there are no reminders, display a   "No Data"  indicator.  If there are any errors, display an error message.

- [ ] Create a screen that shows a map with the user's current location and asks the user to select a point of interest to create a reminder.

- [ ] Create a screen to add a reminder when a user reaches the selected location.  Each reminder should include
a. title
b. description
c. selected location

- [ ] Reminder data should be saved to local storage.

- [ ] For each reminder, create a geofencing request in the background that fires up a notification when the user enters the geofencing area.

- [ ] Provide testing for the ViewModels, Coroutines and LiveData objects.

- [ ] Create a FakeDataSource to replace the Data Layer and test the app in isolation.

- [ ] Use Espresso and Mockito to test each screen of the app:
  - [ ] Test DAO (Data Access Object) and Repository classes.
  - [ ] Add testing for the error messages.
  - [ ] Add End-To-End testing for the Fragments navigation.

## Built With

- [Koin](https://github.com/InsertKoinIO/koin) - A pragmatic lightweight dependency injection framework for Kotlin.
- [FirebaseUI Authentication](https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md) - FirebaseUI provides a drop-in auth solution that handles the UI flows for signing
- [JobIntentService](https://developer.android.com/reference/androidx/core/app/JobIntentService) - Run background service from the background application, Compatible with >= Android O.
