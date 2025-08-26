package com.tnt.rate.ui

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.tnt.rate.R
import com.tnt.rate.core.RateCallback
import com.tnt.rate.core.RateManager
import com.tnt.rate.core.RatePrefs
import com.tnt.rate.core.StarRatingHelper
import com.tnt.rate.core.setBackgroundSafe
import com.tnt.rate.core.setImageSafe
import com.tnt.rate.core.setTextColorSafe
import com.tnt.rate.core.setTextSafe
import com.tnt.rate.core.showReviewInApp
import com.tnt.rate.model.OptionButtonConfig
import com.tnt.rate.model.RateOption

class RateDialog(
    context: Context,
    val layoutRes: Int,
    val rateOptions: List<RateOption>,
    val btnConfig: OptionButtonConfig?,
    val callback: RateCallback,
) : BaseDialog(context, layoutRes) {

    private var btnRate: TextView? = null
    private var ivClose: View? = null
    private var ivPreview: ImageView? = null
    private var tvDescription: TextView? = null

    private var starRatingHelper: StarRatingHelper? = null

    override fun initView(view: View) {
        btnRate = view.findViewById(R.id.btnRate)
        ivClose = view.findViewById(R.id.ivClose)
        ivPreview = view.findViewById(R.id.ivPreview)
        tvDescription = view.findViewById(R.id.tvDescription)

        initStars()
    }

    private fun initStars() {
        initBtnRate(false)
        ivPreview?.setImageSafe(rateOptions.firstOrNull()?.iconPreview)
        tvDescription?.setTextSafe(rateOptions.firstOrNull()?.messageRes)
        starRatingHelper = StarRatingHelper(
            stars = listOf(
                rootView.findViewById(R.id.ivStar1),
                rootView.findViewById(R.id.ivStar2),
                rootView.findViewById(R.id.ivStar3),
                rootView.findViewById(R.id.ivStar4),
                rootView.findViewById(R.id.ivStar5),
            ),
            rateOptions = rateOptions.takeLast(5),
            disable = false,
            onRatingChanged = { star, rateOption ->
                callback.onRate(star, false)
                ivPreview?.setImageSafe(rateOption.iconPreview)
                tvDescription?.setTextSafe(rateOption.messageRes)
                initBtnRate(true)
            },
        )
    }

    private fun initBtnRate(isEnable: Boolean) {
        btnRate?.isEnabled = isEnable

        if (btnConfig?.backgroundUnselected == null) {
            btnRate?.alpha = if (isEnable) 1f else 0.7f
        }

        if (isEnable) {
            btnRate?.setBackgroundSafe(btnConfig?.backgroundSelected)
            btnRate?.setTextColorSafe(btnConfig?.textColorSelected)
            btnRate?.setTextSafe(btnConfig?.textSelected)
        } else {
            ivPreview?.setImageSafe(rateOptions.firstOrNull()?.iconPreview)
            btnRate?.setBackgroundSafe(btnConfig?.backgroundUnselected)
            btnRate?.setTextColorSafe(btnConfig?.textColorUnselected)
            btnRate?.setTextSafe(btnConfig?.textUnselected)
        }
    }

    override fun initListener() {
        btnRate?.setOnClickListener {
            submitRate()
        }
        ivClose?.setOnClickListener {
            dismiss()
        }
    }

    private fun submitRate() {
        val star = starRatingHelper!!.currentRating
        callback.onRate(star, true)
        RatePrefs.saveLastStars(context, star)
        if (RateManager.config.openInAppReviewAfterStars >= star) {
            showReviewInApp { isSuccess, message ->
                callback.showReviewInApp(isSuccess, message)
                if (isSuccess) {
                    dismiss()
                } else {
                    dismiss()
                }
            }
        } else {
            RateManager.showFeedBack(context, true, callback)
            dismiss()
        }
    }

    override fun show() {
        super.show()
        runCatching {
            callback.onShowDialog(this, rootView)
        }
    }

    override fun dismiss() {
        super.dismiss()
        callback.onDismissDialog(true)
    }
}