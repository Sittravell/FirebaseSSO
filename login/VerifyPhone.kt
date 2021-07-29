package com.example.templates.login

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.templates.R
import com.example.templates.base.SuperActivity
import com.example.templates.databinding.VerificationCodeLayoutBinding
import com.example.templates.utils.inputUtils.RequiresETUtils
import com.example.templates.utils.sso.PhoneLogin
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputLayout

class VerifyPhone: SuperActivity(), PhoneLogin.Subscriber, RequiresETUtils {
    companion object{
        lateinit var phone: String
        lateinit var area: String
        lateinit var fullPhone: String
    }
    lateinit var binding: VerificationCodeLayoutBinding
    lateinit var TIList: List<TextInputLayout>
    lateinit var phoneLogin: PhoneLogin
    var verCode: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = VerificationCodeLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        phoneLogin = PhoneLogin()
        with(binding){
            c1.setTxt("")
            c2.setTxt("")
            c3.setTxt("")
            c4.setTxt("")
            c5.setTxt("")
            c6.setTxt("")
            desc.text = "We have sent you an SMS on $area $phone with 6 digit verification code."
        }

        with(binding){
            TIList = listOf(c1,c2,c3,c4,c5,c6)
            c1.changeFocusAfter(c2, 1)
            c2.changeFocusAfter(c3, 1)
            c3.changeFocusAfter(c4, 1)
            c4.changeFocusAfter(c5, 1)
            c5.changeFocusAfter(c6, 1)
            TIList.forEach { t ->
                t.onMaxCharacters(1){_ ->
                    var c = true
                    TIList.forEach{
                        if(it.value?.length ?: 0 < 1) c = false
                    }
                    if(c){
                        isLoading = true
                        error.text = ""
                        phoneLogin.manualLogin(
                            TIList.run{
                                var rCode = ""
                                val success = true
                                forEach {
                                    if (success && it.value != null && it.value?.length ?: 0 == 1){
                                        rCode += it.value!!
                                    }else{
                                        rCode = ""
                                    }
                                }
                                rCode
                            }
                        )
                    }
                }
            }
            registerUI(c1,c2,c3,c4,c5,c6)

            resend.setOnClickListener {
                error.text = ""
                phoneLogin.resend()
            }
        }

        phoneLogin.verify(this, this, fullPhone)
    }

    override fun onTimeOutCode() {
        super.onTimeOutCode()
        binding.error.text = "We are unable to send you the verification code. Tap Resend to try again."
    }

    override fun onPhoneSignIn(success: Boolean, message: String, code: String) {
        Log.d("onPhoneSignIn", "success: $success, message: $message, code: $code")
        isLoading = false
        if(success){
            TIList.forEach {
                it.isEnabled = false
                it.boxBackgroundColor = getColour(R.color.colorPrimary)
                it.editText?.setTextColor(getColour(R.color.white))
                (it.parent as MaterialCardView).setCardBackgroundColor(getColour(R.color.colorPrimary))
            }
            Handler(Looper.getMainLooper()).postDelayed({
                /* REPLACE: Verification is successful. Replace these with your desired code*/
//                Session.start(User("test", "test", "test", "test"))
//                Navigate.reset(this, BaseActivity::class.java)
                TIList.forEach {
                    it.boxBackgroundColor = getColour(R.color.white)
                    it.setTxt("")
                    (it.parent as MaterialCardView).setCardBackgroundColor(getColour(R.color.white))
                }
            }, 1500)
        }else{
            binding.error.text = "Invalid code entered"
        }
    }

    override fun onCodeRetrieved(success: Boolean, message: String, code: String) {
        Log.d("verificationcode", "success: $success, message: $message, code: $code")
        if(success && code.isNotEmpty()){
            code.forEachIndexed {i,c ->
                TIList[i].run{
                    setTxt(c.toString())
                    isEnabled = false


                }
            }
        }
    }
}