package com.tnt.rate.core

import android.widget.ImageView
import com.tnt.rate.model.RateOption

class StarRatingHelper(
    private val stars: List<ImageView>,
    private val rateOptions: List<RateOption>,
    private val disable: Boolean = false,
    private val onRatingChanged: ((Int, RateOption) -> Unit)? = null
) {
    var currentRating = 0

    init {
        stars.forEachIndexed { index, imageView ->
            imageView.setImageSafe(source = rateOptions[index].starEmptyIcon)
            imageView.zoomIn(450L + (100 * index))
            if (!disable) imageView.setOnClickListener {
                imageView.bounceIn(400)
                setRating(index + 1)
            }
        }
    }

    fun setRating(rating: Int, isDefault: Boolean = false) {
        currentRating = rating
        stars.forEachIndexed { index, imageView ->
            imageView.setImageSafe(
                source = if (index < rating) {
                    rateOptions[index].starFullIcon
                } else {
                    rateOptions[index].starEmptyIcon
                }
            )
        }
        if (!isDefault && rating > 0) {
            onRatingChanged?.invoke(currentRating, rateOptions[rating - 1])
        }
    }

    fun getRating(): Int = currentRating
}
