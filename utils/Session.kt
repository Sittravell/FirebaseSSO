
/*
    REPLACE: With your package id
    IMPORT: User
 */
package com.example.templates.utils
import com.example.templates.models.User
import android.app.Activity
import android.util.Log
import com.example.templates.models.UserFir

object Session: RequiresLogger{
    override val TAG = "Session"
    const val NO_USERNAME = "NO_USERNAME"
    const val LOGGED_IN = 1
    const val LOGGED_OUT = 2

    /* STORAGE KEYS */
    val statusK = "status"
    val usernameK = "username"

    var status = LOGGED_OUT
    private var _user: User? = null
    var username: String? = null
    lateinit var storage: Storage

    val isLoggedIn: Boolean
        get(){
            return status == LOGGED_IN
        }

    var user: User?
        get() = _user
        set(u){
            Log.d(TAG, "User: ${u?.username}")
            _user = u
        }

    /* NOTE: Needs to be executed on every activity that uses it. This is to initialize Storage. */
    /* NOTE: If activity extends SuperActivity, activity does not need to setup */
    fun setup(activity: Activity, l: GenericCB? = null){
        if (!Session::storage.isInitialized) {
            storage = Storage(activity)
            status = storage.get(statusK) ?: LOGGED_OUT
            storage.get<String?>(usernameK).let{
                if(it != null && it != NO_USERNAME){
                    username = it

                    /* IMPORTANT / REMOVE / REPLACE : This is used to start session using Firebase. */
                    UserFir.getUser(it){ s, m, u ->
                        u?.let{ start(it.user) }
                        l?.invoke(s, m)
                    }

                }else{
                    log("No username found")
                    l?.invoke(true, "No username found")
                }
            }
        }
    }


    fun start(user: User){
        username = user.username
        _user = user
        status = LOGGED_IN
        storage.set(statusK, LOGGED_IN)
        storage.set(usernameK, user.username)
    }

    /* NOTE: Mock User */
    fun mock(){
        user = User("username", "name", "email", "role")
    }

    fun finish(){
        username = null
        _user = null
        status = LOGGED_OUT
        storage.set(statusK, LOGGED_OUT)
        storage.set(usernameK, NO_USERNAME)
    }
}

