package br.com.projetopi.smartlock

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import br.com.projetopi.smartlock.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

class MarkerInfoAdapter(private val context: Context): GoogleMap.InfoWindowAdapter {

    override fun getInfoWindow(marker: Marker): View? = null

    override fun getInfoContents(marker: Marker): View? {
        val place = marker.tag as? Place ?: return null

        val view = LayoutInflater.from(context).inflate(R.layout.custom_marker_info, null)

        view.findViewById<TextView>(R.id.txt_tittle).text = place.name
        view.findViewById<TextView>(R.id.txt_address).text = place.address
        view.findViewById<TextView>(R.id.txt_reference).text = place.reference

        return view
    }
}