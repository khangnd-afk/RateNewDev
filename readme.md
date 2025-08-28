# Hướng dẫn Sử dụng Thư viện Rate

Thư viện này cung cấp một giải pháp dễ dàng để hiển thị hộp thoại đánh giá (rate) và phản hồi (feedback) trong ứng dụng Android của bạn.

## 1. Hướng dẫn Triển khai Nhanh (RateUtils)

Cách nhanh nhất để tích hợp là sử dụng đối tượng `RateUtils`.

### 1.1. Khởi tạo

Trong lớp `Application` của bạn, gọi phương thức `RateUtils.init()`:

```kotlin
import android.app.Application
import com.tnt.ratenewdev.BuildConfig // Hoặc import BuildConfig của module app của bạn
import com.tnt.ratenewdev.RateUtils // Đảm bảo đúng package

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        RateUtils.init(this)
    }
}
```
Lưu ý: `RateUtils.init()` sẽ tự động gọi `RateManager.init()` với một cấu hình mặc định (được định nghĩa trong `RateUtils.buildConfig()`).

### 1.2. Hiển thị Hộp thoại Đánh giá

Để hiển thị hộp thoại đánh giá:

```kotlin
// context: Context của Activity hoặc Fragment
// isShowNow: true để hiển thị ngay lập tức, bỏ qua các điều kiện (ví dụ: số session, khoảng thời gian)
//            false (mặc định) để tuân theo các điều kiện đã cấu hình
RateUtils.showRate(context, isShowNow = false)
```

### 1.3. Hiển thị Hộp thoại Phản hồi

Để hiển thị hộp thoại phản hồi trực tiếp:

```kotlin
// context: Context của Activity hoặc Fragment
// isShowNow: true để hiển thị ngay lập tức, bỏ qua các điều kiện
//            false (mặc định) để tuân theo các điều kiện đã cấu hình
RateUtils.showFeedback(context, isShowNow = false)
```

### 1.4. Reset Dữ liệu (Dành cho Kiểm thử)

Để xóa toàn bộ dữ liệu đánh giá đã lưu (hữu ích khi kiểm thử):

```kotlin
RateUtils.reset()
```

## 2. Cấu hình Chi tiết (Thông qua RateUtils và RateConfig.Builder)

Bạn có thể tùy chỉnh sâu hơn các hành vi của thư viện thông qua phương thức `RateUtils.buildConfig()`. Phương thức này sử dụng `RateConfig.Builder` để tạo đối tượng `RateConfig`.

Dưới đây là ví dụ về cách `RateUtils` cấu hình `RateConfig` và giải thích các tùy chọn:

