# Hướng dẫn Sử dụng Thư viện Rate

Thư viện này cung cấp một giải pháp dễ dàng để hiển thị hộp thoại đánh giá (rate) và phản hồi (feedback) trong ứng dụng Android của bạn.

## 1. Hướng dẫn Triển khai Nhanh (RateUtils)

Cách nhanh nhất để tích hợp là sử dụng đối tượng `RateUtils`.

### 1.1. Khởi tạo

Trong lớp `Application` hoặc `MainActivity` của bạn, gọi phương thức `RateUtils.init()`:

```kotlin
RateUtils.init(application)
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
        appName = "Rate Example",              // Tên ứng dụng
        packageId = "com.example.app",         // ID gói
        supportEmail = "support@example.com",  // Email hỗ trợ
        rateOptions = getRateOptions(),        // Các option hiển thị theo sao
        feedbackReasons = getFeedbackReasons(),// Lý do phản hồi
        uiConfig = UiConfig(                   // Cấu hình giao diện
            rateLayout = R.layout.dialog_rate,
            feedbackLayout = R.layout.dialog_feedback,
            feedbackItemLayout = R.layout.item_feedback,
            buttonRate = getButtonRateConfig(),
            buttonFeedback = getButtonFeedbackConfig()
        )
    )
        // Điều kiện theo session
        .setMinSession(0)        // Số session tối thiểu để hiển thị
        .setSessionInterval(0)   // Khoảng cách giữa các session
        .setMaxShowPerSession(Int.MAX_VALUE) // Số lần show trong 1 session

        // Điều kiện theo thời gian
        .setMinIntervalMillis(10_000, IntervalType.GLOBAL) // cách nhau ít nhất 10s (toàn app)
        .setMaxTotalShow(Int.MAX_VALUE) // Giới hạn số lần show toàn app

        // Điều kiện theo số sao
        .setDisableAfterStars(4)        // Nếu >=4 sao thì tắt dialog
        .setDisableType(DisableType.SESSION) // Tắt trong session hiện tại
        .setOpenInAppReviewAfterStars(4) // Nếu >=4 sao thì mở In-App Review
        .setMaxStarsForFeedback(4)      // Nếu <=4 sao thì mở feedback
        .setDisableOpenInAppReview(false) // Cho phép mở In-App Review

        // Điều kiện bổ sung
        .setCustomShowCondition { !RateManager.isRated } // Không show nếu đã rate
        .setForceShowCondition { false }                 // Không ép show
        .build()
}

```

Để tùy chỉnh, bạn có thể sửa đổi trực tiếp phương thức `buildConfig()` trong `RateUtils.kt` hoặc tự mình khởi tạo `RateManager` với một `RateConfig` tùy chỉnh hoàn toàn.

## 3. Mã nguồn RateUtils.kt

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
        RateManager.init(context, true, config, com.tnt.ratenewdev.BuildConfig.DEBUG)
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
            packageId = "com.tnt.ratenewdev",
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
            .setSessionInterval(0) 
            .setMaxShowPerSession(Int.MAX_VALUE)
            .setMinIntervalMillis(10000, IntervalType.GLOBAL)
            .setMaxTotalShow(Int.MAX_VALUE) 
            .setDisableAfterStars(4) 
            .setDisableType(DisableType.SESSION)
            .setOpenInAppReviewAfterStars(4) 
            .setMaxStarsForFeedback(4) 
            .setDisableOpenInAppReview(false) 
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
