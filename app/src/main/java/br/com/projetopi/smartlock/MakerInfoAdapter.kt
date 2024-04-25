package br.com.projetopi.smartlock

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import br.com.projetopi.smartlock.Classes.Establishment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class MarkerInfoAdapter(private val context: Context): GoogleMap.InfoWindowAdapter {

    override fun getInfoWindow(marker: Marker): View? = null

    override fun getInfoContents(marker: Marker): View? {
        val establishment = marker.tag as? Establishment ?: return null

        val view = LayoutInflater.from(context).inflate(R.layout.custom_marker_info, null)

        view.findViewById<TextView>(R.id.txt_tittle).text = establishment.name
        view.findViewById<TextView>(R.id.txt_address).text = establishment.address
        view.findViewById<TextView>(R.id.txt_reference).text = establishment.reference

        return view
    }
}