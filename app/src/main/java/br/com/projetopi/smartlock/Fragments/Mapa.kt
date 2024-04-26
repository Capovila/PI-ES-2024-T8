package br.com.projetopi.smartlock.Fragments

import SharedViewModelEstablishment
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import br.com.projetopi.smartlock.Classes.Establishment
import br.com.projetopi.smartlock.Classes.User
import br.com.projetopi.smartlock.MainActivity
import br.com.projetopi.smartlock.MarkerInfoAdapter
import br.com.projetopi.smartlock.R
import br.com.projetopi.smartlock.SimpleStorage
import br.com.projetopi.smartlock.databinding.FragmentMapaBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

//Código mínimo para uma fragment usual
class Mapa() : Fragment() {

    private val establishments: ArrayList<Establishment> = arrayListOf()

    private var _binding: FragmentMapaBinding? = null
    private val binding get() = _binding!!

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var db: FirebaseFirestore

    private lateinit var simpleStorage: SimpleStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapaBinding.inflate(inflater,container,false)

        simpleStorage = SimpleStorage(requireContext())

        val user: User = simpleStorage.getUserAccountData()

        binding.lnlaBtnMenuFragment.visibility = View.GONE

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

        val sharedViewModelEstablishment: SharedViewModelEstablishment by activityViewModels()

        db = Firebase.firestore

        db.collection("rentals").whereEqualTo("rentalImplemented", false)
            .whereEqualTo("idUser", user.uid).get().addOnSuccessListener {
                for(documents in it){
                    Toast.makeText(requireContext(), "Existe uma locação para ser efetivada", Toast.LENGTH_LONG).show()
                }
            }

        db.collection("establishments").get().addOnSuccessListener { documents ->
            for (document in documents) {
                val id = document.id
                val name = document.getString("name") ?: ""
                val latitude = document.getDouble("latitude") ?: 0.0
                val longitude = document.getDouble("longitude") ?: 0.0
                val address = document.getString("address") ?: ""
                val reference = document.getString("description") ?: ""
                val managerName = document.getString("managerName") ?: ""

                val establishment = Establishment(id, name, LatLng(latitude, longitude), address, reference, managerName)

                establishments?.add(establishment)
            }

            val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragmentMain) as SupportMapFragment

            mapFragment.getMapAsync{ googleMap ->
                addMarkers(googleMap)

                googleMap.setInfoWindowAdapter(MarkerInfoAdapter(requireContext()))

                googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(),
                    R.raw.map_style
                ))

                googleMap.uiSettings.isMapToolbarEnabled = false

                googleMap.setOnMarkerClickListener {marker ->
                    val markerPosition = marker.position
                    val markerLatitude = markerPosition.latitude
                    val markerLongitude = markerPosition.longitude
                    val markerLatLng = LatLng(markerLatitude, markerLongitude)

                    binding.lnlaBtnMenuFragment.visibility = View.VISIBLE

                    binding.btnIrFragment.setOnClickListener{
                        startActivity(Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?q=$markerLatitude,$markerLongitude")
                        )
                        )
                    }

                    binding.btnAlugarFragment.setOnClickListener{
                        db.collection("rentals")
                            .whereEqualTo("idUser", user.uid)
                            .whereEqualTo("rentalImplemented", false)
                            .get()
                            .addOnSuccessListener {
                                if(!it.isEmpty){
                                    Toast.makeText(requireContext(), "Existe uma locação para ser efetivada", Toast.LENGTH_LONG).show()
                                } else {
                                    db.collection("rentals")
                                        .whereEqualTo("idUser", user.uid)
                                        .whereEqualTo("rentalOpen", true)
                                        .get()
                                        .addOnSuccessListener {
                                            if(!it.isEmpty){
                                                Toast.makeText(requireContext(), "Você ja possui uma locação aberta", Toast.LENGTH_LONG).show()
                                            } else {
                                                db.collection("users")
                                                    .whereEqualTo("uid", user.uid)
                                                    .whereEqualTo("cardRegistred", false)
                                                    .get()
                                                    .addOnSuccessListener {
                                                        if(!it.isEmpty){
                                                            Toast.makeText(requireContext(), "Você precisa ter um cartão cadastrado", Toast.LENGTH_LONG).show()
                                                        } else {
                                                            if (ActivityCompat.checkSelfPermission(
                                                                    requireContext(),
                                                                    Manifest.permission.ACCESS_FINE_LOCATION
                                                                ) != PackageManager.PERMISSION_GRANTED &&
                                                                ActivityCompat.checkSelfPermission(
                                                                    requireContext(),
                                                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                                                ) != PackageManager.PERMISSION_GRANTED) {
                                                                requestPermission()
                                                            } else {
                                                                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                                                                    if (task.isSuccessful) {
                                                                        val location: Location? = task.result
                                                                        if (location != null) {
                                                                            val userLatLng = LatLng(location.latitude, location.longitude)
                                                                            val distanciaDoUsuario =
                                                                                calcularDistanciaEmMetros(userLatLng, markerLatLng)
                                                                            if (distanciaDoUsuario < 150.0) {
                                                                                val markerEstablishment: Establishment = marker.tag as Establishment
                                                                                sharedViewModelEstablishment.selectEstablishment(markerEstablishment)
                                                                                (activity as MainActivity).changeFragment(OpcaoTempo())
                                                                            } else {
                                                                                Toast.makeText(requireContext(), "Você está distante do armário", Toast.LENGTH_LONG).show()
                                                                            }
                                                                        } else {
                                                                            Toast.makeText(
                                                                                requireContext(),
                                                                                "Habilite sua localização",
                                                                                Toast.LENGTH_LONG
                                                                            ).show()
                                                                        }
                                                                    } else {
                                                                        Toast.makeText(
                                                                            requireContext(),
                                                                            "Erro ao obter a localização",
                                                                            Toast.LENGTH_LONG
                                                                        ).show()
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                            }
                                        }
                                }
                            }
                    }
                    false
                }

                googleMap.setOnInfoWindowCloseListener {
                    binding.lnlaBtnMenuFragment.visibility = View.GONE
                }

                googleMap.setOnMapLoadedCallback {
                    val bounds = LatLngBounds.builder()
                    establishments?.forEach{
                        bounds.include(it.latLng)
                    }

                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 300))
                }

            }
        }
        return binding.root
    }

    private fun addMarkers(googleMap: GoogleMap) {
        establishments?.forEach {establishment ->
            val marker = googleMap.addMarker(
                MarkerOptions()
                    .title(establishment.name)
                    .snippet(establishment.address)
                    .position(establishment.latLng)
                    .alpha(0.8f)
            )
            if (marker != null) {
                marker.tag = establishment
            }
        }
    }

    private fun calcularDistanciaEmMetros(userLocation: LatLng, destinationLocation: LatLng): Float {
        val result = FloatArray(1)
        Location.distanceBetween(
            userLocation.latitude, userLocation.longitude,
            destinationLocation.latitude, destinationLocation.longitude,
            result
        )
        return result[0]
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireContext() as Activity, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION), 100)
    }
}