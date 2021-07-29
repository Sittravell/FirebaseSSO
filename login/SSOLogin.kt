package com.example.templates.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.templates.base.SuperActivity
import com.example.templates.databinding.SsoLayoutBinding
import com.example.templates.models.User
import com.example.templates.utils.Navigate
import com.example.templates.utils.Session
import com.example.templates.utils.sso.FBMatButton
import com.example.templates.utils.sso.GoogleButton
import com.example.templates.utils.sso.PhoneLogin
import com.facebook.AccessToken
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth

class SSOLogin: SuperActivity(), View.OnClickListener, FBMatButton.FirebaseListener, GoogleButton.LoginListener {
    lateinit var binding: SsoLayoutBinding
    var auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SsoLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding){
            phone.setOnClickListener(this@SSOLogin)
            facebook.setup(listOf("email", "public_profile"),this@SSOLogin)
            google.setup(this@SSOLogin, this@SSOLogin)
        }
    }

    fun signInSuccess(email:String){
        /*REPLACE: Sign in is successful. Replace with your desired code.*/
//        Session.start(User(email, "test", "test","test"))
//        Navigate.reset(this, BaseActivity::class.java)
    }

    fun signInFailed(message: String){
        Toast.makeText(this, "Unable to Sign In", Toast.LENGTH_LONG).show()
    }

    override fun onGoogleSignIn(success: Boolean, message: String, email: String?) {
        if(success && email != null){
            signInSuccess(email)
        }else{
            signInFailed(message)
        }
    }
    override fun onFacebookTokenReady(token: AccessToken, email: String?) {
        Log.d("onFacebookTokenReady", "handleFacebookAccessToken:$token")

        if(email == null){
            signInFailed("email not found")
            return
        }
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("onFacebookTokenReady", "signInWithCredential:success")
                    val user = auth.currentUser
                    signInSuccess(email)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("onFacebookTokenReady", "signInWithCredential:failure", task.exception)
                    signInFailed("failed to use facebook token for firebase sign in")
                }
            }
    }

    override fun onClick(v: View?) {
        /* NOTE: You may perform phone verification / login here. But in this example , we will
            perform it in another class*/
        Navigate.push(this, PhoneLogin::class.java)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        binding.facebook.onActivityResult(requestCode, resultCode, data)
        binding.google.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}