package com.tnt.ratenewdev

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.View
import com.tnt.rate.core.RateCallback
import com.tnt.rate.core.RateManager
import com.tnt.rate.model.FeedbackReason
import com.tnt.rate.model.OptionButtonConfig
import com.tnt.rate.model.RateConfig
import com.tnt.rate.model.RateConfig.DisableType
import com.tnt.rate.model.RateOption

object RateUtils {

    fun init(context: Context) {
        if (!RateManager.isInit) {
            val config = RateConfig.Builder(
                packageId = "com.example.app",
                supportEmail = "support@example.com",
                rateOptions = getRateOptions(),
                feedbackReasons = getFeedbackReasons(),
                layoutConfig = RateConfig.LayoutConfig(
                    rateLayout = R.layout.dialog_rate,
                    feedbackLayout = R.layout.dialog_feedback,
                    feedbackItemLayout = R.layout.item_feedback
                )
            )
                .setMinSession(1)
                .setMaxShowPerSession(2)
                .setMinIntervalMillis(5000)
                .setMaxTotalShow(5)
                .setDisableAfterStars(4)
                .setDisableType(DisableType.FOREVER)
                .setOpenInAppReviewAfterStars(4)
                .setButtonRate(
                    OptionButtonConfig(
                        textSelected = R.string.btn_rate_selected,
                        backgroundSelected = com.tnt.rate.R.drawable.btn_submit,
                        textColorSelected = R.color.white
                    )
                )
                .setButtonFeedback(
                    OptionButtonConfig(
                        textSelected = R.string.btn_rate_selected,
                        backgroundSelected = com.tnt.rate.R.drawable.btn_submit,
                        textColorSelected = R.color.white
                    )
                )
                .build()
            RateManager.init(context, true, config)
        }
    }

    private fun getFeedbackReasons(): List<FeedbackReason> {
        return listOf(
            FeedbackReason(
                title = R.string.feedback_reason_good,
                iconSelect = com.tnt.rate.R.drawable.iv_preview_rate_1,
                iconUnselect = com.tnt.rate.R.drawable.iv_preview_rate_2
            ),
            FeedbackReason(
                title = R.string.feedback_reason_crash,
                iconSelect = com.tnt.rate.R.drawable.iv_preview_rate_1,
                iconUnselect = com.tnt.rate.R.drawable.iv_preview_rate_2
            ),
            FeedbackReason(
                title = R.string.feedback_reason_missing_feature,
                iconSelect = com.tnt.rate.R.drawable.iv_preview_rate_1,
                iconUnselect = com.tnt.rate.R.drawable.iv_preview_rate_2,
                requireInput = true
            )
        )
    }

    private fun getRateOptions(): List<RateOption> {
        return listOf(
            RateOption(
                iconPreview = com.tnt.rate.R.drawable.iv_preview_rate,
                messageRes = R.string.rate_message_df,
                isDefault = true
            ),
            RateOption(
                iconPreview = com.tnt.rate.R.drawable.iv_preview_rate_1,
                starFullIcon = com.tnt.rate.R.drawable.ic_star,
                starEmptyIcon = com.tnt.rate.R.drawable.ic_un_star_up,
                messageRes = R.string.rate_message_1
            ),
            RateOption(
                iconPreview = com.tnt.rate.R.drawable.iv_preview_rate_2,
                starFullIcon = com.tnt.rate.R.drawable.ic_star,
                starEmptyIcon = com.tnt.rate.R.drawable.ic_un_star_up,
                messageRes = R.string.rate_message_2
            ),
            RateOption(
                iconPreview = com.tnt.rate.R.drawable.iv_preview_rate_3,
                starFullIcon = com.tnt.rate.R.drawable.ic_star,
                starEmptyIcon = com.tnt.rate.R.drawable.ic_un_star_up,
                messageRes = R.string.rate_message_3
            ),
            RateOption(
                iconPreview = com.tnt.rate.R.drawable.iv_preview_rate_4,
                starFullIcon = com.tnt.rate.R.drawable.ic_star,
                starEmptyIcon = com.tnt.rate.R.drawable.ic_un_star_up,
                messageRes = R.string.rate_message_4
            ),
            RateOption(
                iconPreview = com.tnt.rate.R.drawable.iv_preview_rate_5,
                starFullIcon = com.tnt.rate.R.drawable.ic_star,
                starEmptyIcon = com.tnt.rate.R.drawable.ic_un_star_up,
                messageRes = R.string.rate_message_5
            ),
        )
    }

    fun showRate(context: Context, isShowNow: Boolean = false) {
        runCatching {
            RateManager.showRate(context, isShowNow, object : RateCallback {
                override fun onRate(star: Int, isSubmit: Boolean) {
                    Log.d("RateLog", "onRate: star=$star, isSubmit=$isSubmit")
                }

                override fun onFeedBack(message: String, text: String, isSubmit: Boolean) {
                    Log.d("RateLog", "onFeedBack: message=$message, text=$text, isSubmit=$isSubmit")
                }

                override fun onShowDialog(dialog: Dialog, view: View) {
                    Log.d("RateLog", "onShowDialog: dialog=$dialog, view=$view")
                }

                override fun onDismissDialog(isDialogRate: Boolean) {
                    Log.d("RateLog", "onDismissDialog: isDialogRate=$isDialogRate")
                }

                override fun showReviewInApp(isSuccess: Boolean, message: String) {

                }
            })
        }
    }

    fun showFeedback(context: Context, isShowNow: Boolean = false) {
        runCatching {
            RateManager.showFeedBack(context, isShowNow, object : RateCallback {
                override fun onRate(star: Int, isSubmit: Boolean) {
                    Log.d("FeedbackLog", "onRate: star=$star, isSubmit=$isSubmit")
                }

                override fun onFeedBack(message: String, text: String, isSubmit: Boolean) {
                    Log.d(
                        "FeedbackLog",
                        "onFeedBack: message=$message, text=$text, isSubmit=$isSubmit"
                    )
                }

                override fun onShowDialog(dialog: Dialog, view: View) {
                    Log.d("FeedbackLog", "onShowDialog: dialog=$dialog, view=$view")
                }

                override fun onDismissDialog(isDialogRate: Boolean) {
                    Log.d("FeedbackLog", "onDismissDialog: isDialogRate=$isDialogRate")
                }

                override fun showReviewInApp(isSuccess: Boolean, message: String) {

                }
            })
        }
    }

    fun reset(context: Context) {
        RateManager.reset(context)
     }
}
