/* REPLACE: With your package id */
package com.example.templates.utils

/* REMOVE / REPLACE: Check if imports are necessary */
import com.example.templates.models.User
import com.example.templates.models.UserFir

typealias GenericCB = (Boolean, String) -> Unit


/* REMOVE / REPLACE: Check if aliases are necessary */

/* CALLBACKS */
typealias UserCB = (Boolean, String, User?) -> Unit
typealias UserFirCB = (Boolean, String, UserFir?) -> Unit

/* MAPS */
typealias StringMap = MutableMap<String, String>
typealias AnyMap = MutableMap<String, Any>
typealias MapListOfAny = MutableMap<String,ListOfAny>

/* LISTS */
typealias ListOfStringMap = MutableList<StringMap>
typealias ListOfAny = MutableList<AnyMap>