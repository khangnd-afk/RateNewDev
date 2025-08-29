package com.tnt.rate.core

import android.app.Dialog
import android.content.Context
import android.util.Log
import com.tnt.rate.model.DisableType
import com.tnt.rate.model.IntervalType
import com.tnt.rate.model.RateConfig
import com.tnt.rate.ui.RateDialog
import com.tnt.rate.ui.RateDialogFeedBack
import java.lang.ref.WeakReference

object RateManager {
    // --- Config ---
    private var _context: WeakReference<Context>? = null
    internal val context: Context get() = _context?.get()!!
    var idEnable: Boolean = false
    internal lateinit var config: RateConfig

    var supportEmail: String = ""
    var packageId: String = ""
    internal var appName: String = ""
    private var isDebuggable: Boolean = true
    private var rateLayout: Int = 0
    private var feedbackLayout: Int = 0
    private var feedbackItemLayout: Int = 0

    // --- State ---
    private var dialog: Dialog? = null
    private var dismissNextFlag = false

    val isRated get() = RatePrefs.isRated(context)

    val canShowRateDialog
        get() = runCatching {
            canShow()
        }.isSuccess

    var isInit = false

    fun init(
        context: Context,
        enable: Boolean,
        customConfig: RateConfig,
        isDebuggable: Boolean = true
    ) {
        runCatching {
            this._context = WeakReference(context)
            if (isInit || !enable) return
            this.idEnable = enable
            this.config = customConfig
            this.isDebuggable = isDebuggable
            this.appName = customConfig.appName
            this.packageId = customConfig.packageId
            this.supportEmail = customConfig.supportEmail
            this.rateLayout = customConfig.uiConfig.rateLayout
            this.feedbackLayout = customConfig.uiConfig.feedbackLayout
            this.feedbackItemLayout = customConfig.uiConfig.feedbackItemLayout

            RatePrefs.increaseSession(context)
            RatePrefs.saveDisableType(context, customConfig.disableType)
            RatePrefs.saveIntervalType(context, customConfig.intervalType)
            isInit = true
        }
    }

    fun showRate(context: Context, isShowNow: Boolean = false, callback: RateCallback) {
        if (shouldSkipShow(isShowNow)) return

        dialog = RateDialog(
            context = context,
            layoutRes = rateLayout,
            rateOptions = config.rateOptions,
            btnConfig = config.uiConfig.buttonRate,
            callback = callback
        ).apply { showDialog() }
        recordShow()
    }

    fun showFeedBack(context: Context, isShowNow: Boolean = false, callback: RateCallback) {
        if (shouldSkipShow(isShowNow)) return

        dialog = RateDialogFeedBack(
            context = context,
            layoutRes = feedbackLayout,
            layoutItemFeedBack = feedbackItemLayout,
            feedbackReasons = config.feedbackReasons,
            btnConfig = config.uiConfig.buttonFeedback,
            callback = callback
        ).apply {
            showDialog()
        }
        recordShow()
    }

    fun dismissNext() {
        log("Start Dismiss Next")
        dismissNextFlag = true
    }

    // --- Helpers ---
    private fun recordShow() {
        RatePrefs.saveShowTime(context)
    }

    private fun shouldSkipShow(isShowNow: Boolean): Boolean {
        if (!isInit) return true
        if (!isShowNow && !canShow()) return true
        if (dismissNextFlag) {
            log("Dismiss Next")
            dismissNextFlag = false
            return true
        }
        return false
    }

