package com.tnt.rate.core

import android.app.Dialog
import android.content.Context
import com.tnt.rate.model.RateConfig
import com.tnt.rate.ui.RateDialog
import com.tnt.rate.ui.RateDialogFeedBack

object RateManager {

    // --- Config ---
    private var enable: Boolean = false
    lateinit var config: RateConfig
    private var rateLayout: Int = 0
    private var feedbackLayout: Int = 0
    private var feedbackItemLayout: Int = 0

    // --- State ---
    private var dialog: Dialog? = null
    private var dismissNextFlag = false
    var isInit = false

    fun init(
        context: Context,
        enable: Boolean,
        customConfig: RateConfig,
    ) {
        if (isInit || !enable) return

        this.enable = true
        this.config = customConfig
        this.rateLayout = customConfig.layoutConfig.rateLayout
        this.feedbackLayout = customConfig.layoutConfig.feedbackLayout
        this.feedbackItemLayout = customConfig.layoutConfig.feedbackItemLayout

        RatePrefs.increaseSession(context)
        RatePrefs.saveDisableType(context, customConfig.disableType)
        isInit = true
    }

    fun showRate(context: Context, isShowNow: Boolean = false, callback: RateCallback) {
        if (shouldSkipShow(context, isShowNow)) return

        dialog = RateDialog(
            context = context,
            layoutRes = rateLayout,
            rateOptions = config.rateOptions,
            btnConfig = config.buttonRate,
            callback = callback
        ).apply { showDialog() }
        recordShow(context)
    }

    fun showFeedBack(context: Context, isShowNow: Boolean = false, callback: RateCallback) {
        if (shouldSkipShow(context, isShowNow)) return

        dialog = RateDialogFeedBack(
            context = context,
            layoutRes = feedbackLayout,
            layoutItemFeedBack = feedbackItemLayout,
            feedbackReasons = config.feedbackReasons,
            btnConfig = config.buttonFeedback,
            callback = callback
        ).apply {
            showDialog()
        }
        recordShow(context)
    }

    fun dismissNext() {
        dismissNextFlag = true
    }

    // --- Helpers ---
    private fun recordShow(context: Context) {
        RatePrefs.saveShowTime(context)
    }

    private fun shouldSkipShow(context: Context, isShowNow: Boolean): Boolean {
        if (!isInit) return true
        if (!isShowNow && !canShow(context)) return true
        if (dismissNextFlag) {
            dismissNextFlag = false
            return true
        }
        return false
    }

    private fun canShow(context: Context): Boolean {
        if (!enable) {
            log("Not show → enable=false")
            return false
        }

        val sessionCount = RatePrefs.getSession(context)
        val lastShowTime = RatePrefs.getLastShowTime(context)
        val totalShowCount = RatePrefs.getTotalShow(context)
        val lastStars = RatePrefs.getLastStars(context)
        val showCountThisSession = RatePrefs.getShowCountThisSession(context)
        val now = System.currentTimeMillis()

        log("🔹 Check conditions:")
        log("   - SessionCount=$sessionCount / minSession=${config.minSession}")
        log("   - ShowCountThisSession=$showCountThisSession / maxShowPerSession=${config.maxShowPerSession}")
        log("   - LastShowTime=$lastShowTime [${if (lastShowTime > 0) now - lastShowTime else 0}ms] / MinInterval=${config.minIntervalMillis}ms")
        log("   - TotalShowCount=$totalShowCount / MaxTotalShow=${config.maxTotalShow}")
        log("   - LastStars=$lastStars / DisableAfterStars=${config.disableAfterStars}")
        log("   - DisableType=${config.disableType}")

        val reasons = mutableListOf<String>()

        if (config.disableAfterStars > 0 && lastStars >= config.disableAfterStars) {
            when (RatePrefs.getDisableType(context)) {
                RateConfig.DisableType.FOREVER -> {
                    reasons.add("lastStars=$lastStars >= disableAfterStars=${config.disableAfterStars} → disable vĩnh viễn")
                }

                RateConfig.DisableType.SESSION -> {
                    reasons.add("lastStars=$lastStars >= disableAfterStars=${config.disableAfterStars} → disable trong phiên hiện tại")
                }
            }
        }

        if (sessionCount < config.minSession) {
            reasons.add("sessionCount=$sessionCount < minSession=${config.minSession}")
        }

        if (config.maxTotalShow > 0 && totalShowCount >= config.maxTotalShow) {
            reasons.add("totalShowCount=$totalShowCount >= maxTotalShow=${config.maxTotalShow}")
        }

        if (config.maxShowPerSession > 0 && showCountThisSession >= config.maxShowPerSession) {
            reasons.add("showCountThisSession=$showCountThisSession >= maxShowPerSession=${config.maxShowPerSession}")
        }

        if (config.minIntervalMillis > 0 && now - lastShowTime < config.minIntervalMillis) {
            reasons.add("TimeBetween=${now - lastShowTime}ms < minIntervalMillis=${config.minIntervalMillis}")
        }

        return if (reasons.isEmpty()) {
            log("____________________SHOW DIALOG RATE___________________")
            true
        } else {
            log("____________________NOT SHOW RATE___________________")
            reasons.forEachIndexed { i, o -> log("$i, ==> $o") }
            false
        }
    }

    fun reset(context: Context) {
        RatePrefs.clearAll(context)
        isInit = false
        runCatching {
            init(context, enable, config)
        }
    }

    private fun log(msg: String) {
        android.util.Log.e("RateConfig", msg)
    }
}
