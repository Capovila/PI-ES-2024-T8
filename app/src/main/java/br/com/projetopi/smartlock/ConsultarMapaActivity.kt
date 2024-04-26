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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class ConsultarMapaActivity : AppCompatActivity() {

    //Lista de lugares que exitem armarios no mapa
    private val establishments: ArrayList<Establishment>? = arrayListOf()

    private lateinit var binding: ActivityConsultarMapaBinding

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConsultarMapaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = com.google.firebase.Firebase.firestore

        //Esconde o linear layout lnlaBtnMenu
        binding.lnlaBtnMenu.visibility = View.GONE

        db.collection("establishments").get().addOnSuccessListener { documents ->
            for (document in documents) {
                val id = document.id
                val name = document.getString("name") ?: ""
                val latitude = document.getDouble("latitude") ?: 0.0
                val longitude = document.getDouble("longitude") ?: 0.0
                val address = document.getString("address") ?: ""
                val reference = document.getString("reference") ?: ""
                val managerName = document.getString("managerName") ?: ""

                val establishment = Establishment(id, name, LatLng(latitude, longitude), address, reference, managerName)

                establishments?.add(establishment)
            }

            //Atribui ao map_fragment o mapa vindo do google cloud
            val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment

            //Executa quando o mapa é carregado
            mapFragment.getMapAsync { googleMap ->

                //Adiciona marcadores no mapa
                addMarkers(googleMap)

                //Define as informacoes do marcador com uma classe externa
                googleMap.setInfoWindowAdapter(MarkerInfoAdapter(this))

                //Define o estilo do mapa com um arquivo JSON da pasta raw
                googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))

                //Desativa os botoes que ficam no canto inferior direito que aparece quando o usuario clica em um marcador
                googleMap.uiSettings.isMapToolbarEnabled = false

                //Executa quando um marcador recebe um click
                googleMap.setOnMarkerClickListener { marker -> //Se o marcador nao for nulo

                    //Atribui em variaveis as coordenadas desse marcador
                    val markerPosition = marker.position
                    val markerLatitude = markerPosition.latitude
                    val markerLongitude = markerPosition.longitude

                    //Mostra o linear layout lnlaBtnMenu
                    binding.lnlaBtnMenu.visibility = View.VISIBLE

                    //Executa quando o btnIr recebe um click
                    binding.btnIr.setOnClickListener {

                        //Abre o Google Maps para calcular a rota ate o marcador referenciado

                        startActivity(Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?q=$markerLatitude,$markerLongitude")
                        )
                        )
                    }

                    binding.btnAlugar.setOnClickListener {
                        Toast.makeText(baseContext, "Você precisa estar logado para alugar um armário", Toast.LENGTH_LONG).show()
                        finish()
                    }
                    // Retorna false para permitir que o Google Maps trate o evento e exiba a janela de informações do marcador
                    false
                }

                //Executa quando uma janela de informacoes de um marcador
                googleMap.setOnInfoWindowCloseListener { //Esconde o linear layout lnlaBtnMenu
                    binding.lnlaBtnMenu.visibility = View.GONE
                }

                //Executa quando o mapa é carregado
                googleMap.setOnMapLoadedCallback{
                    //Define os limites do mapa
                    val bounds = LatLngBounds.builder()
                    establishments?.forEach{
                        bounds.include(it.latLng)
                    }
                    //Move a camera para mostrar o mapa com os limites definidos com um padding das bordas de 300px
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 300))
                }
            }
        }

        binding.btnBack.setOnClickListener{
            finish()
        }
    }

    //Funcao para acidionar marcadores
    private fun addMarkers(googleMap: GoogleMap) {

        //Para cada lugar da lista establishments adiciona um marcador com as opcoes definidas
        establishments?.forEach {establishment ->
            val marker = googleMap.addMarker(
                MarkerOptions()
                    .title(establishment.name)
                    .snippet(establishment.address)
                    .position(establishment.latLng)
                    .icon(
                        BitmapHelper.vectorToBitmap(this, R.drawable.logo, ContextCompat.getColor(this, R.color.white))
                    )
                    .alpha(0.8f)
            )
            if (marker != null) {
                marker.tag = establishment
            }
        }
    }

}