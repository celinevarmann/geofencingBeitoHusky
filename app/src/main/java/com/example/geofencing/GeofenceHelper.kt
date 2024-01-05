package com.example.geofencing

import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.maps.model.LatLng



class GeofenceHelper(base: Context) : ContextWrapper(base) {

    private val TAG : String = "GeofenceHelper"
    lateinit var pendingIntent : PendingIntent

    fun getGeofencingRequest (geofence : Geofence) : GeofencingRequest{
        return GeofencingRequest.Builder()
            .addGeofence(geofence)
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER) // Hvis man allerede er inne i geofence vil det aktiveres
            .build()
    }

    public fun getGeofence(id : String, latLng : LatLng, radius : Float, transitionTypes : Int) : Geofence {
        return Geofence.Builder()
            .setCircularRegion(latLng.latitude, latLng.longitude, radius)
            .setRequestId(id)
            .setTransitionTypes(transitionTypes)
            .setLoiteringDelay(0) //Tid i millisekunder - Hvor lang tid det skal ta før du går inn i området til du får varsel
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .build()
    }

    public fun collectPendingIntent() : PendingIntent{
        /*if (pendingIntent != null) {
            return pendingIntent
        }
        else{*/
        val intent : Intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(this, 2607, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        return pendingIntent
    }

    fun getErrorString(e: Exception): String? {
        if (e is ApiException) {
            val apiException = e as ApiException
            when (apiException.statusCode) {
                GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE -> return "GEOFENCE_NOT_AVAILABLE"
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES -> return "GEOFENCE_TOO_MANY_GEOFENCES"
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS -> return "GEOFENCE_TOO_MANY_PENDING_INTENTS"
            }
        }
        return e.localizedMessage
    }

}
