# Hướng dẫn sử dụng Rate Library

Thư viện Rate cung cấp giải pháp dễ dàng để hiển thị hộp thoại đánh giá (rating) và phản hồi (feedback) trong ứng dụng Android, giúp cải thiện trải nghiệm người dùng và thu thập ý kiến.

## 1. Hướng dẫn Triển khai Nhanh (RateUtils)

Cách nhanh nhất để tích hợp thư viện là sử dụng đối tượng `RateUtils`.

### 1.1. Khởi tạo

Trong lớp `Application` hoặc `MainActivity`, gọi phương thức `RateUtils.init()`:

```kotlin
RateUtils.init(application)
```

**Lưu ý**: `RateUtils.init()` sẽ tự động gọi `RateManager.init()` với cấu hình mặc định được định nghĩa trong `RateUtils.buildConfig()`.

### 1.2. Hiển thị Hộp thoại Đánh giá

Để hiển thị hộp thoại đánh giá:

```kotlin
// context: Context của Activity hoặc Fragment
// isShowNow: true để hiển thị ngay lập tức, bỏ qua các điều kiện (số session, khoảng thời gian)
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

### 1.4. Reset Dữ liệu (Dành cho thử nghiệm)

Để xóa toàn bộ dữ liệu đánh giá đã lưu (hữu ích khi kiểm tra):

```kotlin
RateUtils.reset()
```

## 2. Cấu hình Chi tiết (Thông qua RateUtils và RateConfig.Builder)

Bạn có thể tùy chỉnh hành vi của thư viện bằng phương thức `RateUtils.buildConfig()`, sử dụng `RateConfig.Builder` để tạo đối tượng `RateConfig`.

Dưới đây là ví dụ cấu hình `RateConfig` và giải thích các tùy chọn:

```kotlin
private fun buildConfig(): RateConfig {
    return RateConfig.Builder(
        appName = "Rate Example",              // Tên ứng dụng
        packageId = "com.example.app",         // ID gói
        supportEmail = "support@example.com",  // Email hỗ trợ
        rateOptions = getRateOptions(),        // Các tùy chọn hiển thị theo sao
        feedbackReasons = getFeedbackReasons(),// Lý do phản hồi
        uiConfig = UiConfig(                   // Cấu hình giao diện
            rateLayout = R.layout.layout_dialog_rate,
            feedbackLayout = R.layout.layout_dialog_feedback,
            feedbackItemLayout = R.layout.item_feedback,
            buttonRate = getButtonRateConfig(),
            buttonFeedback = getButtonFeedbackConfig()
        )
    )
        // Điều kiện theo session
        .setMinSession(0)        // Số session tối thiểu để hiển thị
        .setSessionInterval(0)   // Khoảng cách giữa các session
        .setMaxShowPerSession(Int.MAX_VALUE) // Số lần hiển thị tối đa trong 1 session

        // Điều kiện theo thời gian
        .setMinIntervalMillis(10_000, IntervalType.GLOBAL) // Cách nhau ít nhất 10 giây (toàn app)
        .setMaxTotalShow(Int.MAX_VALUE) // Giới hạn số lần hiển thị toàn app

        // Điều kiện theo số sao
        .setDisableAfterStars(4)        // Tắt dialog nếu >= 4 sao
        .setDisableType(DisableType.SESSION) // Tắt trong session hiện tại
        .setOpenInAppReviewAfterStars(4) // Mở In Principled before content. Mở In-App Review nếu >= 4 sao
        .setMaxStarsForFeedback(4)      // Mở feedback nếu <= 4 sao
        .setDisableOpenInAppReview(false) // Cho phép mở In-App Review

        // Điều kiện bổ sung
        .setCustomShowCondition { !RateManager.isRated } // Không hiển thị nếu đã đánh giá
        .setForceShowCondition { false }                 // Không ép hiển thị
        .build()
}
```

Để tùy chỉnh, bạn có thể sửa đổi `buildConfig()` trong `RateUtils.kt` hoặc tự khởi tạo `RateManager` với `RateConfig` tùy chỉnh.

## 3. Mã nguồn RateUtils.kt

Dưới đây là toàn bộ nội dung của `RateUtils.kt`:

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
        RateManager.init(context, Firebase.remoteConfig.getBoolean("key_rate_enable"), config, com.tnt.ratenewdev.BuildConfig.DEBUG)
    }

    fun showRate(context: Context, isShowNow: Boolean = false) {
        runCatching {
            RateManager.showRate(
                context, isShowNow, createCallback(
                onRate = { star, isSubmit ->
                    Log.d("RATE_CONFIG", "onRate: star=$star, isSubmit=$isSubmit")
                },
                onFeedback = { message, text, isSubmit ->
                    Log.d("RATE_CONFIG", "onFeedBack: message=$message, text=$text, isSubmit=$isSubmit")
                }
            ))
        }
    }

    fun showFeedback(context: Context, isShowNow: Boolean = false) {
        runCatching {
            RateManager.showFeedBack(
                context, isShowNow, createCallback(
                    onFeedback = { message, text, isSubmit ->
                        Log.d("RATE_CONFIG", "onFeedBack: message=$message, text=$text, isSubmit=$isSubmit")
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
                rateLayout = R.layout.layout_dialog_rate, 
                feedbackLayout = R.layout.layout_dialog_feedback,
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

**Lưu ý**:
- Khi gọi `RateManager.init(context, true, config, BuildConfig.DEBUG)`, sử dụng `BuildConfig` của module app (`com.tnt.ratenewdev.BuildConfig.DEBUG`).
- Trong `getRateOptions()`, hàm `RateUtils.getRateOptions(...)` thực chất gọi `com.tnt.rate.core.RateUtils.getRateOptions(...)` từ thư viện rate. Chỉ định đầy đủ gói nếu cần.

## 4. Mặc định XML bố cục

Dưới đây là mã nguồn XML mặc định của các tệp bố cục được sử dụng nếu không cung cấp `UiConfig` tùy chỉnh.

### 4.1. item_feedback.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:padding="12dp">

    <ImageView
        android:id="@+id/ivBgSelect"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

    <ImageView
        android:id="@+id/ivSelect"
        android:layout_width="24dp"
        android:layout_height="24dp" />

    <TextView
        android:id="@+id/tvReason"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginHorizontal="12dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toEndOf="@+id/ivSelect"
        app:layout_constraintTop_toTopOf="parent" />
</androidx.constraintlayout.widget.ConstraintLayout>
```

