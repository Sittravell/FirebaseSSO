package com.example.templates.utils.sso

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.util.Log
import com.example.templates.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class GoogleButton(context: Context, attrs: AttributeSet): MaterialButton(context, attrs) {
    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }

    interface LoginListener{
        fun onGoogleSignIn(success: Boolean, message: String, email: String?)
    }
    val auth = FirebaseAuth.getInstance()
    lateinit var cntxt: Context
    lateinit var loginListener: LoginListener
    fun setup(context: Context, l: LoginListener){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)/*
            IMPORTANT: Uncomment this.R.string.default_web_client_id is provided by firebase.
            Make sure installation is correct.
            .requestIdToken(context.getString(R.string.default_web_client_id))
         */
            .requestEmail()
            .build()
        val gsi = GoogleSignIn.getClient(context, gso)
        setOnClickListener {
            (context as Activity).startActivityForResult(gsi.signInIntent, RC_SIGN_IN)
        }
        cntxt = context
        loginListener = l

    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                loginListener.onGoogleSignIn(false, "Google sign in Failed: $e", null)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(cntxt as Activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    if(user?.email != null)
                        loginListener.onGoogleSignIn(true, "Google sign in success", user.email)
                    else
                        loginListener.onGoogleSignIn(false, "Google sign in success but no email found", null)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    loginListener.onGoogleSignIn(false, "Google sign in Failed: ${task.exception}", null)
                }
            }
    }
}