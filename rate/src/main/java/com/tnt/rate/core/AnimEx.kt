package com.tnt.rate.core

import android.view.View
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo

fun View.bounceIn(time: Long = 350, onEnd: () -> Unit = {}) {
    YoYo.with(Techniques.BounceIn)
        .duration(time).onEnd {
            onEnd()
        }
        .playOn(this)
}

fun View.fadeInUp(time: Long = 350, onEnd: () -> Unit = {}) {
    YoYo.with(Techniques.FadeInUp)
        .duration(time).onEnd {
            onEnd()
        }
        .playOn(this)
}

fun View.zoomIn(time: Long = 350, onEnd: () -> Unit = {}) {
    YoYo.with(Techniques.ZoomIn)
        .duration(time).onEnd {
            onEnd()
        }
        .playOn(this)
}

fun View.fadeIn(time: Long = 350, onEnd: () -> Unit = {}) {
    YoYo.with(Techniques.FadeIn)
        .duration(time).onEnd {
            onEnd()
        }
        .playOn(this)
}
