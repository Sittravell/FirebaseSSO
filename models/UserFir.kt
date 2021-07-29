package com.example.templates.models

import android.util.Log
import com.example.templates.utils.*
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserFir(var user: User) : RequiresLogger{
    override val TAG = "UserFir"
    companion object: RequiresLogger {
        /* Firebase Keys */
        const val collectionK = "users"
        const val usernameK = "username"
        const val nameK = "name"
        const val passwordK = "password"
        const val emailK = "email"
        const val roleK = "role"
        const val imageK = "image"

        const val role = "user"
        override val TAG = "UserFir"
        val db = Firebase.firestore
        
        fun checkUsernameDuplication(username: String, l: GenericCB? = null) {
            db.collection(collectionK)
                .whereEqualTo(usernameK, username)
                .get()
                .addOnSuccessListener {
                    if (it.size() > 0) {
                        log("Username already exist")
                        l?.invoke(false, "Username already exist")
                    } else {
                        log( "Username is valid")
                        l?.invoke(true, "Username is valid")
                    }
                }
                .addOnFailureListener {
                    log("Unable to query for user")
                    l?.invoke(false, "Unable to query for user")
                }
        }

        fun getUser(username: String, l: UserFirCB) {
            db.collection(collectionK)
                .whereEqualTo(usernameK, username)
                .get()
                .addOnSuccessListener {
                    if (it.isEmpty) {
                        log("User not found")
                        l(false, "User not found", null)
                    } else {
                        it.first().data.let { data ->
                            Log.d("usertest", data.toString())
                            val user = UserFir(User(
                                username = data[usernameK] as String,
                                name = data[nameK] as String,
                                role =  data[roleK] as String,
                                email = data[emailK] as String
                            ))
                            user.reference = it.first().reference
                            log("Successfully retrieved user")
                            l(true, "Successfully retrieved user", user)
                        }
                    }
                }
                .addOnFailureListener {
                    l(false, "Unable to retrieve data", null)
                }
        }
    }

    /* NOTE: Hold Firebase reference */
    internal var reference: DocumentReference? = null
    internal val referenceNullError = "User not saved yet and requires a reference"

    fun save(l: GenericCB? = null) {
        val addUser = {
            db.collection(collectionK)
                .add(user)
                .addOnSuccessListener {
                    reference = it
                    l?.invoke(true, "Successfully registered user")
                    log("Successfully registered user")
                }
                .addOnFailureListener {
                    l?.invoke(true, "Failed to register user: $it")
                }
        }
        checkUsernameDuplication(user.username) { s, m ->
            if (!s) l?.invoke(s, m) else addUser()
        }
    }

    fun getReference(){
        val fHandler = { m: String ->
            log("get reference fail: $m")
        }
        db.collection(collectionK)
            .whereEqualTo(usernameK, user.username)
            .get()
            .addOnSuccessListener {
                if (it.isEmpty){
                    fHandler("no user found")
                }else{
                    reference = it.first().reference
                }
            }
            .addOnFailureListener {
                fHandler(it.toString())
            }
    }

    fun updateUser(l: ((Boolean, String) -> Unit)? = null) {
        getUser(user.username) { s, m, u ->
            if (s && u!=null) {
                user = u.user
            }
            l?.invoke(s, m)
        }
    }

}

