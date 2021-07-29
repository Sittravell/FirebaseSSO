package com.example.templates.login

import android.os.Bundle
import com.example.templates.base.SuperActivity
import com.example.templates.databinding.PhoneLoginLayoutBinding
import com.example.templates.utils.Navigate
import com.example.templates.utils.inputUtils.PhoneNumberVal
import com.example.templates.utils.inputUtils.RequiresAdvanceTextInputLayout

class PhoneLoginActivity: SuperActivity(), RequiresAdvanceTextInputLayout {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = PhoneLoginLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.error.text = ""
        binding.submit.setOnClickListener{
            var c = true
            var phoneNumber : String
            with(binding){
                error.text = ""
                phoneNumber = areaCode.value + phone.value
                PhoneNumberVal.validate(phoneNumber){ s, m->
                    if(!s){
                        c=s
                        error.text = m
                    }
                }
            }
            if(c) {
                VerifyPhone.area = binding.areaCode.value!!
                VerifyPhone.phone = binding.phone.value!!
                VerifyPhone.fullPhone = phoneNumber
                Navigate.push(this, VerifyPhone::class.java)
            }
        }
    }
}