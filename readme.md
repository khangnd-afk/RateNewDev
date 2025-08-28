# Hướng dẫn Nhanh: Triển khai Đánh giá Trong Ứng dụng

Hướng dẫn này cung cấp một cách nhanh chóng để tích hợp chức năng đánh giá và phản hồi trong ứng dụng vào ứng dụng Android của bạn bằng cách sử dụng `RateUtils`.

## 1. Khởi tạo

Khởi tạo `RateManager` trong phương thức `onCreate` của lớp `Application` hoặc `MainActivity` của bạn. Thao tác này sẽ thiết lập cấu hình mặc định.

```kotlin
// Trong lớp Application  
import android.app.Application
import com.tnt.ratenewdev.RateUtils // Giả sử RateUtils nằm trong gói này

class YourApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        RateUtils.init(this)
    }
}
```
 ```kotlin
// Trong lớp MainActivity  
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        RateUtils.init(application)
    }
}
```

## 2. Hiển thị Hộp thoại Đánh giá

Để hiển thị hộp thoại đánh giá tại một thời điểm thích hợp trong ứng dụng của bạn (ví dụ: sau khi người dùng hoàn thành một hành động quan trọng), hãy gọi `RateUtils.showRate()`.

```kotlin
// Trong Activity hoặc Fragment của bạn
import android.content.Context
import com.tnt.ratenewdev.RateUtils

// ...

// Ví dụ: Hiển thị hộp thoại đánh giá (điều kiện áp dụng dựa trên cấu hình)
RateUtils.showRate(this)

// Để buộc hiển thị ngay lập tức (ví dụ: để thử nghiệm, bỏ qua một số điều kiện)
// RateUtils.showRate(this, isShowNow = true)
```
Hộp thoại sẽ chỉ xuất hiện nếu các điều kiện được xác định trong `RateConfig` (ví dụ: số phiên tối thiểu, khoảng thời gian giữa các lần hiển thị) được đáp ứng, trừ khi `isShowNow = true` được sử dụng.

Phương thức `showRate` trong `RateUtils` sẽ tự động tạo một `RateCallback` để xử lý các sự kiện như `onRate` và `onFeedback`. Bạn có thể tùy chỉnh callback này trong `RateUtils` nếu cần.

## 3. Hiển thị Hộp thoại Phản hồi

Nếu bạn muốn trực tiếp hiển thị hộp thoại phản hồi (ví dụ: từ một mục menu "Gửi phản hồi"), hãy gọi `RateUtils.showFeedback()`.

```kotlin
// Trong Activity hoặc Fragment của bạn
import android.content.Context
import com.tnt.ratenewdev.RateUtils

// ...

// Ví dụ: Hiển thị hộp thoại phản hồi (điều kiện áp dụng dựa trên cấu hình)
RateUtils.showFeedback(this)

// Để buộc hiển thị ngay lập tức (ví dụ: để thử nghiệm)
// RateUtils.showFeedback(this, isShowNow = true)
```

## 4. Cấu hình (thông qua RateUtils)

`RateUtils.kt` cung cấp một phương thức `buildConfig()` nơi một đối tượng `RateConfig` được tạo. Bạn có thể sửa đổi phương thức này để tùy chỉnh hành vi và giao diện của hộp thoại đánh giá và phản hồi.

Các khía cạnh chính bạn có thể cấu hình trong `RateConfig.Builder` (xem `RateConfig.kt` để biết tất cả các tùy chọn):

*   `appName`, `packageId`, `supportEmail`
*   `rateOptions`: Danh sách các thông báo và hình ảnh cho mỗi mức sao.
*   `feedbackReasons`: Các lý do người dùng có thể chọn trong biểu mẫu phản hồi.
*   `uiConfig`: Bố cục cho các hộp thoại (`rateLayout`, `feedbackLayout`), các nút.
*   Điều kiện hiển thị:
    *   `setMinSession()`: Số phiên tối thiểu trước khi hiển thị.
    *   `setSessionInterval()`: Số phiên giữa các lần hiển thị.
    *   `setMinIntervalMillis()`: Thời gian tối thiểu giữa các lần hiển thị.
    *   `setDisableAfterStars()`: Tắt các lời nhắc trong tương lai nếu người dùng đánh giá cao.
    *   `setOpenInAppReviewAfterStars()`: Kích hoạt đánh giá trong ứng dụng cho các đánh giá cao.
    *   `setMaxStarsForFeedback()`: Hiển thị biểu mẫu phản hồi cho các đánh giá thấp.

Đoạn mã ví dụ từ `RateUtils.buildConfig()`:
```kotlin
private fun buildConfig(): RateConfig {
    return RateConfig.Builder(
        appName = "Rate Example", // Tùy chỉnh tên ứng dụng của bạn
        packageId = "com.example.app", // Tùy chỉnh ID gói của bạn
        supportEmail = "support@example.com", // Tùy chỉnh email hỗ trợ của bạn
        rateOptions = getRateOptions(), // Xem getRateOptions() trong RateUtils
        feedbackReasons = getFeedbackReasons(), // Xem getFeedbackReasons() trong RateUtils
        uiConfig = UiConfig(
            rateLayout = R.layout.dialog_rate,     // Cung cấp bố cục tùy chỉnh của bạn
            feedbackLayout = R.layout.dialog_feedback, // Cung cấp bố cục tùy chỉnh của bạn
            feedbackItemLayout = R.layout.item_feedback, // Cung cấp bố cục tùy chỉnh của bạn
            buttonRate = getButtonRateConfig(),
            buttonFeedback = getButtonFeedbackConfig()
        )
    )
        .setMinSession(3) // Ví dụ: Hiển thị sau 3 phiên
        .setMinIntervalMillis(7 * 24 * 60 * 60 * 1000L, IntervalType.GLOBAL) // Ví dụ: khoảng thời gian chung 7 ngày
        .setDisableAfterStars(4) // Ví dụ: Tắt nếu người dùng cho 4 hoặc 5 sao
        .setOpenInAppReviewAfterStars(4) // Ví dụ: Mở đánh giá trong ứng dụng nếu 4 hoặc 5 sao
        .setMaxStarsForFeedback(3) // Ví dụ: Hiển thị biểu mẫu phản hồi nếu 1-3 sao
        .build()
}
```
Tham khảo `RateConfig.kt` để biết giải thích chi tiết về từng phương thức builder.

## 5. Đặt lại để Thử nghiệm

Trong quá trình phát triển và thử nghiệm, bạn có thể muốn đặt lại trạng thái đánh giá (số lượng phiên, thời gian hiển thị lần cuối, v.v.).

```kotlin
import com.tnt.ratenewdev.RateUtils

// ...

// Đặt lại tất cả dữ liệu đánh giá
RateUtils.reset()
```
Điều này cho phép hộp thoại được hiển thị lại như thể đây là lần đầu tiên, theo cấu hình.

---

Hướng dẫn nhanh này sẽ giúp bạn thiết lập và chạy chức năng đánh giá và phản hồi. Đối với các tình huống nâng cao hơn hoặc tùy chỉnh sâu hơn, hãy tham khảo mã nguồn của `RateManager.kt` và `RateConfig.kt`.
