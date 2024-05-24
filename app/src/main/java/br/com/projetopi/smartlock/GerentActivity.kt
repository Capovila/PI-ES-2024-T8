package br.edu.puccampinas.testepi

import android.os.Bundle
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import br.edu.puccampinas.testepi.databinding.ActivityGerentBinding

/**
 * Tela de exemplo que é chamada quando o user.manager == true e abre as duas condições para ele.
 * Função que chama outra Activity para o próximo passo: Abrir Camera e Ler QRCode
 *  @author Júlia
 *  */


class GerentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGerentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGerentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val btnLiberarLocacao = binding.btnLiberarLoc
        val btnAcessoArmario = binding.btnAcessoArmario

        btnLiberarLocacao.setOnClickListener {
            Toast.makeText(this, "Liberar Locação clicado", Toast.LENGTH_SHORT).show()
        }

        btnAcessoArmario.setOnClickListener {
            Toast.makeText(this, "Acesso ao Armário clicado", Toast.LENGTH_SHORT).show()
        }

        // para chamar a ManagerMainActivity quando o gerente clicar no botão Liberar Locação
        startActivity(Intent(this, ManagerMainActivity::class.java))
        }
    }
