package br.com.projetopi.smartlock.Fragments

import SharedViewModelEstablishment
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.activityViewModels
import br.com.projetopi.smartlock.BitmapHelper
import br.com.projetopi.smartlock.Classes.Establishment
import br.com.projetopi.smartlock.Classes.User
import br.com.projetopi.smartlock.ConsultarMapaActivity
import br.com.projetopi.smartlock.MainActivity
import br.com.projetopi.smartlock.MarkerInfoAdapter
import br.com.projetopi.smartlock.R
import br.com.projetopi.smartlock.SimpleStorage
import br.com.projetopi.smartlock.databinding.ActivityConsultarMapaBinding
import br.com.projetopi.smartlock.databinding.FragmentMapaBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.util.Timer
import java.util.TimerTask

class Mapa() : Fragment() {

    private val establishments: ArrayList<Establishment> = arrayListOf()
    private var _binding: FragmentMapaBinding? = null
    private val binding get() = _binding!!
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var db: FirebaseFirestore
    private lateinit var simpleStorage: SimpleStorage
    private val timer = Timer()
    private var userMarker: Marker? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMapaBinding.inflate(inflater,container,false)

        simpleStorage = SimpleStorage(requireContext())

        val user: User = simpleStorage.getUserAccountData()

        // Esconde o btnMenuFragment
        binding.lnlaBtnMenuFragment.visibility = View.GONE

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

        val sharedViewModelEstablishment: SharedViewModelEstablishment by activityViewModels()

        db = Firebase.firestore

        /***
         * Busca locações que nao foram efetivadas e que estejam relacionadas ao id do usuario, caso alguma
         * seja encontrada, deixa visivel ao usuario assim que o fragment é criado com uma Toast
         */
        db.collection("rentals")
            .whereEqualTo("rentalImplemented", false)
            .whereEqualTo("idUser", user.uid)
            .get()
            .addOnSuccessListener {
                for(documents in it){
                    Toast.makeText(
                        requireContext(),
                        "Existe uma locação para ser efetivada",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        /***
         * Busca todos os estabelecimentos cadastrados e caso seja encontrado algum, cria uma variavel
         * da Classe Establishment e atribui todos os dados vindos do Firestore à essa variavel, após isso
         * adiciona esse estabelecimento à lista de estabelecimentos e faz o mesmo para os outros estabelecimentos
         * caso houver mais algum
         */
        db.collection("establishments")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val id = document.id
                    val name = document.getString("name") ?: ""
                    val latitude = document.getDouble("latitude") ?: 0.0
                    val longitude = document.getDouble("longitude") ?: 0.0
                    val address = document.getString("address") ?: ""
                    val reference = document.getString("description") ?: ""
                    val managerName = document.getString("managerName") ?: ""

                    val establishment = Establishment(
                        id,
                        name,
                        LatLng(latitude, longitude),
                        address,
                        reference,
                        managerName
                    )
                    establishments.add(establishment)
                }

                // Atribui à variavel mapFragment o childFragment map_fragmentMain
                val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragmentMain) as SupportMapFragment

                /***
                 * Quando é obtido o mapa assincrono, adiciona os marcadores,
                 * define as janela de informações dos marcadores, define o estilo do mapa exibido e
                 * define o uiSettings.isMapToolbarEnabled como false
                 */
                mapFragment.getMapAsync{ googleMap ->
                    mapFragment.view?.visibility = View.GONE

                    addMarkers(googleMap)

                    googleMap.setInfoWindowAdapter(MarkerInfoAdapter(requireContext()))

                    googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                        requireContext(),
                        R.raw.map_style
                    ))

                    googleMap.uiSettings.isMapToolbarEnabled = false

