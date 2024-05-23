package br.com.projetopi.smartlock

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CadastrarCartaoActivity : AppCompatActivity() {

    private lateinit var tvNumeroCartao: EditText
    private lateinit var etDataVencimento: EditText
    private lateinit var tvCVV: EditText
    private lateinit var btnCadastroCartao: Button
    private lateinit var etNomeTitular: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastrar_cartao)

        tvNumeroCartao = findViewById(R.id.tvNumeroCartao)
        etDataVencimento = findViewById(R.id.etDataVencimento)
        tvCVV = findViewById(R.id.tvCVV)
        btnCadastroCartao = findViewById(R.id.btnCadastroCartao)
        etNomeTitular = findViewById(R.id.etNomeTitular)

        btnCadastroCartao.setOnClickListener {
            registerCard()
        }
    }

    private fun registerCard() {
        val NumeroCartao = tvNumeroCartao.text.toString().trim()
        val NomeTitular = etNomeTitular.text.toString().trim()
        val DataVencimento = etDataVencimento.text.toString().trim()
        val CVV = tvCVV.text.toString().trim()

        if (NumeroCartao.isEmpty() || NomeTitular.isEmpty() || DataVencimento.isEmpty() || CVV.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Lógica de cadastro do cartão (ex: enviar dados para um servidor ou salvar localmente)
        Toast.makeText(this, "Cartão cadastrado com sucesso", Toast.LENGTH_SHORT).show()
    }
}