```kotlin
private fun buildConfig(): RateConfig {
    return RateConfig.Builder(
        appName = "Rate Example", // Tên ứng dụng của bạn
        packageId = "com.example.app", // Package ID của ứng dụng
        supportEmail = "support@example.com", // Email hỗ trợ
        rateOptions = getRateOptions(), // Danh sách tùy chọn hiển thị cho từng mức sao
        feedbackReasons = getFeedbackReasons(), // Danh sách lý do phản hồi
        uiConfig = UiConfig( // Cấu hình giao diện
            rateLayout = R.layout.dialog_rate,
            feedbackLayout = R.layout.dialog_feedback,
            feedbackItemLayout = R.layout.item_feedback,
            buttonRate = getButtonRateConfig(),
            buttonFeedback = getButtonFeedbackConfig()
        )
    )
        // Số session tối thiểu trước khi dialog được phép hiển thị.
        // Mặc định: 0 (hiển thị từ session đầu tiên nếu các điều kiện khác thỏa mãn)
        .setMinSession(0)

        // Khoảng cách giữa các lần session hiển thị dialog.
        // Ví dụ: 2 → cứ cách 2 session thì được phép hiển thị lại (nếu các điều kiện khác thỏa mãn).
        // Mặc định: 0 (không có khoảng cách session, có thể hiển thị mỗi session nếu các điều kiện khác thỏa mãn)
        .setSessionInterval(0) // Giá trị mặc định trong RateConfig.Builder là 0

        // Giới hạn số lần show trong 1 session.
        // Mặc định: Int.MAX_VALUE (không giới hạn trong một session)
        .setMaxShowPerSession(Int.MAX_VALUE) // Giá trị mặc định trong RateConfig.Builder là Int.MAX_VALUE

        // Cấu hình khoảng thời gian tối thiểu (ms) giữa 2 lần hiển thị dialog.
        // @param value: thời gian tối thiểu tính bằng millisecond.
        // @param type: kiểu khoảng thời gian cần áp dụng:
        //  - IntervalType.SESSION → giới hạn trong phạm vi session hiện tại.
        //  - IntervalType.GLOBAL → giới hạn trên toàn bộ vòng đời ứng dụng (dữ liệu được lưu lại).
        // Mặc định: 0, IntervalType.SESSION
        .setMinIntervalMillis(10000, IntervalType.GLOBAL)

        // Tổng số lần hiển thị tối đa trong suốt vòng đời app.
        // Mặc định: Int.MAX_VALUE (không giới hạn tổng số lần hiển thị)
        .setMaxTotalShow(Int.MAX_VALUE) // Giá trị mặc định trong RateConfig.Builder là Int.MAX_VALUE

        // Nếu user chọn số sao >= giá trị này → disable dialog trong tương lai.
        // Mặc định: 4
        .setDisableAfterStars(4)

        // Kiểu disable khi đạt disableAfterStars.
        // - DisableType.SESSION → chỉ trong session hiện tại.
        // - DisableType.FOREVER → vĩnh viễn (lưu trạng thái).
        // Mặc định: DisableType.SESSION
        .setDisableType(DisableType.SESSION)

        // Nếu số sao >= giá trị này → mở In-App Review (nếu được kích hoạt).
        // Mặc định: 4
        .setOpenInAppReviewAfterStars(4)

        // Nếu số sao <= giá trị này → mở dialog feedback.
        // Mặc định: 0 (tức là nếu 0 sao thì mới mở feedback, nếu muốn 1,2,3 sao mở feedback thì đặt là 3)
        .setMaxStarsForFeedback(4) // Trong RateUtils.kt đang là 4

        // Nếu true → KHÔNG mở In-App Review mà chuyển thẳng sang Store (nếu có hành động chuyển Store).
        // Hoặc không làm gì nếu không có hành động chuyển Store sau In-App Review.
        // Mặc định: false (cho phép mở In-App Review)
        .setDisableOpenInAppReview(false)

        // Điều kiện custom bổ sung.
        // - return true → tiếp tục check các điều kiện khác.
        // - return false → dialog không hiển thị.
        // Mặc định: null (không có điều kiện custom)
        .setCustomShowCondition { !RateManager.isRated } // Ví dụ: không hiển thị nếu đã rate

        // Điều kiện ép show (bỏ qua toàn bộ điều kiện khác, ngoại trừ RateManager.isInit).
        // - return true → show ngay lập tức.
        // Mặc định: null (không có điều kiện ép show)
        .setForceShowCondition { false } // Ví dụ: không bao giờ ép show
        .build()
}
```

Để tùy chỉnh, bạn có thể sửa đổi trực tiếp phương thức `buildConfig()` trong `RateUtils.kt` hoặc tự mình khởi tạo `RateManager` với một `RateConfig` tùy chỉnh hoàn toàn.

## 3. Mã nguồn RateConfig.kt

Dưới đây là toàn bộ nội dung của tệp `RateConfig.kt` để bạn tham khảo cấu trúc và các tùy chọn có sẵn.

```kotlin
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
```

## 4. Mã nguồn RateUtils.kt

Dưới đây là toàn bộ nội dung của đối tượng `RateUtils.kt` được sử dụng trong ví dụ trên.

