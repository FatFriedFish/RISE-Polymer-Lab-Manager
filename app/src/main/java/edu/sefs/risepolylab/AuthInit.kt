package edu.sefs.risepolylab

import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

// https://firebase.google.com/docs/auth/android/firebaseui
class AuthInit(viewModel: MainViewModel, signInLauncher: ActivityResultLauncher<Intent>) {
    companion object {
        private const val TAG = "AuthInit"
        fun setDisplayName(displayName : String, viewModel: MainViewModel) {
            Log.d(TAG, "profile change request")
            // XXX Write me. User is attempting to update display name. Get the profile updates (see android doc)
            // UserProfileChangeRequest.Builder()
            val user = FirebaseAuth.getInstance().currentUser

            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build()

            user!!.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "User profile updated.")
                        viewModel.updateUser()
                    }
                }

        }
    }

    init {
        val user = FirebaseAuth.getInstance().currentUser
        if(user == null) {
            Log.d(TAG, "user null")
            // Choose authentication providers
            val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build())

            // Create and launch sign-in intent
            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false)
                .build()
            signInLauncher.launch(signInIntent)

            // setIsSmartLockEnabled(false) solves some problems
        } else {
            Log.d(TAG, "user ${user.displayName} email ${user.email}")
            viewModel.updateUser()
        }
    }
}
