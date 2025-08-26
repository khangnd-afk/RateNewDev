package com.tnt.rate.model

import androidx.annotation.Keep
import androidx.annotation.StringRes

@Keep
data class RateOption(
    val iconPreview: Int? = null,
    val starFullIcon: Int? = null,
    val starEmptyIcon: Int? = null,
    @StringRes val messageRes: Int,
    val isDefault: Boolean = false
)