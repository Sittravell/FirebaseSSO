package com.example.templates.utils.sso

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import org.json.JSONException
import com.google.android.material.button.MaterialButton

class FBMatButton(context: Context, attrs: AttributeSet): MaterialButton(context, attrs) {

    private var _permissions = mutableListOf<String>()
    val permissions: List<String>
        get() = _permissions
    private var callbackManager = CallbackManager.Factory.create()

    var profileTracker: ProfileTracker? = null
    interface LoginListener{
        fun onFacebookLogin(success: Boolean, cancel: Boolean, message: String)
    }

    interface FirebaseListener{
        fun onFacebookTokenReady(token: AccessToken, email: String?)
    }
    fun setup(permissions: List<String>, firebaseListener: FirebaseListener? = null, loginListener: LoginListener? = null){
        setup(permissions,
            f = { at,e ->
                firebaseListener?.onFacebookTokenReady(at,e)
            } ,
            l = {s,c,m ->
                loginListener?.onFacebookLogin(s,c,m)
            })
    }

    fun trackProfile(l:(Profile?, Profile?) -> Unit){
        profileTracker = object : ProfileTracker() {
            override fun onCurrentProfileChanged(
                oldProfile: Profile?,
                currentProfile: Profile?
            ) {
                l(oldProfile, currentProfile)
            }
        }
    }
    private fun setup(permissions: List<String>, f: (AccessToken, String?) -> Unit, l: (Boolean, Boolean, String) -> Unit){
        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult?> {
                override fun onSuccess(loginResult: LoginResult?) {
                    Log.d("facebook", "success")
                    if(loginResult == null){
                        l(false, false, "Login successful but LoginResult not found ")
                    }
                    l(true, false, "Facebook Log In success.")
                    val request = GraphRequest.newMeRequest(
                        loginResult!!.accessToken
                    ) { json, response ->
                        Log.v("LoginActivity", response.toString())

                        val email = try{ json.getString("email") }
                        catch(e: JSONException){ null }

                        f(loginResult.accessToken, email)
                    }
                    val parameters = Bundle()
                    parameters.putString("fields", "id,name,email")
                    request.parameters = parameters
                    request.executeAsync()
                }

                override fun onCancel() {
                    Log.d("facebook", "cancle")
                    l(false, true, "Login canceled")
                }

                override fun onError(exception: FacebookException) {
                    Log.d("facebook", "error: $exception")
                    l(false, false, "Login failed: $exception")
                }
            })
        _permissions = permissions.toMutableList()
        setOnClickListener{
            LoginManager.getInstance().logInWithReadPermissions(context as Activity, permissions)
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}