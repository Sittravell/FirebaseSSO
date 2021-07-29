package com.example.templates.models

import android.graphics.Bitmap

data class Chat(
    var name: String? = null,
    var image: Bitmap? = null,
    var username: String,
    var messages: MutableList<Message> = mutableListOf()
)