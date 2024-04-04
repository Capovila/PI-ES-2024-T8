package br.com.projetopi.smartlock

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar

class RecuperarSenhaActivity : AppCompatActivity() {

    private lateinit var btnRecuperar: Button
    private lateinit var tvEmailRecuperar: TextView
    private lateinit var etEmailRecuperar: EditText

    private fun validarCampo(texto: TextView, campo: EditText){
        if(campo.text.toString().isEmpty()){
            texto.setTextColor(getColor(R.color.red))
        }else{
            texto.setTextColor(getColor(R.color.black))
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recuperar_senha)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnRecuperar = findViewById(R.id.btnRecuperar)
        tvEmailRecuperar = findViewById(R.id.tvTextoEmailRecuperar)
        etEmailRecuperar = findViewById(R.id.etEmailRecuperar)

        btnRecuperar.setOnClickListener{
            if(etEmailRecuperar.text.toString().isEmpty()){

                Snackbar.make(btnRecuperar, "Insira o e-mail de acesso", Snackbar.LENGTH_LONG).show()
                validarCampo(tvEmailRecuperar,etEmailRecuperar)

            }else{
                Snackbar.make(btnRecuperar, "Recuperar pelo e-mail", Snackbar.LENGTH_LONG).show()
        }   }
    }
}