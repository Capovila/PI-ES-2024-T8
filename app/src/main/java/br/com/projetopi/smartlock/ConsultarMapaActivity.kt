package br.com.projetopi.smartlock

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.projetopi.smartlock.Classes.Establishment
import br.com.projetopi.smartlock.databinding.ActivityConsultarMapaBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class ConsultarMapaActivity : AppCompatActivity() {

    private val establishments: ArrayList<Establishment>? = arrayListOf()
    private lateinit var binding: ActivityConsultarMapaBinding
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

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

                addMarkers(googleMap)

                googleMap.setInfoWindowAdapter(MarkerInfoAdapter(this))

                googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))

                googleMap.uiSettings.isMapToolbarEnabled = false

                /***
                 * Quando um marcador é clicado atribui em variaveis algumas informações e
                 * mostra o lnlaBtnMenuFragment
                 */
                googleMap.setOnMarkerClickListener { marker ->

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
                    }
                    false
                }

                // Quando a janela de informações do marcador é fechada, esconde o lnlaBtnMenuFragment
                googleMap.setOnInfoWindowCloseListener {
                    binding.lnlaBtnMenu.visibility = View.GONE
                }

                /***
                 * Quando o mapa é carregado, pega a latitude e longitude de cada estabelecimento e
                 * constroi os limites da area de todos os marcadores, em seguida, move a camera do
                 * mapa para se adequar ao limites definidos anteriormente com um padding das bordas de 300px
                 */
                googleMap.setOnMapLoadedCallback{
                    val bounds = LatLngBounds.builder()
                    establishments?.forEach{
                        bounds.include(it.latLng)
                    }
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 300))
                }
            }
        }

        binding.btnBack.setOnClickListener{
            finish()
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
                        BitmapHelper.vectorToBitmap(this, R.drawable.marker_icon, ContextCompat.getColor(this, R.color.red))
                    )
                    .alpha(0.8f)
            )
            if (marker != null) {
                marker.tag = establishment
            }
        }
    }
}