### 4.2. layout_dialog_feedback.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.cardview.widget.CardView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:cardBackgroundColor="#FFFFFF"
    app:cardCornerRadius="16dp">

    <androidx.constraintlayout.widget.ConstraintLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:padding="16dp">

        <TextView
            android:id="@+id/tvRate"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:includeFontPadding="false"
            android:paddingStart="24dp"
            android:paddingLeft="24dp"
            android:text="Your feedback is helpful"
            android:textColor="#000000"
            android:textSize="18sp"
            android:textStyle="bold"
            app:layout_constraintBottom_toBottomOf="@+id/ivClose"
            app:layout_constraintEnd_toStartOf="@+id/ivClose"
            app:layout_constraintStart_toStartOf="@+id/rcvReason"
            app:layout_constraintTop_toTopOf="@+id/ivClose"
            tools:ignore="RtlSymmetry" />

        <ImageView
            android:id="@+id/ivClose"
            android:layout_width="48dp"
            android:layout_height="48dp"
            android:padding="8dp"
            android:src="@drawable/ic_close_rate"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:tint="#666666" />

        <androidx.recyclerview.widget.RecyclerView
            android:id="@+id/rcvReason"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:maxHeight="350dp"
            android:orientation="vertical"
            app:layoutManager="androidx.recyclerview.widget.LinearLayoutManager"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toBottomOf="@+id/ivClose"
            tools:listitem="@layout/rate_item_reason_feedback" />

        <EditText
            android:id="@+id/edtFeedback"
            android:layout_width="match_parent"
            android:layout_height="118dp"
            android:layout_marginTop="16dp"
            android:background="@drawable/custom_edittext_feedback"
            android:gravity="top"
            android:hint="Type your text here"
            android:padding="10dp"
            android:textColor="#000000"
            android:textColorHint="#8044565B"
            android:visibility="gone"
            app:layout_constraintEnd_toEndOf="@+id/rcvReason"
            app:layout_constraintStart_toStartOf="@+id/rcvReason"
            app:layout_constraintTop_toBottomOf="@+id/rcvReason" />

        <TextView
            android:id="@+id/btnFeedback"
            android:layout_width="match_parent"
            android:layout_height="64dp"
            android:layout_marginHorizontal="16dp"
            android:layout_marginTop="16dp"
            android:background="@drawable/btn_submit_df"
            android:ellipsize="end"
            android:gravity="center"
            android:maxLines="1"
            android:text="OK"
            android:textColor="#FFFFFF"
            android:textSize="16sp"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toBottomOf="@+id/edtFeedback"
            app:layout_constraintWidth_percent="0.85" />
    </androidx.constraintlayout.widget.ConstraintLayout>
