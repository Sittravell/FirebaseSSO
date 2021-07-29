package com.example.templates.utils.inputUtils

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout

interface RequiresETUtils : RequiresAdvanceTextInputLayout {
    fun TextInputLayout.changeFocusAfter(t: TextInputLayout, maxChar: Int){
        Log.d("focustest", "Setting change focus of ${this.id} to ${t.id} after $maxChar char")
        editText?.addTextChangedListener {
            if(it?.length ?: 0 >= maxChar){
                t.editText?.requestFocus()
            }
        }
    }

    fun TextInputLayout.onMaxCharacters(maxChar: Int, l:(TextInputLayout) -> Unit){
        val runIfMaxChar = { length: Int? ->
            if(length ?: 0 >= maxChar){
                l(this)
            }
        }
        editText?.addTextChangedListener { runIfMaxChar(it?.length) }

        editText?.setOnFocusChangeListener { _,f -> if(f) runIfMaxChar(value?.length)}
    }

    fun TextInputLayout.validate(
        customValidation: ValidatesET? = null,
        l: ((Boolean, String) -> Unit)? = null
    ) {
        setup()
        val emptyMess = "$hint wajib diisi."
        if (editText == null) {
            l?.invoke(false, emptyMess)
            helperText = emptyMess
            return
        }
        if (editText!!.text.isNullOrEmpty()) {
            l?.invoke(false, emptyMess)
            helperText = emptyMess
            return
        }
        customValidation?.validate(this) { s, m ->
            if (!s) helperText = m
            l?.invoke(s, m)
        }
    }
    
    fun TextInputLayout.optional(
        customValidation: ValidatesET? = null,
        l: ((Boolean, String) -> Unit)? = null
    ) {
        setup()
        if(editText != null && !editText!!.text.isNullOrEmpty()) {
            customValidation?.validate(this) { s, m ->
                if (!s) helperText = m
                l?.invoke(s, m)
            }
        }
    }

    fun TextInputLayout.setup() {
        setHelperTextColor(ColorStateList.valueOf(Color.RED))
        helperText = ""
    }
}