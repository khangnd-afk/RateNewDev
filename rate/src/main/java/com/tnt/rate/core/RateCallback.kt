package com.tnt.rate.core

import android.app.Dialog
import android.view.View

interface RateCallback {
    fun onRate(star: Int, isSubmit: Boolean)
    fun onFeedBack(message: String, text: String, isSubmit: Boolean)

    fun onShowDialog(dialog: Dialog, view: View)

    fun onDismissDialog(isDialogRate: Boolean)

    fun showReviewInApp(isSuccess: Boolean, message: String)
}