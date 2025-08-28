package com.tnt.rate.model

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import androidx.annotation.LayoutRes

@Keep
enum class DisableType {
    SESSION,
    FOREVER
}

@Keep
enum class IntervalType {
    SESSION, GLOBAL
}

@Keep
data class RateConfig(
    val appName: String,
    val packageId: String,
    val supportEmail: String,
    val rateOptions: List<RateOption>,
    val feedbackReasons: List<FeedbackReason>,
    val uiConfig: UiConfig,

    val minSession: Int,
    val sessionInterval: Int,
    val maxShowPerSession: Int,
    val minIntervalMillis: Long,
    val intervalType: IntervalType,
    val maxTotalShow: Int,
    val disableAfterStars: Int,
    val disableType: DisableType,
    val openInAppReviewAfterStars: Int,
    val maxStarsForFeedback: Int,
    val disableOpenInAppReview: Boolean,
    val customCondition: (() -> Boolean)?,
    val forceShowCondition: (() -> Boolean)?
) {


    class Builder(
        private val appName: String,
        private val packageId: String,
        private val supportEmail: String,
        private val rateOptions: List<RateOption>,
        private val feedbackReasons: List<FeedbackReason>,
        private val uiConfig: UiConfig
    ) {
        private var minSession: Int = 0
        private var sessionInterval: Int = 0
        private var maxShowPerSession: Int = Int.MAX_VALUE
        private var minIntervalMillis: Long = 0
        private var intervalType: IntervalType = IntervalType.SESSION
        private var maxTotalShow: Int = Int.MAX_VALUE
        private var disableAfterStars: Int = 4
        private var disableType: DisableType = DisableType.SESSION
        private var openInAppReviewAfterStars: Int = 4
        private var maxStarsForFeedback: Int = 0
        private var disableOpenInAppReview: Boolean = false
        private var customCondition: (() -> Boolean)? = null
        private var forceShowCondition: (() -> Boolean)? = null

        /**
         * Số session tối thiểu trước khi dialog được phép hiển thị.
         */
        fun setMinSession(value: Int) = apply { this.minSession = value }

        /**
         * Khoảng cách giữa các lần session hiển thị dialog.
         * Ví dụ: 2 → cứ cách 2 session thì được phép hiển thị lại.
         */
        fun setSessionInterval(value: Int) = apply { this.sessionInterval = value }

        /**
         * Giới hạn số lần show trong 1 session.
         */
        fun setMaxShowPerSession(value: Int) = apply { this.maxShowPerSession = value }

        /**
         * Cấu hình khoảng thời gian tối thiểu (ms) giữa 2 lần hiển thị dialog.
         *
         * @param value thời gian tối thiểu tính bằng millisecond.
         * @param type kiểu khoảng thời gian cần áp dụng:
         *  - [IntervalType.SESSION] → giới hạn trong phạm vi session hiện tại.
          *
         *  - [IntervalType.GLOBAL] → giới hạn trên toàn bộ vòng đời ứng dụng (dữ liệu được lưu lại).
          *
          */
        fun setMinIntervalMillis(value: Long, type: IntervalType) = apply {
            this.minIntervalMillis = value
            this.intervalType = type
        }

        /**
         * Tổng số lần hiển thị tối đa trong suốt vòng đời app.
         */
        fun setMaxTotalShow(value: Int) = apply { this.maxTotalShow = value }

        /**
         * Nếu user chọn số sao >= giá trị này → disable dialog trong tương lai.
         */
        fun setDisableAfterStars(value: Int) = apply { this.disableAfterStars = value }

        /**
         * Kiểu disable khi đạt [disableAfterStars].
         * SESSION → chỉ trong session hiện tại.
         * FOREVER → vĩnh viễn.
         */
        fun setDisableType(value: DisableType) = apply { this.disableType = value }

        /**
         * Nếu số sao >= giá trị này → mở In-App Review.
         */
        fun setOpenInAppReviewAfterStars(value: Int) =
            apply { this.openInAppReviewAfterStars = value }

        /**
         * Nếu số sao <= giá trị này → mở dialog feedback.
         */
        fun setMaxStarsForFeedback(value: Int) = apply { this.maxStarsForFeedback = value }

        /**
         * Nếu true → KHÔNG mở In-App Review mà chuyển thẳng sang Store.
         */
        fun setDisableOpenInAppReview(value: Boolean) =
            apply { this.disableOpenInAppReview = value }

        /**
         * Điều kiện custom bổ sung.
         * - return true → tiếp tục check các điều kiện khác.
         * - return false → dialog không hiển thị.
         */
        fun setCustomShowCondition(block: () -> Boolean) = apply { this.customCondition = block }

        /**
         * Điều kiện ép show (bỏ qua toàn bộ điều kiện khác).
         * - return true → show ngay lập tức.
         */
        fun setForceShowCondition(block: () -> Boolean) = apply { this.forceShowCondition = block }

        fun build(): RateConfig = RateConfig(
            appName,
            packageId,
            supportEmail,
            rateOptions,
            feedbackReasons,
            uiConfig,
            minSession,
            sessionInterval,
            maxShowPerSession,
            minIntervalMillis,
            intervalType,
            maxTotalShow,
            disableAfterStars,
            disableType,
            openInAppReviewAfterStars,
            maxStarsForFeedback,
            disableOpenInAppReview,
            customCondition,
            forceShowCondition
        )
    }
}

/**
 * ===============================
 * UiConfig
 * ===============================
 */
@Keep
data class UiConfig(
    @LayoutRes val rateLayout: Int,
    @LayoutRes val feedbackLayout: Int,
    @LayoutRes val feedbackItemLayout: Int,
    val buttonRate: OptionButtonConfig,
    val buttonFeedback: OptionButtonConfig
) {
    data class OptionButtonConfig(
        val textSelected: Int? = null,
        val textUnselected: Int? = null,
        @DrawableRes val bgSelected: Int? = null,
        @DrawableRes val bgUnselected: Int? = null,
        @ColorRes val textColorSelected: Int? = null,
        @ColorRes val textColorUnselected: Int? = null
    )
}
