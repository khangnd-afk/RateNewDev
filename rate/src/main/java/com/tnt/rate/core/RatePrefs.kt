package com.tnt.rate.core

import android.content.Context
import com.tnt.rate.model.RateConfig

internal object RatePrefs {
    private const val PREF_NAME = "rate_pref"
    private const val KEY_LAST_SHOW = "last_show_time"
    private const val KEY_SESSION_COUNT = "session_count"
    private const val KEY_TOTAL_SHOW = "total_show_count"
    private const val KEY_SHOW_THIS_SESSION = "show_this_session"
    private const val KEY_LAST_STARS = "last_stars"
    private const val KEY_DISABLE_THIS_SESSION = "disable_this_session"
    private const val KEY_DISABLE_TYPE = "disable_type"

    private fun pref(context: Context) =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun getSession(context: Context): Int =
        pref(context).getInt(KEY_SESSION_COUNT, -1)

    fun increaseSession(context: Context): Int {
        val current = getSession(context) + 1
        pref(context).edit().putInt(KEY_SESSION_COUNT, current)
            .putInt(KEY_SHOW_THIS_SESSION, 0)
            .putBoolean(KEY_DISABLE_THIS_SESSION, false)
            .apply()
        return current
    }

    fun getLastShowTime(context: Context): Long =
        pref(context).getLong(KEY_LAST_SHOW, 0)

    fun saveShowTime(context: Context) {
        pref(context).edit().putLong(KEY_LAST_SHOW, System.currentTimeMillis()).apply()
        increaseTotalShow(context)
        increaseShowThisSession(context)
    }

    fun getTotalShow(context: Context): Int =
        pref(context).getInt(KEY_TOTAL_SHOW, 0)

    private fun increaseTotalShow(context: Context): Int {
        val current = getTotalShow(context) + 1
        pref(context).edit().putInt(KEY_TOTAL_SHOW, current).apply()
        return current
    }

    fun getShowCountThisSession(context: Context): Int =
        pref(context).getInt(KEY_SHOW_THIS_SESSION, 0)

    private fun increaseShowThisSession(context: Context): Int {
        val current = getShowCountThisSession(context) + 1
        pref(context).edit().putInt(KEY_SHOW_THIS_SESSION, current).apply()
        return current
    }

    fun saveLastStars(context: Context, stars: Int) {
        pref(context).edit().putInt(KEY_LAST_STARS, stars).apply()
    }

    fun getLastStars(context: Context): Int =
        pref(context).getInt(KEY_LAST_STARS, 0)

    fun disableThisSession(context: Context) {
        pref(context).edit().putBoolean(KEY_DISABLE_THIS_SESSION, true).apply()
    }

    fun isDisabledThisSession(context: Context): Boolean =
        pref(context).getBoolean(KEY_DISABLE_THIS_SESSION, false)

    fun saveDisableType(context: Context, type: RateConfig.DisableType) {
        if (getDisableType(context) == RateConfig.DisableType.SESSION) {
            saveLastStars(context, 0)
        }
        pref(context).edit().putString(KEY_DISABLE_TYPE, type.name).apply()
    }

    fun getDisableType(context: Context): RateConfig.DisableType {
        val typeName =
            pref(context).getString(KEY_DISABLE_TYPE, RateConfig.DisableType.SESSION.name)
        return RateConfig.DisableType.valueOf(typeName ?: RateConfig.DisableType.SESSION.name)
    }

    fun clearAll(context: Context) {
        pref(context).edit().clear().apply()
    }
}