    fun canShowNormal(): Boolean {
        if (!idEnable) return false

        val sessionCount = RatePrefs.getSession(context)
        val lastShowTime = RatePrefs.getLastShowTime(context)
        val totalShowCount = RatePrefs.getTotalShow(context)
        val lastStars = RatePrefs.getLastStars(context)
        val showCountThisSession = RatePrefs.getShowCountThisSession(context)
        val now = System.currentTimeMillis()
        val remainder = sessionCount % (config.sessionInterval + 1)

        log("🔹 Rate Check Conditions (Normal):")
        log("   [Session Info]")
        log("      - SessionCount: $sessionCount / MinSession=${config.minSession}")
        log("      - ShowThisSession: $showCountThisSession / MaxShowPerSession=${config.maxShowPerSession}")
        log("      - SessionInterval: ${config.sessionInterval}, remainder=$remainder")
        log("   [Global Info]")
        log("      - TotalShowCount: $totalShowCount / MaxTotalShow=${config.maxTotalShow}")
        log("      - LastShowTime: $lastShowTime [${if (lastShowTime > 0) now - lastShowTime else 0}ms] / MinIntervalGlobal=${config.minIntervalMillis}ms")
        log("   [Stars / Disable]")
        log("      - LastStars: $lastStars / DisableAfterStars=${config.disableAfterStars}")
        log("      - DisableType: ${config.disableType}")
        log("      - MaxStarsForFeedback: ${config.maxStarsForFeedback}")
        log("      - DisableOpenInAppReview: ${config.disableOpenInAppReview}")

        val reasons = mutableListOf<String>()
        config.customCondition?.let { condition ->
            if (!condition()) {
                reasons.add("CustomCondition")
            }
        }

        // Check stars & disable
        if (config.disableAfterStars > 0 && lastStars >= config.disableAfterStars) {
            when (RatePrefs.getDisableType(context)) {
                DisableType.FOREVER -> reasons.add("lastStars=$lastStars >= disableAfterStars=${config.disableAfterStars} → disable vĩnh viễn")
                DisableType.SESSION -> reasons.add("lastStars=$lastStars >= disableAfterStars=${config.disableAfterStars} → disable trong phiên hiện tại")
            }
        }

        // Check minIntervalMillis
        if (config.minIntervalMillis > 0 && lastShowTime > 0) {
            val elapsed = now - lastShowTime
            if (elapsed < config.minIntervalMillis) {
                when (RatePrefs.getIntervalType(context)) {
                    IntervalType.GLOBAL -> reasons.add(
                        "GLOBAL interval not reached: elapsed=${elapsed}ms < minIntervalMillis=${config.minIntervalMillis}"
                    )

                    IntervalType.SESSION -> reasons.add(
                        "SESSION interval not reached: elapsed=${elapsed}ms < minIntervalMillis=${config.minIntervalMillis}"
                    )
                }
            }
        }

        // Check session interval
        if (config.sessionInterval > 0 && remainder != 0) {
            reasons.add("Current session=$sessionCount → sessionInterval=${config.sessionInterval}, remainder=$remainder")
        }

        // Check minSession
        if (sessionCount < config.minSession) reasons.add("sessionCount=$sessionCount < minSession=${config.minSession}")

        // Check maxTotalShow
        if (config.maxTotalShow > 0 && totalShowCount >= config.maxTotalShow) reasons.add("totalShowCount=$totalShowCount >= maxTotalShow=${config.maxTotalShow}")

        // Check maxShowPerSession
        if (config.maxShowPerSession > 0 && showCountThisSession >= config.maxShowPerSession) reasons.add(
            "showCountThisSession=$showCountThisSession >= maxShowPerSession=${config.maxShowPerSession}"
        )

        return if (reasons.isEmpty()) {
            log("____________________SHOW DIALOG RATE___________________")
            true
        } else {
            log("____________________NOT SHOW RATE___________________")
            reasons.forEachIndexed { i, o -> log("$i, ==> $o") }
            false
        }
    }

    private fun canShow(): Boolean {
        config.forceShowCondition?.let { condition ->
            if (condition()) {
                log("ForceShowCondition → SHOW")
                return true
            }
        }

        return canShowNormal()
    }

    fun reset() {
        runCatching {
            RatePrefs.clearAll(context)
            isInit = false
            init(context, idEnable, config)
        }
    }

    private fun log(msg: String) {
        if (isDebuggable) Log.e("RATE_CONFIG", msg)
    }
}
