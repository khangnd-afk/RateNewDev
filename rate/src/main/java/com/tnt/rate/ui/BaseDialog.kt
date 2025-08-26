package com.tnt.rate.ui

import android.R
import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager

abstract class BaseDialog(
    context: Context,
    private val layoutRes: Int
) : Dialog(context) {

    protected lateinit var rootView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        rootView = LayoutInflater.from(context).inflate(layoutRes, null)
        setContentView(rootView)
        window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        window?.setBackgroundDrawableResource(R.color.transparent)
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        setCancelable(true)

        initView(rootView)
        initListener()
    }

    abstract fun initView(view: View)
    abstract fun initListener()
    override fun show() {
        super.show()
        val width = getScreenWidth() * 0.85
        window?.setLayout(width.toInt(), -2)
        window?.setBackgroundDrawableResource(R.color.transparent)
    }

    fun getScreenWidth(): Int {
        return Resources.getSystem().displayMetrics.widthPixels
    }

    fun showDialog() {
        if (!isShowing) show()
    }

    fun dismissDialog() {
        if (isShowing) dismiss()
    }
}
