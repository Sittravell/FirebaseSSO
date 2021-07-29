package com.example.templates.utils.inputUtils

import com.example.templates.utils.GenericCB
import com.google.android.material.textfield.TextInputLayout

interface ValidatesET : RequiresAdvanceTextInputLayout {
    fun validate(t: TextInputLayout, l: (GenericCB))
    fun String.isPostiveNumber(): Boolean {
        val number = toIntOrNull()
        return number != null && number >= 0
    }
    fun String.isPositiveFloat(): Boolean {
        val float = toFloatOrNull()
        return float != null && float >= 0
    }
    fun String.isValidMonth(): Boolean = isPostiveNumber() && length <= 12
    fun String.isValidYear(): Boolean = isPostiveNumber()
}