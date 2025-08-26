package com.tnt.rate.model

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import androidx.annotation.LayoutRes

/**
 * Cấu hình cho Rate/Feedback dialog.
 *
 * Dùng để kiểm soát logic hiển thị & giao diện cho dialog đánh giá và feedback.
 *
 * @property packageId                  Package name của ứng dụng.
 * @property supportEmail               Email support khi người dùng gửi feedback.
 * @property rateOptions                Danh sách lựa chọn rate (ví dụ: 1★ = "Tệ", 5★ = "Tuyệt vời").
 * @property feedbackReasons            Danh sách lý do khi người dùng chọn gửi feedback.
 * @property layoutConfig               Cấu hình layout hiển thị cho dialog rate/feedback.
 *
 * @property minSession                 Số phiên tối thiểu trước khi được phép hiển thị dialog.
 *                                      Ví dụ: =2 → chỉ bắt đầu hiển thị từ phiên mở app thứ 2.
 *
 * @property maxShowPerSession          Số lần hiển thị tối đa trong 1 phiên (session).
 *                                      Tránh việc hiển thị quá nhiều trong cùng 1 lần mở app.
 *
 * @property minIntervalMillis          Khoảng thời gian tối thiểu (ms) giữa 2 lần hiển thị liên tiếp.
 *                                      Nếu chưa đủ thì dialog sẽ không hiển thị lại.
 *
 * @property maxTotalShow               Tổng số lần hiển thị tối đa trong toàn bộ vòng đời app.
 *                                      Nếu vượt quá thì dialog sẽ không hiện lại nữa.
 *
 * @property disableAfterStars          Nếu người dùng đánh giá >= số sao này → tắt vĩnh viễn dialog.
 *                                      Ví dụ: =4 → nếu user rate 4★ hoặc 5★ thì không show lại.
 *
 * @property openInAppReviewAfterStars  Nếu người dùng rate >= số sao này → mở Google Play In-App Review.
 *                                      Ví dụ: =5 → chỉ khi user chọn 5★ mới trigger review flow.
 *
 * @property buttonRate                 Cấu hình giao diện cho button "Rate" (tuỳ chọn).
 * @property buttonFeedback             Cấu hình giao diện cho button "Feedback" (tuỳ chọn).
 */
@Keep
data class RateConfig(
    val packageId: String,
    val supportEmail: String,
    val rateOptions: List<RateOption>,
    val feedbackReasons: List<FeedbackReason>,
    val layoutConfig: LayoutConfig,
    val minSession: Int = 0,
    val maxShowPerSession: Int = 0,
    val minIntervalMillis: Long = 0,
    val maxTotalShow: Int = 0,
    val disableAfterStars: Int = 0,
    val disableType: DisableType = DisableType.SESSION,
    val openInAppReviewAfterStars: Int = 0,
    val buttonRate: OptionButtonConfig? = null,
    val buttonFeedback: OptionButtonConfig? = null
) {
    @Keep
    enum class DisableType {
        SESSION,
        FOREVER
    }
    /**
     * Cấu hình layout XML cho dialog & item.
     *
     * @property rateLayout         Layout của dialog rate (chọn sao).
     * @property feedbackLayout     Layout của dialog feedback (chọn lý do).
     * @property feedbackItemLayout Layout của từng item feedback.
     */
    @Keep
    data class LayoutConfig(
        @LayoutRes val rateLayout: Int,
        @LayoutRes val feedbackLayout: Int,
        @LayoutRes val feedbackItemLayout: Int
    )

    /**
     * Builder pattern để khởi tạo [RateConfig] linh hoạt hơn.
     */
    class Builder(
        private val packageId: String,
        private val supportEmail: String,
        private val rateOptions: List<RateOption>,
        private val feedbackReasons: List<FeedbackReason>,
        private val layoutConfig: LayoutConfig
    ) {
        private var minSession: Int = 1
        private var maxShowPerSession: Int = 1
        private var minIntervalMillis: Long = 0
        private var maxTotalShow: Int = Int.MAX_VALUE
        private var disableAfterStars: Int = 4
        private var disableType: DisableType = DisableType.FOREVER
        private var openInAppReviewAfterStars: Int = 5
        private var buttonRate: OptionButtonConfig? = null
        private var buttonFeedback: OptionButtonConfig? = null

        /** Số phiên tối thiểu trước khi cho phép show dialog */
        fun setMinSession(value: Int) = apply { minSession = value }

        /** Giới hạn số lần show trong 1 phiên */
        fun setMaxShowPerSession(value: Int) = apply { maxShowPerSession = value }

        /** Thời gian tối thiểu giữa 2 lần show (ms) */
        fun setMinIntervalMillis(value: Long) = apply { minIntervalMillis = value }

        /** Tổng số lần show tối đa */
        fun setMaxTotalShow(value: Int) = apply { maxTotalShow = value }

        /** Nếu rate >= value thì disable vĩnh viễn */
        fun setDisableAfterStars(value: Int) = apply { disableAfterStars = value }

        /** Nếu rate >= value thì mở Google In-App Review */
        fun setOpenInAppReviewAfterStars(value: Int) = apply { openInAppReviewAfterStars = value }

        /** Cấu hình button Rate */
        fun setButtonRate(config: OptionButtonConfig) = apply { buttonRate = config }

        /** Cấu hình button Feedback */
        fun setButtonFeedback(config: OptionButtonConfig) = apply { buttonFeedback = config }

        /** Kiểu disable (theo phiên / vĩnh viễn) */
        fun setDisableType(value: DisableType) = apply { disableType = value }

        /** Xây dựng [RateConfig] */
        fun build() = RateConfig(
            packageId,
            supportEmail,
            rateOptions,
            feedbackReasons,
            layoutConfig,
            minSession,
            maxShowPerSession,
            minIntervalMillis,
            maxTotalShow,
            disableAfterStars,
            disableType,
            openInAppReviewAfterStars,
            buttonRate,
            buttonFeedback
        )
    }
}

/**
 * Cấu hình giao diện cho các button (Rate/Feedback).
 *
 * @property textSelected         String resource cho text khi được chọn.
 * @property textUnselected       String resource cho text khi chưa chọn.
 * @property backgroundSelected   Drawable resource cho background khi chọn.
 * @property backgroundUnselected Drawable resource cho background khi chưa chọn.
 * @property textColorSelected    Color resource cho text khi chọn.
 * @property textColorUnselected  Color resource cho text khi chưa chọn.
 */
@Keep
data class OptionButtonConfig(
    val textSelected: Int? = null,
    val textUnselected: Int? = null,
    @DrawableRes val backgroundSelected: Int? = null,
    @DrawableRes val backgroundUnselected: Int? = null,
    @ColorRes val textColorSelected: Int? = null,
    @ColorRes val textColorUnselected: Int? = null
)
