package com.tnt.ratenewdev

import android.app.Application
import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.View
import com.tnt.rate.core.RateCallback
import com.tnt.rate.core.RateManager
import com.tnt.rate.core.RateUtils
import com.tnt.rate.model.DisableType
import com.tnt.rate.model.FeedbackReason
import com.tnt.rate.model.IntervalType
import com.tnt.rate.model.RateConfig
import com.tnt.rate.model.RateOption
import com.tnt.rate.model.UiConfig

object RateUtils {

    fun init(context: Application) {
        if (RateManager.isInit) return

        val config = buildConfig()
        RateManager.init(context, true, config, BuildConfig.DEBUG)
    }

    fun showRate(context: Context, isShowNow: Boolean = false) {
        runCatching {
            RateManager.showRate(
                context, isShowNow, createCallback(
                onRate = { star, isSubmit ->


                },
                onFeedback = { message, text, isSubmit ->

                }
            ))
        }
    }

    fun showFeedback(context: Context, isShowNow: Boolean = false) {
        runCatching {
            RateManager.showFeedBack(
                context, isShowNow, createCallback(
                    onFeedback = { message, text, isSubmit ->

                    }
                ))
        }
    }

    fun reset() = RateManager.reset()

    private fun buildConfig(): RateConfig {
        return RateConfig.Builder(
            appName = "Rate Example",
            packageId = "com.example.app",
            supportEmail = "support@example.com",
            rateOptions = getRateOptions(),
            feedbackReasons = getFeedbackReasons(),
            uiConfig = UiConfig(
                rateLayout = R.layout.dialog_rate,
                feedbackLayout = R.layout.dialog_feedback,
                feedbackItemLayout = R.layout.item_feedback,
                buttonRate = getButtonRateConfig(),
                buttonFeedback = getButtonFeedbackConfig()
            )
        )
            .setMinSession(0)
//          .setSessionInterval()
//           .setMaxShowPerSession()
            .setMinIntervalMillis(10000, IntervalType.GLOBAL)
//            .setMaxTotalShow()
            .setDisableAfterStars(4)
            .setDisableType(DisableType.SESSION)
            .setOpenInAppReviewAfterStars(4)
            .setMaxStarsForFeedback(4)
            .setDisableOpenInAppReview(false)
//            .setCustomShowCondition { !RateManager.isRated }
            .setForceShowCondition { false }
            .build()
    }

    private fun createCallback(
        onRate: ((star: Int, isSubmit: Boolean) -> Unit)? = null,
        onFeedback: ((message: String, text: String, isSubmit: Boolean) -> Unit)? = null,
        onShowDialog: ((dialog: Dialog, view: View) -> Unit)? = null,
        onDismiss: ((isDialogRate: Boolean) -> Unit)? = null,
        onReviewInApp: ((isSuccess: Boolean, message: String) -> Unit)? = null,
    ): RateCallback {
        return object : RateCallback {
            override fun onRate(star: Int, isSubmit: Boolean) {
                Log.d("RATE_CONFIG", "onRate: star=$star, isSubmit=$isSubmit")
                if (isSubmit) {
//                    Tracking.logParams("hit_submit_dialog_send_rate") {
//                        param("star", count.toString())
//                    }
                }
                onRate?.invoke(star, isSubmit)
            }

            override fun onFeedBack(count: Int, message: String, text: String, isSubmit: Boolean) {
                Log.d("RATE_CONFIG", "onFeedBack: message=$message, text=$text, isSubmit=$isSubmit")
                if (isSubmit) {
//                    Tracking.logParams("hit_submit_dialog_feedback") {
//                        param("count", count.toString())
//                        param("feedback_reason", message)
//                        param("original_feedback", text)
//                        param("device", RateUtils.getDeviceInfo())
//                        param("country", Locale.getDefault().country)
//                    }
                }
                onFeedback?.invoke(message, text, isSubmit)
            }

            override fun onShowDialog(dialog: Dialog, view: View) {
                Log.d("RATE_CONFIG", "onShowDialog: dialog=$dialog, view=$view")
                onShowDialog?.invoke(dialog, view)
            }

            override fun onDismissDialog(isDialogRate: Boolean) {
                Log.d("RATE_CONFIG", "onDismissDialog: isDialogRate=$isDialogRate")
                onDismiss?.invoke(isDialogRate)
            }

            override fun showReviewInApp(isSuccess: Boolean, message: String) {
                Log.d("RATE_CONFIG", "showReviewInApp:$isSuccess, msg=$message")
                onReviewInApp?.invoke(isSuccess, message)
            }
        }
    }

    private fun getButtonRateConfig() = UiConfig.OptionButtonConfig(
        textSelected = R.string.btn_rate_selected,
        bgSelected = R.drawable.btn_submit,
        textColorSelected = R.color.white,
    )

    private fun getButtonFeedbackConfig() = UiConfig.OptionButtonConfig(
        textSelected = R.string.btn_rate_selected,
        bgSelected = R.drawable.btn_submit,
        textColorSelected = R.color.white,
    )

    private fun getFeedbackReasons(): List<FeedbackReason> = listOf(
        FeedbackReason(
            title = R.string.feedback_reason_1,
            icSelected = R.drawable.iv_preview_rate_1,
            icUnselected = R.drawable.iv_preview_rate_2,
        ),
        FeedbackReason(
            title = R.string.feedback_reason_2,
            icSelected = R.drawable.iv_preview_rate_1,
            icUnselected = R.drawable.iv_preview_rate_2,
        ),
        FeedbackReason(
            title = R.string.feedback_reason_3,
            icSelected = R.drawable.iv_preview_rate_1,
            icUnselected = R.drawable.iv_preview_rate_2,
            requireInput = true
        )
    )

    private fun getRateOptions(): List<RateOption> {
        return RateUtils.getRateOptions(
            messages = listOf(
                R.string.rate_message_df,
                R.string.rate_message_1,
                R.string.rate_message_2,
                R.string.rate_message_3,
                R.string.rate_message_4,
                R.string.rate_message_5
            ),
            previews = listOf(
                R.drawable.iv_preview_rate,
                R.drawable.iv_preview_rate_1,
                R.drawable.iv_preview_rate_2,
                R.drawable.iv_preview_rate_3,
                R.drawable.iv_preview_rate_4,
                R.drawable.iv_preview_rate_5
            ),
            starFullDf = R.drawable.ic_star,
            starEmptyDf = R.drawable.ic_un_star_up
        )
    }
}
