package com.example.templates.base

import androidx.fragment.app.Fragment

open class SuperFrag: Fragment(){
    private var onResumeTriggered = false
    override fun onResume() {
        super.onResume()
        if(onResumeTriggered)
            loadWhenVisible()
        onResumeTriggered = true
    }

    internal open fun loadWhenVisible(){}
}