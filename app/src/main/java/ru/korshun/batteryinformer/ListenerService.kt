package ru.korshun.batteryinformer

import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.Observer

class ListenerService : LifecycleService() {

    private val mBatteryChargeReceiver = BatteryChargeReceiver()
    var mAppNotification: AppNotification? = null
    private val mBinder = LocalBinder()


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        return START_STICKY
    }


    override fun onCreate() {
        super.onCreate()

        registerReceiver(mBatteryChargeReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

        mAppNotification = AppNotification.getInstance(applicationContext)

        startForeground(
            mAppNotification!!.NOTIFICATION_ID_REAL_TIME,
            mAppNotification!!.getRealtimeNotification()
        )

        mAppNotification?.showRealtimeNotification()

        val liveData = Observer<Event> { event ->
            mAppNotification!!.showRealtimeNotification()

            if (event.type != null) {

                val notificationText: String = String.format(
                    applicationContext.resources.getString(R.string.notification_event_text),
                    event.type,
                    event.value)
                mAppNotification!!.showEventNotification(notificationText)
            }

        }

        mBatteryChargeReceiver.mData.observe(this, liveData)
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mBatteryChargeReceiver)
        mAppNotification = null
    }


    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        unregisterReceiver(mBatteryChargeReceiver)
        mAppNotification = null
    }


    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return mBinder
    }


    inner class LocalBinder : Binder() {
        fun getService(): ListenerService = this@ListenerService
    }

}