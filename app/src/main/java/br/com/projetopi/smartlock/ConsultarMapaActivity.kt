package br.com.projetopi.smartlock

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.projetopi.smartlock.Classes.Establishment
import br.com.projetopi.smartlock.databinding.ActivityConsultarMapaBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.util.Timer
import java.util.TimerTask

class ConsultarMapaActivity : AppCompatActivity() {

    private val establishments: ArrayList<Establishment>? = arrayListOf()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var binding: ActivityConsultarMapaBinding
    private lateinit var db: FirebaseFirestore
    private val timer = Timer()
    private var userMarker: Marker? = null
    private var isTimerRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        if(!isLocationEnable()) {
            Toast.makeText(baseContext, "Você precisa estar com o GPS ligado", Toast.LENGTH_LONG).show()
            finish()
            isTimerRunning = false
        } else {
            isTimerRunning = true
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        binding = ActivityConsultarMapaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = Firebase.firestore

        // Esconde o lnlaBtnMenu
        binding.lnlaBtnMenu.visibility = View.GONE

        /***
         * Busca estabelecimentos e para cada estabelecimento buscado, pega
         * os dados do estabelecimento, atribui-os em uma variavel do tipo
         * Establishment e adiciona à uma lista de estabelecimentos
         */
        db.collection("establishments").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val id = document.id
                    val name = document.getString("name") ?: ""
                    val latitude = document.getDouble("latitude") ?: 0.0
                    val longitude = document.getDouble("longitude") ?: 0.0
                    val address = document.getString("address") ?: ""
                    val reference = document.getString("reference") ?: ""
                    val managerName = document.getString("managerName") ?: ""

                    val establishment = Establishment(
                        id,
                        name,
                        LatLng(latitude, longitude),
                        address,
                        reference,
                        managerName)

                    establishments?.add(establishment)
                }

                // Atribui à variavel mapFragment o supportFragment map_fragment
                val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment

                /***
                 * Quando é obtido o mapa assincrono, adiciona os marcadores,
                 * define as janela de informações dos marcadores, define o estilo do mapa exibido e
                 * define o uiSettings.isMapToolbarEnabled como false
                 */
                mapFragment.getMapAsync { googleMap ->
                    mapFragment.view?.visibility = View.GONE

                    addMarkers(googleMap)

                    googleMap.setInfoWindowAdapter(MarkerInfoAdapter(this))

                    googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))

                    googleMap.uiSettings.isMapToolbarEnabled = false

                    /***
                     * Quando um marcador é clicado atribui em variaveis algumas informações e
                     * mostra o lnlaBtnMenuFragment
                     */
                    googleMap.setOnMarkerClickListener { marker ->
                        if(marker.title != "Sua localização atual"){

                            val markerPosition = marker.position
                            val markerLatitude = markerPosition.latitude
                            val markerLongitude = markerPosition.longitude

                            // Mostra o lnlaBtnMenu
                            binding.lnlaBtnMenu.visibility = View.VISIBLE

                            /***
                             * Quando o btnIrFragment é clicado, direciona o usuario ao google maps
                             * com a latitude e longitude do marcador selecionado para que seja
                             * traçada a rota
                             */
                            binding.btnIr.setOnClickListener {

                                startActivity(Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("http://maps.google.com/maps?q=$markerLatitude,$markerLongitude")
                                )
                                )
                            }

                            /***
                             * Quando o btnAlugar é clicado, mostra um Toast com a mensagem
                             * de que é necessaio entrar com a conta do usuario para alugar um armario,
                             * fechando a activity atual
                             */
                            binding.btnAlugar.setOnClickListener {
                                Toast.makeText(baseContext, "Você precisa entrar com sua conta para alugar um armário", Toast.LENGTH_LONG).show()
                                finish()
                                isTimerRunning = false
                            }
                        }
                        false
                    }

                    // Quando a janela de informações do marcador é fechada, esconde o lnlaBtnMenuFragment
                    googleMap.setOnInfoWindowCloseListener {
                        binding.lnlaBtnMenu.visibility = View.GONE
                    }

                    /***
                     * Quando o mapa é carregado, verifica se o aplicativo tem acesso a localização do usuario, pega a
                     * latitude e longitude do usuario e adiciona um marcador com a localização do usuario, centralizando
                     * a camera do mapa no usuario, se o aplicativo não tiver acesso a localização do usuario e o GPS do
                     * dispositivo nao estiver ligado, não é carregado o mapa
                     */
                    googleMap.setOnMapLoadedCallback{
                        if (ActivityCompat.checkSelfPermission(
                                this,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(
                                this,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED && isLocationEnable()) {

                                Handler().postDelayed({
                                    binding.loadView.visibility = View.GONE
                                    mapFragment.view?.visibility = View.VISIBLE
                                }, 1000L)

                                fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->

                                    val userLocation = LatLng(location.latitude, location.longitude)

                                    userMarker = googleMap.addMarker(
                                        MarkerOptions()
                                            .position(userLocation)
                                            .title("Sua localização atual")
                                            .icon(BitmapHelper.vectorToBitmap(this, R.drawable.user_map_icon, ContextCompat.getColor(this, R.color.main_dark_blue)))
                                    )

                                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 18f))
                                }
                            } else {
                                Toast.makeText(this, "Para acessar é necessario permitir que tenhamos acesso à sua localização", Toast.LENGTH_LONG).show()
                                finish()
                                isTimerRunning = false
                            }
                    }

                    // Inicia um loop para que atualize a localização do usuario
                    startPeriodicUpdate(this, binding, googleMap)
                }
            }

        binding.btnBack.setOnClickListener{
            finish()
            isTimerRunning = false
        }
    }

    /***
     * Faz com que quando executada, para cada estabelecimento da lista de estabelecimentos,
     * adiciona um marcador no mapa e define o title, snippet, position e tag ultilizando
     * value-parameter googleMap do getMapAsync vindo da lista de paramentros
     */
    private fun addMarkers(googleMap: GoogleMap) {
        establishments?.forEach {establishment ->
            val marker = googleMap.addMarker(
                MarkerOptions()
                    .title(establishment.name)
                    .snippet(establishment.address)
                    .position(establishment.latLng)
                    .icon(
                        BitmapHelper.vectorToBitmap(this, R.drawable.marker_icon, ContextCompat.getColor(this, R.color.main_dark_blue))
                    )
                    .alpha(0.8f)
            )
            if (marker != null) {
                marker.tag = establishment
            }
        }
    }

    /***
     * Faz com que quando executada, a cada 1 segundo, verifica se o aplicativo tem acesso a localização do usuario,
     * puxa novamente a localização do usuario e atualiza a latitude e longitude do marcador que representa o usuario
     */
    private fun startPeriodicUpdate(context: ConsultarMapaActivity, binding: ActivityConsultarMapaBinding, googleMap: GoogleMap) {
        var cameraState: Int = 1
        val timerTask = object : TimerTask() {
            override fun run() {
                if (!isTimerRunning) {
                    cancel()
                    return
                }

                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && isLocationEnable()) {

                    fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                        if (userMarker != null) {
                            if (location == null) {
                                if(cameraState != 2) {
                                    val bounds = LatLngBounds.builder()
                                    establishments?.forEach {
                                        bounds.include(it.latLng)
                                    }

                                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 300))
                                    cameraState = 2
                                }
                            } else {
                                val userLocation = LatLng(location.latitude, location.longitude)
                                userMarker?.position = userLocation

                                if(cameraState != 1) {
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 18f))
                                    cameraState = 1
                                }
                            }
                        }
                    }
                } else if (!isLocationEnable()) {
                    fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                        if (userMarker != null) {
                            if (location == null) {
                                if(cameraState != 2) {
                                    val bounds = LatLngBounds.builder()
                                    establishments?.forEach {
                                        bounds.include(it.latLng)
                                    }

                                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 300))
                                    cameraState = 2
                                }
                            } else {
                                val userLocation = LatLng(location.latitude, location.longitude)
                                userMarker?.position = userLocation

                                if(cameraState != 1) {
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 18f))
                                    cameraState = 1
                                }
                            }
                        }
                    }
                }
            }
        }
        timer.schedule(timerTask, 0, 1000L)
    }

    private fun isLocationEnable() = this.getSystemService(LocationManager::class.java)
        .isProviderEnabled(LocationManager.GPS_PROVIDER)
}