```kotlin
package com.tnt.ratenewdev

import android.app.Application
import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.View
import com.tnt.rate.core.RateCallback
import com.tnt.rate.core.RateManager
import com.tnt.rate.core.RateUtils // Đây là com.tnt.rate.core.RateUtils, không phải class này
import com.tnt.rate.model.DisableType
import com.tnt.rate.model.FeedbackReason
import com.tnt.rate.model.IntervalType
import com.tnt.rate.model.RateConfig
import com.tnt.rate.model.RateOption
import com.tnt.rate.model.UiConfig

// Đổi tên object này để tránh trùng lặp nếu cần, ví dụ: AppRateHelper
object RateUtils { // Đây là object trong module app của bạn

    fun init(context: Application) {
        if (RateManager.isInit) return

        val config = buildConfig()
        // Sử dụng BuildConfig từ module app của bạn
        RateManager.init(context, true, config, com.tnt.ratenewdev.BuildConfig.DEBUG)
    }

    fun showRate(context: Context, isShowNow: Boolean = false) {
        runCatching {
            RateManager.showRate(
                context, isShowNow, createCallback(
                onRate = { star, isSubmit ->
                    // Xử lý khi người dùng rate
                },
                onFeedback = { message, text, isSubmit ->
                    // Xử lý khi người dùng gửi feedback (qua dialog rate)
                }
            ))
        }
    }

    fun showFeedback(context: Context, isShowNow: Boolean = false) {
        runCatching {
            RateManager.showFeedBack(
                context, isShowNow, createCallback(
                    onFeedback = { message, text, isSubmit ->
                        // Xử lý khi người dùng gửi feedback (qua dialog feedback trực tiếp)
                    }
                ))
        }
    }

    fun reset() = RateManager.reset()

    private fun buildConfig(): RateConfig {
        return RateConfig.Builder(
            appName = "Rate Example", // Thay bằng tên ứng dụng của bạn
            packageId = "com.tnt.ratenewdev", // Thay bằng package ID ứng dụng của bạn
            supportEmail = "support@example.com", // Thay bằng email hỗ trợ
            rateOptions = getRateOptions(),
            feedbackReasons = getFeedbackReasons(),
            uiConfig = UiConfig(
                rateLayout = R.layout.dialog_rate, // Layout dialog rate của bạn
                feedbackLayout = R.layout.dialog_feedback, // Layout dialog feedback của bạn
                feedbackItemLayout = R.layout.item_feedback, // Layout item lý do feedback
                buttonRate = getButtonRateConfig(),
                buttonFeedback = getButtonFeedbackConfig()
            )
        )
            .setMinSession(0)
            .setSessionInterval(0) // Mặc định trong RateConfig.Builder
            .setMaxShowPerSession(Int.MAX_VALUE) // Mặc định trong RateConfig.Builder
            .setMinIntervalMillis(10000, IntervalType.GLOBAL)
            .setMaxTotalShow(Int.MAX_VALUE) // Mặc định trong RateConfig.Builder
            .setDisableAfterStars(4) // Mặc định trong RateConfig.Builder
            .setDisableType(DisableType.SESSION) // Mặc định trong RateConfig.Builder
            .setOpenInAppReviewAfterStars(4) // Mặc định trong RateConfig.Builder
            .setMaxStarsForFeedback(4) // Cấu hình trong RateUtils là 4, mặc định builder là 0
            .setDisableOpenInAppReview(false) // Mặc định trong RateConfig.Builder
            .setCustomShowCondition { !RateManager.isRated }
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
                onRate?.invoke(star, isSubmit)
            }

            override fun onFeedBack(count: Int, message: String, text: String, isSubmit: Boolean) {
                Log.d("RATE_CONFIG", "onFeedBack: message=$message, text=$text, isSubmit=$isSubmit")
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

    // Sử dụng com.tnt.rate.core.RateUtils từ thư viện rate
    private fun getRateOptions(): List<RateOption> {
        return com.tnt.rate.core.RateUtils.getRateOptions(
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

```

Lưu ý quan trọng trong `RateUtils.kt` của module app:
-  Khi gọi `RateManager.init(context, true, config, BuildConfig.DEBUG)`, `BuildConfig.DEBUG` nên là `BuildConfig` của module app (`com.tnt.ratenewdev.BuildConfig.DEBUG`).
-  Trong `getRateOptions()`, việc gọi `RateUtils.getRateOptions(...)` thực chất là đang gọi `com.tnt.rate.core.RateUtils.getRateOptions(...)` từ thư viện `rate`. Nếu có sự nhầm lẫn về tên, bạn có thể cần chỉ định rõ package đầy đủ như ví dụ.

---
Vui lòng kiểm tra lại đường dẫn và nội dung.
