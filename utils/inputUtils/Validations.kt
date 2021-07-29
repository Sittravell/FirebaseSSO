package com.example.templates.utils.inputUtils

import android.telephony.PhoneNumberUtils
import android.util.Log
import com.example.templates.utils.GenericCB
import com.google.android.material.textfield.TextInputLayout
import java.util.regex.Pattern

object YearVal: ValidatesET {
    override fun validate(t: TextInputLayout, l: GenericCB) {
        if(t.value!!.isPostiveNumber()){
            l(true, "Year is valid")
        }else{
            l(false, "Year has to be a positive number")
        }
    }
}

object MonthVal: ValidatesET {
    override fun validate(t: TextInputLayout, l: GenericCB) {
        if(t.value!!.isPostiveNumber() && t.value!!.toInt() <= 11){
            l(true, "Month is valid")
        }else{
            l(false, "Month must be between 0 - 11")
        }
    }
}

object PriceVal : ValidatesET {
    override fun validate(t: TextInputLayout, l: GenericCB) {
        if (t.value!!.isPositiveFloat() && t.value!!.matches("^[1-9]\\d*(((,\\d{3}){1})?(\\.\\d{0,2})?)\$".toRegex())) l(true, "Price is valid")
        else l(false, "Price is invalid. Price should consist 2 decimal places. E.g. 35.00")
    }
}

object SessionVal : ValidatesET {
    override fun validate(t: TextInputLayout, l: GenericCB) {
        when{
            !t.value!!.isPostiveNumber() && t.value!!.toInt() < 0-> l(false, "Session number must be at least 1")
            t.value!!.toInt() > 100 -> l(false, "Session number must be at most 100")
            else -> l(true, "Session is valid")
        }
    }
}

class PackageVal(private vararg val items: String): ValidatesET {
    override fun validate(t: TextInputLayout, l: GenericCB) {
        var v = true
        items.forEach {
            if (it.toLowerCase() == t.value!!.toLowerCase()) {
                v = false
            }
        }
        if (v) l(v, "Package is valid.") else l(v, "Package already exist.")
    }
}

class ServiceVal(private vararg val items: String): ValidatesET {
    override fun validate(t: TextInputLayout, l: GenericCB) {
        var v = true
        items.forEach{
            Log.d("servicevaltest", "${it.toLowerCase()} , ${t.value!!.toLowerCase()}")
            if(it.toLowerCase() == t.value!!.toLowerCase()) {
                v = false
                Log.d("servicevaltest", "failed")
            }
        }
        if (v) l(v, "Service is valid.") else l(v, "Service already exist.")
    }
}

object PhoneNumberVal : ValidatesET {
    override fun validate(t: TextInputLayout, l: GenericCB) {
        validate(t.value!!, l)
    }

    fun validate(phone: String, l: GenericCB){
        if (PhoneNumberUtils.isGlobalPhoneNumber(phone))
            l(true, "Phone number is valid")
        else l(false, "Phone number is not invalid")
    }
}

object PostCodeVal : ValidatesET {
    override fun validate(t: TextInputLayout, l: GenericCB) {
        if (t.value!!.isPostiveNumber()) l(true, "Postcode is valid") else l(
            false,
            "Postcode is invalid"
        )
    }
}

object EmailVal : ValidatesET {
    override fun validate(t: TextInputLayout, l: GenericCB) {
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(t.value!!).matches())
            l(true, "Email is valid")
        else l(false, "Invalid email provided.")
    }
}

object NameVal : ValidatesET {
    override fun validate(t: TextInputLayout, l: GenericCB) {
        if (t.value!!.length >= 4 && t.value!!.matches("^[a-zA-Z\\s]*$".toRegex()) && t.value!!.length < 20) l(
            true,
            "Name is valid"
        )
        else if(t.value!!.length < 4)
            l(false, "Name must contain at least 4 characters")
        else l(false, "Name must only contain alphabets and must contain lesser than 20 characters")
    }

}

object BusiNameVal : ValidatesET {
    override fun validate(t: TextInputLayout, l: GenericCB) {
        if (t.value!!.length < 20) l(true, "Business Name is valid")
        else l(false, "Business Name should be lesser than 20 characters")
    }

}

class PassConfVal(private val t2: TextInputLayout) : ValidatesET {
    override fun validate(t: TextInputLayout, l: GenericCB) {
        if (t.value!!.trim() != t2.value!!.trim())
            l(false, "Confirmation Password does not match Password")
        else {
            l(true, "Confirmation Password matches Password")
        }
    }
}

object UsernameVal : ValidatesET {
    override fun validate(t: TextInputLayout, l: GenericCB) {
        when {
            t.value!!.length < 8
            -> {
                l(false, "Username must have at least 8 characters");return
            }
            !Pattern.compile("^[A-Za-z0-9]+(?:[ _-][A-Za-z0-9]+)*\$").matcher(t.value!!).matches()
            -> {
                l(
                    false,
                    "Username can only consist of lowercase(a-z), uppercase(A-Z), numbers(0-9), underscore(_),hyphens(-) and spaces"
                );return
            }
            t.value!!.length > 64
            -> {
                l(false, "Username must have at least 8 characters");return
            }
            else
            -> {
                l(true, "Username valid.");return
            }
        }
    }

}

object PasswordVal : ValidatesET {
    override fun validate(t: TextInputLayout, l: GenericCB) {
        val hasUppercaseLowercaseAndNumber =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$").matcher(t.value!!).matches()
        if (!hasUppercaseLowercaseAndNumber or (t.value!!.length < 8))
            l(
                false,
                "Password must have at least 8 characters, " +
                        "includes the 3 of the following - uppercase (A-Z), " +
                        "lowercase (a-z) and numeric characters (0-9)"
            )
        else
            l(true, "Password is valid")

    }

}