package com.tnt.rate.core

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.os.StrictMode
import android.util.DisplayMetrics
import com.tnt.rate.model.RateOption
import java.util.Locale
import java.util.TimeZone

object RateUtils {

    fun getCurrentVersion(): String {
        try {
            val pInfo: PackageInfo =
                RateManager.context.packageManager.getPackageInfo(
                    RateManager.context.packageName, 0
                )
            return pInfo.versionName.toString()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return "1.0.0"
    }

    fun disableExposure() {
        try {
            val m = StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
            m.invoke(null)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun getRateOptions(
        messages: List<Int>,
        previews: List<Int>,
        starFullDf: Int,
        starEmptyDf: Int,
        overrides: Map<Int, Pair<Int, Int>> = emptyMap()
    ): List<RateOption> {
        return messages.mapIndexed { index, msg ->
            if (index == 0) {
                RateOption(
                    iconPreview = previews[index],
                    messageRes = msg,
                    isDefault = true
                )
            } else {
                val (full, empty) = overrides[index] ?: (starFullDf to starEmptyDf)
                RateOption(
                    iconPreview = previews[index],
                    starFullIcon = full,
                    starEmptyIcon = empty,
                    messageRes = msg
                )
            }
        }
    }

    fun getDeviceInfo(): String {
        val densityText = when (Resources.getSystem().displayMetrics.densityDpi) {
            DisplayMetrics.DENSITY_LOW -> "LDPI"
            DisplayMetrics.DENSITY_MEDIUM -> "MDPI"
            DisplayMetrics.DENSITY_HIGH -> "HDPI"
            DisplayMetrics.DENSITY_XHIGH -> "XHDPI"
            DisplayMetrics.DENSITY_XXHIGH -> "XXHDPI"
            DisplayMetrics.DENSITY_XXXHIGH -> "XXXHDPI"
            else -> "HDPI"
        }

        //TODO: Update android Q
        val stat = StatFs(Environment.getExternalStorageDirectory().path)
        var megAvailable = 0L
        val bytesAvailable: Long
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bytesAvailable = stat.blockSizeLong * stat.availableBlocksLong
            megAvailable = bytesAvailable / (1024 * 1024)
        }


        return "Manufacturer ${Build.MANUFACTURER}, Model ${Build.MODEL}," + " ${Locale.getDefault()}, " + "osVer ${Build.VERSION.RELEASE}, Screen ${Resources.getSystem().displayMetrics.widthPixels}x${Resources.getSystem().displayMetrics.heightPixels}, " + "$densityText, Free space ${megAvailable}MB, TimeZone ${
            TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT)
        }"
    }
}