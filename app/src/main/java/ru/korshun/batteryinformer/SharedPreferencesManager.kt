package ru.korshun.batteryinformer

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesManager private constructor(context: Context) {

    private val mSharedPreferences: SharedPreferences =
        context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)

    companion object : SingletonHolder<SharedPreferencesManager, Context>(
        ::SharedPreferencesManager) {
        const val SP_MIN_VALUE = "sp_min_value"
        const val SP_MAX_VALUE = "sp_max_value"
        const val SP_LAST_LEVEL_VALUE = "sp_last_level_value"

        const val MIN_VALUE_DEFAULT = 23
        const val MAX_VALUE_DEFAULT = 77
    }

    init {
        if (!containsKey(SP_MIN_VALUE))
            setIntValue(SP_MIN_VALUE, MIN_VALUE_DEFAULT)
        if (!containsKey(SP_MAX_VALUE))
            setIntValue(SP_MAX_VALUE, MAX_VALUE_DEFAULT)
    }


    fun setIntValue(key: String, value: Int) {
        mSharedPreferences
            .edit()
            .putInt(key, value)
            .apply()
    }

    fun getIntValue(key: String): Int {
        return mSharedPreferences.getInt(key, -1)
    }

    fun containsKey(key: String): Boolean {
        return mSharedPreferences.contains(key)
    }


}