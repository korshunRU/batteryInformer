package ru.korshun.batteryinformer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import java.text.DateFormat
import java.util.*

class AppNotification private constructor(context: Context) {

    private val mNotificationManager =
        context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    private val mContext = context

    private val CHANNEL_ID_REAL_TIME = "channel_real_time"
    private val CHANNEL_ID_EVENT = "channel_event"
    val NOTIFICATION_ID_REAL_TIME = 123456780
    private val NOTIFICATION_ID_EVENT = 123456781

    companion object : SingletonHolder<AppNotification, Context>(::AppNotification)

    init {
        createNotificationChannel(
            CHANNEL_ID_REAL_TIME,
            mContext.getString(R.string.app_name),
            mContext.getString(R.string.notification_real_time_desc)
        )

        createNotificationChannel(
            CHANNEL_ID_EVENT,
            mContext.getString(R.string.app_name),
            mContext.getString(R.string.notification_event_desc)
        )
    }

    private fun createNotificationChannel(
        id : String,
        name : String,
        description : String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = description
            channel.enableVibration(true)

            mNotificationManager.createNotificationChannel(channel)

        }

    }


    fun getRealtimeNotification(): Notification {
        val activityPendingIntent = PendingIntent.getActivity(
            mContext, 0,
            Intent(mContext, MainActivity::class.java), 0
        )
        val sharedPreferencesManager = SharedPreferencesManager.getInstance(mContext)
        val text = String.format(
            mContext.getString(R.string.notification_real_time_text),
            DateFormat.getDateTimeInstance().format(Date()),
            sharedPreferencesManager.getIntValue(SharedPreferencesManager.SP_MIN_VALUE),
            sharedPreferencesManager.getIntValue(SharedPreferencesManager.SP_MAX_VALUE)
        )

        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(mContext, CHANNEL_ID_REAL_TIME)
                .setContentIntent(activityPendingIntent)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(text)
                )
                .setContentTitle(mContext.resources.getString(R.string.app_name))
                .setContentText(text)
                .setOngoing(true)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(getBitmapFromVectorDrawable(mContext, R.mipmap.ic_launcher))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID_REAL_TIME)
        }

        return builder.build()
    }


    fun showRealtimeNotification() {
        mNotificationManager.notify(NOTIFICATION_ID_REAL_TIME, getRealtimeNotification())
    }


    private fun getEventNotification(text : String): Notification {
        val activityPendingIntent = PendingIntent.getActivity(
            mContext, 0,
            Intent(mContext, MainActivity::class.java), 0
        )

        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(mContext, CHANNEL_ID_EVENT)
                .setContentIntent(activityPendingIntent)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(text)
                )
                .setContentTitle(mContext.resources.getString(R.string.app_name))
                .setContentText(text)
                .setAutoCancel(true)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(getBitmapFromVectorDrawable(mContext, R.mipmap.ic_launcher))
                .setWhen(System.currentTimeMillis())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID_EVENT)
        }

        return builder.build()
    }


    fun showEventNotification(text : String) {
        mNotificationManager.notify(NOTIFICATION_ID_EVENT, getEventNotification(text))
    }


    private fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(context, drawableId) ?: return null
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

}