package com.example.myapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp.databinding.ActivityFotografarCadBinding

/**
 * Activity responsável por exibir o layout onde o gerente vai apenas ler as instrucoes e prosseguir para a captura da foto do usuario.
 * Chama a prox activity atraves da funcao navigateToUserPhotoActivity() para fotografar.
 * 
 * @autor Julia
 */

class FotografarCadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFotografarCadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFotografarCadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnFotografar.setOnClickListener {
            navigateToUserPhotoActivity()
        }
    }

    
    private fun navigateToUserPhotoActivity() {
        val intent = Intent(this, UserPhotoActivity::class.java)
        startActivity(intent)
    }
}
