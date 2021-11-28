package ru.korshun.batteryinformer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import ru.korshun.batteryinformer.databinding.ActivityMainBinding
import ru.korshun.batteryinformer.databinding.DialogMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var mActivityMainBinding: ActivityMainBinding
    private lateinit var mService: ListenerService

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        mActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mActivityMainBinding.root)

        startService(Intent(this, ListenerService::class.java))

        showSettingsWindow()
    }


    override fun onStart() {
        super.onStart()
        Intent(this, ListenerService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }


    override fun onStop() {
        super.onStop()
        unbindService(connection)
    }


    private fun showSettingsWindow() {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogBinding = DialogMainBinding.inflate(inflater)
        val currentMinValue = SharedPreferencesManager.getInstance(this)
            .getIntValue(SharedPreferencesManager.SP_MIN_VALUE)
        val currentMaxValue = SharedPreferencesManager.getInstance(this)
            .getIntValue(SharedPreferencesManager.SP_MAX_VALUE)

        dialogBinding.monitoringRangeSlider.addOnChangeListener { slider, _, _ ->
            dialogBinding.monitoringMinValue.text =
                String.format(
                    Locale.getDefault(), "%d%%",
                    Math.round(slider.values[0])
                )
            dialogBinding.monitoringMaxValue.text =
                String.format(
                    Locale.getDefault(), "%d%%",
                    Math.round(slider.values[1])
                )
        }

        dialogBinding.btnSave.setOnClickListener {

            SharedPreferencesManager.getInstance(this).setIntValue(
                SharedPreferencesManager.SP_MIN_VALUE,
                Math.round(dialogBinding.monitoringRangeSlider.values[0])
            )
            SharedPreferencesManager.getInstance(this).setIntValue(
                SharedPreferencesManager.SP_MAX_VALUE,
                Math.round(dialogBinding.monitoringRangeSlider.values[1])
            )
            mService.mAppNotification?.showRealtimeNotification()
            finish()
        }

        dialogBinding.monitoringRangeSlider.setValues(
            currentMinValue.toFloat(), currentMaxValue.toFloat())
        dialogBinding.monitoringMinValue.text =
            String.format(Locale.getDefault(), "%s%%", currentMinValue)
        dialogBinding.monitoringMaxValue.text =
            String.format(Locale.getDefault(), "%s%%", currentMaxValue)

        builder
            .setCancelable(false)
            .setView(dialogBinding.root)
            .create()
            .show()
    }


    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as ListenerService.LocalBinder
            mService = binder.getService()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {}
    }

}