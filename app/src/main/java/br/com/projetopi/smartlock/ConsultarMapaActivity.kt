package br.com.projetopi.smartlock

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import br.com.projetopi.smartlock.databinding.ActivityConsultarMapaBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar

class ConsultarMapaActivity : AppCompatActivity() {

    //Lista de lugares que exitem armarios no mapa
    private val places = arrayListOf(
        Place("26153", "Ponto1", LatLng(-22.834554,-47.055358), "Av. Profa. Ana Maria Silvestre Adade, 825 - Parque das Universidades",  "Em frente a PUC Campinas") ,
        Place("26151", "Ponto2", LatLng(-22.847644, -47.062139), "Parque Dom Pedro, Jardim Santa Genebra", "Na entrada das águas")
    )

    private lateinit var binding: ActivityConsultarMapaBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        //Esconde a barra com o nome do app que fica no canto superior da tela
        supportActionBar?.hide()

        super.onCreate(savedInstanceState)
        binding = ActivityConsultarMapaBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //Esconde o linear layout lnlaBtnMenu
        binding.lnlaBtnMenu.visibility = View.GONE

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

                    startActivity(Intent(this, LoginActivity::class.java))
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
                places.forEach{
                    bounds.include(it.latLng)
                }
                //Move a camera para mostrar o mapa com os limites definidos com um padding das bordas de 300px
                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 300))
            }
        }

        binding.btnBack.setOnClickListener{
            finish()
        }
    }

    //Funcao para acidionar marcadores
    private fun addMarkers(googleMap: GoogleMap) {

        //Para cada lugar da lista places adiciona um marcador com as opcoes definidas
        places.forEach {place ->
            val marker = googleMap.addMarker(
                MarkerOptions()
                    .title(place.name)
                    .snippet(place.address)
                    .position(place.latLng)
                    .icon(
                        BitmapHelper.vectorToBitmap(this, R.drawable.logo, ContextCompat.getColor(this, R.color.white))
                    )
                    .alpha(0.8f)
            )
            if (marker != null) {
                marker.tag = place
            }
        }
    }

}