</androidx.cardview.widget.CardView>
```

### 4.3. layout_dialog_rate.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.cardview.widget.CardView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:cardCornerRadius="16sp">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:gravity="center"
        android:orientation="vertical"
        android:padding="16sp">

        <androidx.constraintlayout.widget.ConstraintLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content">

            <TextView
                android:id="@+id/tvTitle"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginStart="48dp"
                android:textColor="#000000"
                android:textSize="16dp"
                android:textStyle="bold"
                app:layout_constraintBottom_toBottomOf="@+id/ivClose"
                app:layout_constraintEnd_toStartOf="@+id/ivClose"
                app:layout_constraintStart_toStartOf="parent"
                app:layout_constraintTop_toTopOf="@+id/ivClose"
                tools:text="Title" />

            <ImageView
                android:id="@+id/ivClose"
                android:layout_width="48dp"
                android:layout_height="48dp"
                android:padding="8dp"
                android:src="@drawable/ic_close_rate"
                app:layout_constraintEnd_toEndOf="parent"
                app:layout_constraintTop_toTopOf="parent"
                app:tint="#666666" />
        </androidx.constraintlayout.widget.ConstraintLayout>

        <TextView
            android:id="@+id/tvDescription"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="16dp"
            android:gravity="center"
            android:textColor="#000000"
            android:textSize="14dp"
            tools:text="How do you feel about the app? Your feedback is important to us" />

        <ImageView
            android:id="@+id/ivPreview"
            android:layout_width="159dp"
            android:layout_height="140dp"
            android:layout_marginTop="16dp"
            tools:src="@drawable/iv_preview_rate" />

        <TextView
            android:id="@+id/ctaRate"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content" />

        <LinearLayout
            android:layout_width="wrap_content"
            android:layout_height="wrap_content">

            <ImageView
                android:id="@+id/ivStar1"
                android:layout_width="32dp"
                android:layout_height="32dp"
                tools:src="@drawable/ic_star" />

            <ImageView
                android:id="@+id/ivStar2"
                android:layout_width="32dp"
                android:layout_height="32dp"
                android:layout_marginHorizontal="16dp"
                tools:src="@drawable/ic_star" />

            <ImageView
                android:id="@+id/ivStar3"
                android:layout_width="32dp"
                android:layout_height="32dp"
                tools:src="@drawable/ic_star" />

            <ImageView
                android:id="@+id/ivStar4"
                android:layout_width="32dp"
                android:layout_height="32dp"
                android:layout_marginHorizontal="16dp"
                tools:src="@drawable/ic_star" />

            <ImageView
                android:id="@+id/ivStar5"
                android:layout_width="32dp"
                android:layout_height="32dp"
                tools:src="@drawable/ic_star" />
        </LinearLayout>

        <TextView
            android:id="@+id/tvBottom"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="12dp"
            android:drawableEnd="@drawable/ic_rate_plus"
            android:drawablePadding="5dp"
            android:gravity="center"
            android:text="The best we can get"
            android:textColor="#000000" />

        <TextView
            android:id="@+id/btnRate"
            android:layout_width="match_parent"
            android:layout_height="64dp"
            android:layout_marginHorizontal="16dp"
            android:layout_marginTop="10dp"
            android:background="@drawable/btn_submit_df"
            android:ellipsize="end"
            android:gravity="center"
            android:maxLines="1"
            android:text="OK"
            android:textColor="#FFFFFF"
            android:textSize="16dp"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toBottomOf="@+id/textView"
            app:layout_constraintWidth_percent="0.85" />
    </LinearLayout>
</androidx.cardview.widget.CardView>
```

**Lưu ý**: Vui lòng kiểm tra lại đường dẫn và nội dung tài nguyên (drawable, string, layout) để đảm bảo tính chính xác khi tích hợp.
