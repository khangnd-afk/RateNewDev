package com.tnt.rate.model

import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import androidx.annotation.StringRes
import java.util.UUID

@Keep
data class FeedbackReason(
    val id: String = UUID.randomUUID().toString(),
    @StringRes val title: Int,
    val iconSelect: Int,
    val iconUnselect: Int,
    val requireInput: Boolean = false,
)
