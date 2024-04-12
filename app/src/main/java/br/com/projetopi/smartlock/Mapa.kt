package br.com.projetopi.smartlock

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar

//Código mínimo para uma fragment usual 
class Mapa : Fragment() {
    private lateinit var textMapa: TextView
    private lateinit var btnSnackBar: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_mapa, container, false)
        textMapa = root.findViewById(R.id.textMapa)
        textMapa.text = "Mapa"
        btnSnackBar = root.findViewById(R.id.btnSnackBar)
        btnSnackBar.setOnClickListener {
            Snackbar.make(btnSnackBar, "botao apertado", Snackbar.LENGTH_LONG).show()
        }

        return root
    }
}