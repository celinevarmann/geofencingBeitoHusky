package com.example.geofencing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    var TAG = "GeofenceBroadcastReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.

        //val notificationHelper = NotificationHelper(context)
        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        if (geofencingEvent != null) {
            if (geofencingEvent.hasError()){
                Log.d(TAG, "onReceive: Error receiving geofence event...")
                return
            }

            val geofenceList = geofencingEvent.triggeringGeofences
            for (geofence in geofenceList!!){
                Log.d(TAG, "onReceive:" + geofence.requestId)
            }

            val transitionType = geofencingEvent.geofenceTransition

            // Hva som skjer når man går inn, befinner seg i, og går ut av et geofence
            when(transitionType){
                Geofence.GEOFENCE_TRANSITION_ENTER -> {
                    Toast.makeText(context, "GEOFENCE_TRANSITION_ENTER", Toast.LENGTH_SHORT).show()
                    //notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_ENTER", "", MapsActivity::class.java)
                }
                Geofence.GEOFENCE_TRANSITION_DWELL -> {
                    Toast.makeText(context, "GEOFENCE_TRANSITION_DWELL", Toast.LENGTH_SHORT).show()
                    //notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_DWELL", "",MapsActivity::class.java)
                }
                Geofence.GEOFENCE_TRANSITION_EXIT -> {
                    Toast.makeText(context, "GEOFENCE_TRANSITION_EXIT", Toast.LENGTH_SHORT).show()
                    //notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_EXIT", "",MapsActivity::class.java)
                }
            }
        }

    }
}