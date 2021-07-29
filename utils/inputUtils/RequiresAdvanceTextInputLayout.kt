package com.example.templates.utils.inputUtils

import com.google.android.material.textfield.TextInputLayout

interface RequiresAdvanceTextInputLayout {
    val TextInputLayout.value: String?
        get() = editText?.text.toString()

    fun TextInputLayout.setTxt(value: String) = editText?.setText(value)
}