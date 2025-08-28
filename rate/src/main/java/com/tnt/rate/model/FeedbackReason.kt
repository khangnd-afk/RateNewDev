package com.tnt.rate.model

import androidx.annotation.Keep
import androidx.annotation.StringRes
import java.util.UUID

@Keep
data class FeedbackReason(
    val id: String = UUID.randomUUID().toString(),
    @StringRes val title: Int,
    val icSelected: Int,
    val icUnselected: Int,
    val bgSelected: Int? = null,
    val bgUnselected: Int? = null,
    val textColorSelected: Int? = null,
    val textColorUnselected: Int? = null,
    val requireInput: Boolean = false,
)
