package ru.korshun.batteryinformer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.util.Log
import androidx.lifecycle.MutableLiveData

class BatteryChargeReceiver : BroadcastReceiver() {

    val mData = MutableLiveData<Event>()

    override fun onReceive(context: Context, intent: Intent) {

        val mSharedPreferencesManager = SharedPreferencesManager.getInstance(context)
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val lastLevel = mSharedPreferencesManager.getIntValue(
            SharedPreferencesManager.SP_LAST_LEVEL_VALUE)

        if (lastLevel == -1) {
            mSharedPreferencesManager.setIntValue(
                SharedPreferencesManager.SP_LAST_LEVEL_VALUE, level)
            return
        }

        if (lastLevel != level) {

            mSharedPreferencesManager.setIntValue(
                SharedPreferencesManager.SP_LAST_LEVEL_VALUE, level)

            val minLevel =
                mSharedPreferencesManager.getIntValue(SharedPreferencesManager.SP_MIN_VALUE)
            val maxLevel =
                mSharedPreferencesManager.getIntValue(SharedPreferencesManager.SP_MAX_VALUE)
            val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            val isCharging = status == BatteryManager.BATTERY_STATUS_FULL ||
                    status == BatteryManager.BATTERY_STATUS_CHARGING
            val eventType =
                if (level <= minLevel && !isCharging)
                    context.resources.getString(R.string.event_low)
                else if (level >= maxLevel && isCharging)
                    context.resources.getString(R.string.event_high)
                else
                    null
            val eventValue =
                when {
                    level <= minLevel -> minLevel
                    level >= maxLevel -> maxLevel
                    else -> -1
                }

            mData.postValue(Event(eventType, eventValue))

        }

    }


}