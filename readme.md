# Quick Start Guide: Implementing In-App Rating

This guide provides a quick way to integrate the in-app rating and feedback functionality into your Android application using `RateUtils`.

## 1. Initialization

Initialize the `RateManager` in your `Application` class's `onCreate` method. This sets up the default configuration.

```kotlin
// In your Application class
import android.app.Application
import com.tnt.ratenewdev.RateUtils // Assuming RateUtils is in this package

class YourApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        RateUtils.init(this)
    }
}
```

Make sure to declare your `Application` class in your `AndroidManifest.xml`:
```xml
<application
    android:name=".YourApplication"
    ...>
    ...
</application>
```

## 2. Showing the Rate Dialog

To show the rating dialog at an appropriate point in your app (e.g., after a user completes a key action), call `RateUtils.showRate()`.

```kotlin
// In your Activity or Fragment
import android.content.Context
import com.tnt.ratenewdev.RateUtils

// ...

// Example: Show rate dialog (conditions apply based on config)
RateUtils.showRate(this)

// To force show immediately (e.g., for testing, bypasses some conditions)
// RateUtils.showRate(this, isShowNow = true)
```
The dialog will only appear if the conditions defined in `RateConfig` (e.g., minimum sessions, interval between shows) are met, unless `isShowNow = true` is used.

The `showRate` method in `RateUtils` internally creates a `RateCallback` to handle events like `onRate` and `onFeedback`. You can customize this callback in `RateUtils` if needed.

## 3. Showing the Feedback Dialog

If you want to directly show the feedback dialog (e.g., from a "Send Feedback" menu item), call `RateUtils.showFeedback()`.

```kotlin
// In your Activity or Fragment
import android.content.Context
import com.tnt.ratenewdev.RateUtils

// ...

// Example: Show feedback dialog (conditions apply based on config)
RateUtils.showFeedback(this)

// To force show immediately (e.g., for testing)
// RateUtils.showFeedback(this, isShowNow = true)
```

## 4. Configuration (via RateUtils)

`RateUtils.kt` provides a `buildConfig()` method where a `RateConfig` object is created. You can modify this method to customize the behavior and appearance of the rating and feedback dialogs.

Key aspects you can configure in `RateConfig.Builder` (see `RateConfig.kt` for all options):

*   `appName`, `packageId`, `supportEmail`
*   `rateOptions`: List of messages and drawables for each star level.
*   `feedbackReasons`: Reasons user can select in the feedback form.
*   `uiConfig`: Layouts for dialogs (`rateLayout`, `feedbackLayout`), buttons.
*   Display conditions:
    *   `setMinSession()`: Minimum sessions before showing.
    *   `setSessionInterval()`: How many sessions between shows.
    *   `setMinIntervalMillis()`: Minimum time between shows.
    *   `setDisableAfterStars()`: Disable future prompts if user rates highly.
    *   `setOpenInAppReviewAfterStars()`: Trigger in-app review for high ratings.
    *   `setMaxStarsForFeedback()`: Show feedback form for low ratings.

Example snippet from `RateUtils.buildConfig()`:
```kotlin
private fun buildConfig(): RateConfig {
    return RateConfig.Builder(
        appName = "Rate Example", // Customize your app name
        packageId = "com.example.app", // Customize your package ID
        supportEmail = "support@example.com", // Customize your support email
        rateOptions = getRateOptions(), // See getRateOptions() in RateUtils
        feedbackReasons = getFeedbackReasons(), // See getFeedbackReasons() in RateUtils
        uiConfig = UiConfig(
            rateLayout = R.layout.dialog_rate,     // Provide your custom layout
            feedbackLayout = R.layout.dialog_feedback, // Provide your custom layout
            feedbackItemLayout = R.layout.item_feedback, // Provide your custom layout
            buttonRate = getButtonRateConfig(),
            buttonFeedback = getButtonFeedbackConfig()
        )
    )
        .setMinSession(3) // Example: Show after 3 sessions
        .setMinIntervalMillis(7 * 24 * 60 * 60 * 1000L, IntervalType.GLOBAL) // Example: 7 days global interval
        .setDisableAfterStars(4) // Example: Disable if user gives 4 or 5 stars
        .setOpenInAppReviewAfterStars(4) // Example: Open in-app review if 4 or 5 stars
        .setMaxStarsForFeedback(3) // Example: Show feedback form if 1-3 stars
        .build()
}
```
Refer to `RateConfig.kt` for detailed explanations of each builder method.

## 5. Resetting for Testing

During development and testing, you might want to reset the rating state (session counts, last shown time, etc.).

```kotlin
import com.tnt.ratenewdev.RateUtils

// ...

// Reset all rating data
RateUtils.reset()
```
This allows the dialog to be shown again as if it's the first time, according to the configuration.

---

This quick start guide should help you get the rating and feedback functionality up and running. For more advanced scenarios or deeper customization, refer to the source code of `RateManager.kt` and `RateConfig.kt`.
