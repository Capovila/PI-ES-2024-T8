package br.com.projetopi.smartlock

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar


//Código mínimo para uma fragment usual
class Mapa() : Fragment() {

    private val places = arrayListOf(
        Place("Ponto1", LatLng(-22.834554,-47.055358), "Av. Profa. Ana Maria Silvestre Adade, 825 - Parque das Universidades",  "Em frente a PUC Campinas") ,
        Place("Ponto2", LatLng(-22.847644, -47.062139), "Parque Dom Pedro, Jardim Santa Genebra", "Na entrada das águas")
    )

    private lateinit var lnlaBtnMenuFragment: LinearLayoutCompat
    private lateinit var btnIrFragment: Button
    private lateinit var btnAlugarFragment: Button

    private fun addMarkers(googleMap: GoogleMap) {

        //Para cada lugar da lista places adiciona um marcador com as opcoes definidas
        places.forEach {place ->
            val marker = googleMap.addMarker(
                MarkerOptions()
                    .title(place.name)
                    .snippet(place.address)
                    .position(place.latLng)
                    .icon(
                        BitmapHelper.vectorToBitmap(requireContext(), R.drawable.logo, ContextCompat.getColor(requireContext(), R.color.white))
                    )
                    .alpha(0.8f)
            )
            if (marker != null) {
                marker.tag = place
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_mapa, container, false)
        lnlaBtnMenuFragment = root.findViewById(R.id.lnlaBtnMenuFragment)
        btnIrFragment = root.findViewById(R.id.btnIrFragment)
        btnAlugarFragment = root. findViewById(R.id.btnAlugarFragment)

        lnlaBtnMenuFragment.visibility = View.GONE

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragmentMain) as SupportMapFragment

        mapFragment.getMapAsync{ googleMap ->
            addMarkers(googleMap)

            googleMap.setInfoWindowAdapter(MarkerInfoAdapter(requireContext()))

            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style))

            googleMap.uiSettings.isMapToolbarEnabled = false

            googleMap.setOnMarkerClickListener {marker ->

                val markerPosition = marker.position
                val markerLatitude = markerPosition.latitude
                val markerLongitude = markerPosition.longitude

                lnlaBtnMenuFragment.visibility = View.VISIBLE

                btnIrFragment.setOnClickListener{
                    startActivity(Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?q=$markerLatitude,$markerLongitude")
                    )
                    )
                }

                btnAlugarFragment.setOnClickListener{
                    Snackbar.make(btnAlugarFragment, "Alugar armário", Snackbar.LENGTH_LONG).show()
                }
                false
            }

            googleMap.setOnInfoWindowCloseListener {
                lnlaBtnMenuFragment.visibility = View.GONE
            }

            googleMap.setOnMapLoadedCallback {
                val bounds = LatLngBounds.builder()
                places.forEach{
                    bounds.include(it.latLng)
                }

                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 300))
            }

        }



        return root
    }
}