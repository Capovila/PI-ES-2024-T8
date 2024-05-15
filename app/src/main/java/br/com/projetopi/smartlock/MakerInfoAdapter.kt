package br.com.projetopi.smartlock

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import br.com.projetopi.smartlock.Classes.Establishment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

// Esta classe implementa a interface GoogleMap.InfoWindowAdapter para personalizar a exibição das informações dos marcadores no mapa
class MarkerInfoAdapter(private val context: Context): GoogleMap.InfoWindowAdapter {

    // Este método é chamado quando o conteúdo da janela de informações do marcador é fornecido como uma View
    override fun getInfoWindow(marker: Marker): View? = null

    // Este método é chamado quando o conteúdo da janela de informações do marcador é fornecido como uma View personalizada
    override fun getInfoContents(marker: Marker): View? {

        // Obtém o objeto Establishment associado ao marcador, se houver. Se não houver, retorna null
        val establishment = marker.tag as? Establishment ?: return null

        // Infla o layout personalizado para a janela de informações do marcador
        val view = LayoutInflater.from(context).inflate(R.layout.custom_marker_info, null)

        // Define o nome, endereço e referência do estabelecimento nos TextViews correspondentes
        view.findViewById<TextView>(R.id.txt_tittle).text = establishment.name
        view.findViewById<TextView>(R.id.txt_address).text = establishment.address
        view.findViewById<TextView>(R.id.txt_reference).text = establishment.reference

        return view // Retorna a View personalizada para exibir as informações do marcador
    }
}