                    /***
                     * Quando um marcador é clicado atribui em variaveis algumas informações e
                     * mostra o lnlaBtnMenuFragment
                     */
                    googleMap.setOnMarkerClickListener {marker ->
                        if(marker.title != "Sua localização atual"){

                            val markerPosition = marker.position
                            val markerLatitude = markerPosition.latitude
                            val markerLongitude = markerPosition.longitude
                            val markerLatLng = LatLng(markerLatitude, markerLongitude)

                            binding.lnlaBtnMenuFragment.visibility = View.VISIBLE

                            /***
                             * Quando o btnIrFragment é clicado, direciona o usuario ao google maps
                             * com a latitude e longitude do marcador selecionado para que seja
                             * traçada a rota
                             */
                            binding.btnIrFragment.setOnClickListener{
                                startActivity(Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("http://maps.google.com/maps?q=$markerLatitude,$markerLongitude")
                                )
                                )
                            }

                            /***
                             * Quando o btnAlugarFragment é clicado, busca locações que estão relacionadas
                             * ao id do usuario e que nao foram efetivadas, caso tenha sucesso, mostra
                             * uma Toast com a mensagem de que exite uma locação para ser efetivada, caso
                             * contrario, busca locações que estao relacionadas ao id do usuario e que nao
                             * foram fechadas, caso tenha sucesso, mostra uma Toast com a mensagem que exite
                             * uma locação aberta, caso contrario, busca usuarios que o uid igual ao id do
                             * usuario e que o cartao nao esteja registrado, caso tenha sucesso, mostra uma
                             * Toast com a mensagem de que precisa ter uma cartão cadastrado, caso contrario,
                             * verifica se as permissoes ACCESS_FINE_LOCATION e ACCESS_COARSE_LOCATION estao
                             * PERMISSION_GRANTED, caso nao estejam, executa a função requestPermission, caso
                             * estejam pega a ultima localização do usuario executa a função
                             * calcularDistanciaEmMetros passando na lista de parametros a localização do
                             * usuario e do marcador selecionado, caso estejam mais distantes que 150 metros
                             * mostra uma Toast com a mensagem de que o usuario nao esta proximo do marcador,
                             * caso contrario, passa as informações do estabelecimento pelo
                             * SharedViewModelEstablishment e muda o fragmento exibido na main activity para
                             * o fragmento OpcaoTempo
                             */
                            binding.btnAlugarFragment.setOnClickListener{
                                val establishment = marker.tag as Establishment
                                val idEstablishment = establishment.uid.toString()
                                db.collection("lockers")
                                    .whereEqualTo("idEstablishment", idEstablishment)
                                    .whereEqualTo("isRented", false)
                                    .get()
                                    .addOnSuccessListener {
                                        if (it.isEmpty) {
                                            Toast.makeText(
                                                requireContext(),
                                                "Esse estabelecimento não possui armários disponiveis",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        } else {
                                            db.collection("rentals")
                                                .whereEqualTo("idUser", user.uid)
                                                .whereEqualTo("rentalImplemented", false)
                                                .get()
                                                .addOnSuccessListener {
                                                    if (!it.isEmpty) {
                                                        Toast.makeText(
                                                            requireContext(),
                                                            "Existe uma locação para ser efetivada",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    } else {
                                                        db.collection("rentals")
                                                            .whereEqualTo("idUser", user.uid)
                                                            .whereEqualTo("rentalOpen", true)
                                                            .get()
                                                            .addOnSuccessListener {
                                                                if (!it.isEmpty) {
                                                                    Toast.makeText(
                                                                        requireContext(),
                                                                        "Você ja possui uma locação aberta",
                                                                        Toast.LENGTH_LONG
                                                                    ).show()
                                                                } else {
                                                                    db.collection("users")
                                                                        .whereEqualTo(
                                                                            "uid",
                                                                            user.uid
                                                                        )
                                                                        .whereEqualTo(
                                                                            "cardRegistred",
                                                                            false
                                                                        )
                                                                        .get()
                                                                        .addOnSuccessListener {
                                                                            if (!it.isEmpty) {
                                                                                Toast.makeText(
                                                                                    requireContext(),
                                                                                    "Você precisa ter um cartão cadastrado",
                                                                                    Toast.LENGTH_LONG
                                                                                ).show()
                                                                            } else {
                                                                                if (ActivityCompat.checkSelfPermission(
                                                                                        requireContext(),
                                                                                        Manifest.permission.ACCESS_FINE_LOCATION
                                                                                    ) == PackageManager.PERMISSION_GRANTED &&
                                                                                    ActivityCompat.checkSelfPermission(
                                                                                        requireContext(),
                                                                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                                                                    ) == PackageManager.PERMISSION_GRANTED
                                                                                ) {
                                                                                    fusedLocationProviderClient.lastLocation
                                                                                        .addOnCompleteListener { task ->
                                                                                            if (task.isSuccessful) {
                                                                                                val location: Location? =
                                                                                                    task.result
                                                                                                if (location != null) {
                                                                                                    val userLatLng =
                                                                                                        LatLng(
                                                                                                            location.latitude,
                                                                                                            location.longitude
                                                                                                        )
                                                                                                    val distanciaDoUsuario =
                                                                                                        calcularDistanciaEmMetros(
                                                                                                            userLatLng,
                                                                                                            markerLatLng
                                                                                                        )
                                                                                                    if (distanciaDoUsuario < 150.0) {
                                                                                                        val markerEstablishment: Establishment =
                                                                                                            marker.tag as Establishment
                                                                                                        sharedViewModelEstablishment.selectEstablishment(
                                                                                                            markerEstablishment
                                                                                                        )
                                                                                                        (activity as MainActivity).changeFragment(
                                                                                                            OpcaoTempo()
                                                                                                        )
                                                                                                    } else {
                                                                                                        Toast.makeText(
                                                                                                            requireContext(),
                                                                                                            "Você está distante do armário",
                                                                                                            Toast.LENGTH_LONG
                                                                                                        )
                                                                                                            .show()
                                                                                                    }
                                                                                                } else {
                                                                                                    Toast.makeText(
                                                                                                        requireContext(),
                                                                                                        "Habilite sua localização",
                                                                                                        Toast.LENGTH_LONG
                                                                                                    )
                                                                                                        .show()
                                                                                                }
                                                                                            } else {
                                                                                                Toast.makeText(
                                                                                                    requireContext(),
                                                                                                    "Erro ao obter a localização",
                                                                                                    Toast.LENGTH_LONG
                                                                                                )
                                                                                                    .show()
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
                                    }
                            }
                        }
                        false
                    }

                    // Quando a janela de informações do marcador é fechada, esconde o lnlaBtnMenuFragment
                    googleMap.setOnInfoWindowCloseListener {
                        binding.lnlaBtnMenuFragment.visibility = View.GONE
                    }

                    /***
                     * Quando o mapa é carregado, verifica se o aplicativo tem acesso a localização do usuario, pega a
                     * latitude e longitude do usuario e adiciona um marcador com a localização do usuario, centralizando
                     * a camera do mapa no usuario, se o aplicativo não tiver acesso a localização do usuario, não é
                     * carregado o mapa
                     */
                    googleMap.setOnMapLoadedCallback{
                        if (ActivityCompat.checkSelfPermission(
                                requireContext(),
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(
                                requireContext(),
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED) {

                            if (isGPSEnabled()) {

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
                                            .icon(BitmapHelper.vectorToBitmap(requireContext(), R.drawable.user_map_icon, ContextCompat.getColor(requireContext(), R.color.main_dark_blue)))
                                    )

                                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 18f))
                                }
                            } else {
                                Toast.makeText(requireContext(), "Para acessar é necessario que seu GPS esteja ligado", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(requireContext(), "Para acessar é necessario permitir que tenhamos acesso à sua localização", Toast.LENGTH_LONG).show()
                        }
                    }

                    // Inicia um loop para que atualize a localização do usuario
                    startPeriodicUpdate(this, binding)
                }
        }
        return binding.root
    }


    /***
     * Faz com que quando executada, para cada estabelecimento da lista de estabelecimentos,
     * adiciona um marcador no mapa e define o title, snippet, position e tag ultilizando
     * value-parameter googleMap do getMapAsync vindo da lista de paramentros
     */
    private fun addMarkers(googleMap: GoogleMap) {
        establishments
            .forEach {establishment ->
                val marker = googleMap.addMarker(
                    MarkerOptions()
                        .title(establishment.name)
                        .snippet(establishment.address)
                        .position(establishment.latLng)
                        .icon(
                            BitmapHelper.vectorToBitmap(requireContext(), R.drawable.marker_icon, ContextCompat.getColor(requireContext(), R.color.main_dark_blue))
                        )
                        .alpha(0.8f)
                )
                if (marker != null) {
                    marker.tag = establishment
                }
            }
    }

    /***
     * Faz com que quando executada, usa a localização do usuario e a localização do marcador vindos
     * da lista de parametros e calcula a distancia entre eles, retornando a distancia em metros (Float)
     */
    private fun calcularDistanciaEmMetros(userLocation: LatLng, destinationLocation: LatLng): Float {
        val result = FloatArray(1)
        Location.distanceBetween(
            userLocation.latitude,
            userLocation.longitude,
            destinationLocation.latitude,
            destinationLocation.longitude,
            result
        )
        return result[0]
    }

    /***
     * Faz com que quando executada, a cada 1 segundo, verifica se o aplicativo tem acesso a localização do usuario,
     * puxa novamente a localização do usuario e atualiza a latitude e longitude do marcador que representa o usuario
     */
    private fun startPeriodicUpdate(context: Mapa, binding: FragmentMapaBinding) {
        val timerTask = object : TimerTask() {
            override fun run() {
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                        if (userMarker != null) {
                            val userLocation = LatLng(location.latitude, location.longitude)
                            userMarker?.position = userLocation
                        }
                    }
                }
            }
        }
        timer.schedule(timerTask, 0, 1000L)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Cancela o fragment para que ele nao continue rodando com outro fragment carregado na main
        timer.cancel()
    }

    private fun isGPSEnabled(): Boolean {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
}