package com.tnt.rate.core

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.ReviewManagerFactory
import com.tnt.rate.ui.BaseDialog

fun TextView.setTextColorSafe(textColorRes: Int?) {
    if (textColorRes == null) return
    this.setTextColor(textColorRes)
}

fun TextView.setTextSafe(idRes: Int?) {
    if (idRes == null) return
    runCatching {
        this.text = context.getString(idRes)
    }
}

fun ImageView.setImageSafe(drawableRes: Int?) {
    if (drawableRes != null) {
        Glide.with(context).load(drawableRes).into(this)
    }
}

fun View.setBackgroundSafe(bgRes: Int?) {
    if (bgRes == null) return
    this.setBackgroundResource(bgRes)
}

fun TextView.setBackgroundSafe(bgRes: Int?) {
    if (bgRes == null) return
    this.setBackgroundResource(bgRes)
}

fun BaseDialog.showReviewInApp(onSuccess: (Boolean, String) -> Unit) {
    val manager = ReviewManagerFactory.create(context)
    val request = manager.requestReviewFlow()
    request.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            onSuccess(true, "Review flow finished (user may or may not have submitted).")
        } else {
            val reviewError = (task.exception as? ReviewException)?.message ?: "Unknown error"
            onSuccess(false, reviewError)
        }
    }
}