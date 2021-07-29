package com.example.templates.base

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.example.templates.databinding.SuperLayoutBinding
import com.example.templates.utils.Session

open class SuperActivity : AppCompatActivity() {
    private val superview: SuperLayoutBinding by lazy {
        SuperLayoutBinding.inflate(layoutInflater)
    }
    private var views = mutableListOf<View>()
    private lateinit var loader: ConstraintLayout
    private var _isLoading = false
    var isLoading: Boolean
    get() = _isLoading
    set(v){
        _isLoading = v
        loader.visibility = if(v) View.VISIBLE else View.GONE
        views.forEach{it.isEnabled = !v}
    }

    fun registerUI(vararg views: View){
        this.views.addAll(views)
    }

    fun getColour(value: Int): Int = ResourcesCompat.getColor(resources, value, null)

    fun deregisterUI(vararg views: View){
        this.views.removeAll(views)
    }
    fun notify(title: String, message: String){
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK"){d,i ->
                d.dismiss()
            }
            .show()
    }

    internal open fun setAppTitle(): String?{
        return null
    }

    var isLoginScreen = false
    internal var doubleBackToExit = false
    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce || !doubleBackToExit) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Press Back again to exit.", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }
    open fun afterLoad(){

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Session.setup(this)

    }
    override fun setContentView(@IdRes layoutResID: Int) {
        val subview = findViewById<View>(layoutResID)
        subview.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        if(setAppTitle() != null){
            superview.appBarTitle.text = setAppTitle()
        }else{
            superview.root.removeView(superview.toolbarMain)
        }
        superview.subviewContainer.addView(subview)
        superview.subviewContainer.invalidate()
        superview.loader.let {
            it.visibility = View.GONE
            loader = it
        }
        super.setContentView(superview.root)
    }

    override fun setContentView(subview: View?) {
        subview?.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        if(setAppTitle() != null){
            superview.appBarTitle.text = setAppTitle()
        }else{
            superview.root.removeView(superview.toolbarMain)
        }
        superview.subviewContainer.addView(subview)
        superview.subviewContainer.invalidate()
        superview.loader.let {
            it.visibility = View.GONE
            loader = it
        }
        super.setContentView(superview.root)
        superview.root.invalidate()
    }
}