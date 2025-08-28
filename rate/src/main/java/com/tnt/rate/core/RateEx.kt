package com.tnt.rate.core

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.ReviewManagerFactory
import com.tnt.rate.core.RateUtils.disableExposure
import com.tnt.rate.core.RateUtils.getCurrentVersion
import com.tnt.rate.core.RateUtils.getDeviceInfo
import com.tnt.rate.ui.BaseDialog

fun TextView.setTextColorSafe(colorResOrInt: Int?) {
    runCatching {
        if (colorResOrInt == null) return
        try {
            val colorInt = ContextCompat.getColor(context, colorResOrInt)
            setTextColor(colorInt)
        } catch (e: Exception) {
            setTextColor(colorResOrInt)
        }
    }
}

fun TextView.setTextSafe(idRes: Int?) {
    runCatching {
        if (idRes == null) return
        runCatching {
            text = context.getString(idRes)
        }.onFailure {
            text = idRes.toString()
        }
    }
}

fun ImageView.setImageSafe(source: Any?) {
    runCatching {
        if (source == null) return

        when (source) {
            is Int -> {
                try {
                    val typeName = runCatching { resources.getResourceTypeName(source) }.getOrNull()
                    when (typeName) {
                        "drawable", "mipmap" -> {
                            Glide.with(context).load(source).into(this)
                        }

                        "color" -> {
                            val colorInt = ContextCompat.getColor(context, source)
                            Glide.with(context).load(colorInt.toDrawable()).into(this)
                        }

                        else -> {
                            Glide.with(context).load(source.toDrawable()).into(this)
                        }
                    }
                } catch (e: Exception) {
                    Glide.with(context).load(source.toDrawable()).into(this)
                }
            }

            is ColorDrawable -> Glide.with(context).load(source).into(this)
            else -> Glide.with(context).load(source).into(this)
        }
    }
}

fun View.setBackgroundSafe(bgResOrInt: Int?) {
    runCatching {
        if (bgResOrInt == null) return
        try {
            val typeName = runCatching { resources.getResourceTypeName(bgResOrInt) }.getOrNull()
            when (typeName) {
                "drawable", "mipmap", "color" -> setBackgroundResource(bgResOrInt)
                null -> setBackgroundColor(bgResOrInt)
            }
        } catch (e: Exception) {
            setBackgroundColor(bgResOrInt)
        }
    }
}

fun BaseDialog.openBrowser(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
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

fun BaseDialog.sendFeedback(email: String, reason: String) {
    val addresses = arrayOf(email)
    val body = "${RateManager.appName} ver ${getCurrentVersion()} - feedback"
    disableExposure()

    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
        data = "mailto:".toUri()
        putExtra(Intent.EXTRA_EMAIL, addresses)
        putExtra(Intent.EXTRA_SUBJECT, body)
        putExtra(
            Intent.EXTRA_TEXT,
            body + "\n$reason" + "\n\n\n" + "DEVICE INFORMATION (Device information is useful for application improvement and development)" + "\n\n" + getDeviceInfo()
        )
    }
    try {
        context.startActivity(emailIntent)
    } catch (e: Exception) {
        try {
            context.startActivity(Intent.createChooser(emailIntent, "Send email..."))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
