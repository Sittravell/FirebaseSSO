package com.example.templates.utils

import android.app.Activity
import android.content.Intent
import java.io.Serializable

object Navigate{

    inline fun <reified T : Any> push(currentActivity: Activity, destination: Class<T>) = currentActivity.startActivity(Intent(currentActivity, destination))

    inline fun <reified T : Any> push(currentActivity: Activity, destination: Class<T>, data: Map<String, Serializable>) {
        val i = Intent(currentActivity, destination)
        data.forEach{ i.putExtra(it.key, it.value) }
        currentActivity.startActivity(i)
    }
    inline fun <reified T : Any> reset(currentActivity: Activity, destination: Class<T>) {
        val i = Intent(currentActivity, destination)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        currentActivity.startActivity(i)
        currentActivity.finish()
    }

}