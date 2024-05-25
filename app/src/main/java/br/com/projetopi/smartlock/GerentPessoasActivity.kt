package com.example.myapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * Activity responsável por exibir o layout onde o gerente vai fazer a contagem de pessoas.
 * É uma pagina de escolha para os usuarios que acessam os armarios. 
 * Chama a prox activity atraves da funcao startFotografarCadActivity() para prosseguir.
 * 
 * @autor Julia
 */


class PessoaCounterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applyWindowInsets()

        binding.btnProsseguir.setOnClickListener {
            startFotografarCadActivity()
        }
    }

    
    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootView)) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(insets.left, insets.top, insets.right, insets.bottom)
            windowInsets
        }
    }

    
    private fun startFotografarCadActivity() {
        val intent = Intent(this, FotografarCadActivity::class.java)
        startActivity(intent)
    }
}

