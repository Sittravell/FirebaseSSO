package com.example.templates.utils

import android.util.Log

interface RequiresLogger{
    val TAG: String
    fun log(message: String){
        Log.d(TAG, message)
    }
}