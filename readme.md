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
        RateUtils.init(application) // Hoặc this.application nếu trong Activity
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

## 4. Cấu hình Chi tiết (thông qua RateUtils)

`RateUtils.kt` cung cấp một phương thức `buildConfig()` nơi một đối tượng `RateConfig` được tạo. Bạn có thể sửa đổi phương thức này để tùy chỉnh hành vi và giao diện của hộp thoại đánh giá và phản hồi.

Dưới đây là giải thích chi tiết về các tùy chọn cấu hình có sẵn trong `RateConfig.Builder` được sử dụng trong `RateUtils`:

```kotlin
// bên trong RateUtils.kt
private fun buildConfig(): RateConfig {
    return RateConfig.Builder(
        appName = "Rate Example", // Tên ứng dụng của bạn
        packageId = "com.example.app", // ID gói của ứng dụng
        supportEmail = "support@example.com", // Email hỗ trợ
        rateOptions = getRateOptions(), // Xem getRateOptions() trong RateUtils
        feedbackReasons = getFeedbackReasons(), // Xem getFeedbackReasons() trong RateUtils
        uiConfig = UiConfig(
            rateLayout = R.layout.dialog_rate,     // Layout cho dialog đánh giá
            feedbackLayout = R.layout.dialog_feedback, // Layout cho dialog phản hồi
            feedbackItemLayout = R.layout.item_feedback, // Layout cho từng mục lý do phản hồi
            buttonRate = getButtonRateConfig(), // Cấu hình nút trong dialog đánh giá
            buttonFeedback = getButtonFeedbackConfig() // Cấu hình nút trong dialog phản hồi
        )
    )
    // --- Các điều kiện hiển thị và hành vi ---

    // Số phiên (session) tối thiểu người dùng phải mở ứng dụng trước khi hộp thoại đánh giá có thể hiển thị.
    // Mặc định: 0 (hiển thị ngay trong phiên đầu tiên nếu các điều kiện khác thỏa mãn).
    .setMinSession(0) 

    // Số phiên cách nhau giữa các lần hiển thị hộp thoại đánh giá.
    // Ví dụ: nếu đặt là 3, sau khi hiển thị lần đầu (và không bị vô hiệu hóa),
    // hộp thoại sẽ chỉ có thể hiển thị lại sau 3 phiên nữa.
    // Mặc định: 0 (có thể hiển thị trong các phiên liên tiếp nếu các điều kiện khác thỏa mãn).
    .setSessionInterval(0) // Giá trị ví dụ, bạn có thể thay đổi

    // Số lần hiển thị tối đa hộp thoại đánh giá trong một phiên.
    // Mặc định: không giới hạn (Integer.MAX_VALUE).
    .setMaxShowPerSession(1) // Giá trị ví dụ, bạn có thể thay đổi

    // Thời gian tối thiểu (tính bằng mili giây) giữa các lần hiển thị hộp thoại.
    // IntervalType.GLOBAL: Áp dụng trên toàn cục, bất kể ứng dụng được đóng mở.
    // IntervalType.PER_SESSION: Chỉ áp dụng trong cùng một phiên.
    // Mặc định: 0 (không có khoảng thời gian tối thiểu).
    .setMinIntervalMillis(10000, IntervalType.GLOBAL) // Ví dụ: 10 giây

    // Tổng số lần hiển thị tối đa hộp thoại đánh giá trong suốt vòng đời ứng dụng.
    // Mặc định: không giới hạn (Integer.MAX_VALUE).
    .setMaxTotalShow(5) // Giá trị ví dụ, bạn có thể thay đổi

    // Tự động vô hiệu hóa việc hiển thị hộp thoại đánh giá nếu người dùng đánh giá từ X sao trở lên.
    // Ví dụ: nếu đặt là 4, người dùng đánh giá 4 hoặc 5 sao sẽ không thấy hộp thoại nữa.
    // Mặc định: 0 (không vô hiệu hóa dựa trên số sao).
    .setDisableAfterStars(4)

    // Loại vô hiệu hóa khi đạt điều kiện setDisableAfterStars.
    // DisableType.SESSION: Chỉ vô hiệu hóa trong phiên hiện tại.
    // DisableType.FOREVER: Vô hiệu hóa vĩnh viễn.
    // Mặc định: DisableType.SESSION.
    .setDisableType(DisableType.SESSION) // Hoặc DisableType.FOREVER

    // Tự động mở Google In-App Review nếu người dùng đánh giá từ X sao trở lên.
    // Điều này bỏ qua hộp thoại phản hồi tùy chỉnh nếu có.
    // Mặc định: 0 (không tự động mở In-App Review).
    .setOpenInAppReviewAfterStars(4)

    // Số sao tối đa mà khi người dùng chọn, hộp thoại phản hồi sẽ được hiển thị thay vì Google In-App Review (nếu được cấu hình) hoặc hoàn thành.
    // Ví dụ: nếu đặt là 3, người dùng đánh giá 1, 2, hoặc 3 sao sẽ được chuyển đến màn hình phản hồi.
    // Mặc định: 3.
    .setMaxStarsForFeedback(3) // Giá trị ví dụ, bạn có thể thay đổi, thường là thấp hơn setOpenInAppReviewAfterStars

    // Vô hiệu hóa hoàn toàn việc sử dụng Google In-App Review, ngay cả khi các điều kiện khác được đáp ứng.
    // Mặc định: false.
    .setDisableOpenInAppReview(false)

    // Điều kiện tùy chỉnh để quyết định có hiển thị hộp thoại đánh giá hay không.
    // Hàm lambda này sẽ được gọi trước khi hiển thị. Trả về `true` để cho phép hiển thị.
    // Ví dụ: không hiển thị nếu người dùng đã từng đánh giá (RateManager.isRated).
    .setCustomShowCondition { !RateManager.isRated }

    // Điều kiện tùy chỉnh để buộc hiển thị hộp thoại, bỏ qua các điều kiện thông thường (phiên, khoảng thời gian).
    // Hữu ích cho các trường hợp đặc biệt hoặc thử nghiệm.
    // Mặc định: false.
    .setForceShowCondition { false } // Ví dụ: { BuildConfig.DEBUG } để luôn hiển thị trong bản debug

    .build()
}
```
Tham khảo thêm tệp `RateConfig.kt` và `RateManager.kt` để hiểu rõ hơn về các thuộc tính và hành vi mặc định.

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
