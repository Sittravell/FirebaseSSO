package com.example.templates.models

data class Message(
    var message: String,
    var datetime: String,
    var isSelf: Boolean,
    var read: Boolean
)