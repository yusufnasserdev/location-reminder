package com.udacity.project4.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityAuthenticationBinding
import com.udacity.project4.locationreminders.RemindersActivity


/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {

    /*
     * Companion object to hold the class constants.
     */
    companion object {
        // Class tag to be used in logging.
        const val TAG = "AuthenticationActivity"
    }

    // Class binding object
    private lateinit var binding: ActivityAuthenticationBinding

    // Activity result sign-in launcher
    private val signInLauncher =
        registerForActivityResult(FirebaseAuthUIActivityResultContract())
        { result: FirebaseAuthUIAuthenticationResult? ->
            // Handling the sign-in launcher result
            when (result?.resultCode) {
                RESULT_OK -> {
                    // User successfully signed in
                    Log.i(
                        TAG,
                        "Successfully signed in user ${FirebaseAuth.getInstance().currentUser?.displayName}!"
                    )

                    // Showing a toast of the sign-in success
                    Toast.makeText(this, "Successfully signed in", Toast.LENGTH_SHORT).show()

                    // Starting reminder activity
                    fireReminderActivity()
                }
                else -> {
                    // Sign in failed. If response is null the user canceled the
                    // sign-in flow using the back button. Otherwise check
                    Log.i(TAG, "Sign in unsuccessful ${result?.idpResponse?.error}")

                    // Showing a toast of the sign-in failure
                    Toast.makeText(this, "Sign in unsuccessful", Toast.LENGTH_SHORT).show()
                }
            }
        }

    /**
     * Starts sign-in process
     */

    private fun startSignIn() {

        // Initializing the sign-in providers wanted
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.EmailBuilder().build()
        )

        // Building the sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()

        // Using the activity result launcher to fire up the sign-in intent
        signInLauncher.launch(signInIntent)
    }


    /**
     * Creates the auth activity and sets the clickListener
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflating the auth activity layout and Initializing the binding object
        binding = DataBindingUtil.setContentView(this, R.layout.activity_authentication)

        // Setting the lifecycle-owner to this activity
        binding.lifecycleOwner = this

        // Setting the onClickListener for the sign-in button to start the sign-in
        binding.signInBtn.setOnClickListener {
            startSignIn()
        }
    }

    /**
     * Creates intent for the reminders activity and launching it while finishing the Auth activity.
     */
    private fun fireReminderActivity() {
        // Creating the intent
        val remindersActivityIntent = Intent(this, RemindersActivity::class.java)

        // Starting the reminders activity
        startActivity(remindersActivityIntent)

        // Finishing the current activity
        finish()
    }
}
