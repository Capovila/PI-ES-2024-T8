package br.com.projetopi.smartlock.Classes

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager

class GpsChangeReceiver(private val onGpsStatusChangeListener: OnGpsStatusChangeListener) :
    BroadcastReceiver() {

    interface OnGpsStatusChangeListener {
        fun onGpsStatusChanged(isGpsEnabled: Boolean)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action.equals(LocationManager.PROVIDERS_CHANGED_ACTION)) {
            val locationManager =
                context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            onGpsStatusChangeListener.onGpsStatusChanged(isGpsEnabled)
        }
    }
}