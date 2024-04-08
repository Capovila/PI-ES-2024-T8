package br.com.projetopi.smartlock

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class RecuperarSenhaActivity : AppCompatActivity() {

    private lateinit var btnRecuperar: Button
    private lateinit var btnVoltar: Button
    private lateinit var tvEmailRecuperar: TextView
    private lateinit var etEmailRecuperar: EditText

    private lateinit var auth: FirebaseAuth

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
        btnVoltar = findViewById(R.id.btnVoltarRecSenha)

        tvEmailRecuperar = findViewById(R.id.tvTextoEmailRecuperar)
        etEmailRecuperar = findViewById(R.id.etEmailRecuperar)

        auth = Firebase.auth

        btnRecuperar.setOnClickListener{
            validarCampo(tvEmailRecuperar,etEmailRecuperar)
            if(etEmailRecuperar.text.toString().isEmpty()){
                Snackbar.make(btnRecuperar, "Insira o e-mail de acesso", Snackbar.LENGTH_LONG).show()


            }else{
                auth.sendPasswordResetEmail(etEmailRecuperar.text.toString()).addOnCompleteListener{
                    if(it.isSuccessful){
                        Snackbar.make(btnRecuperar, "E-mail enviado", Snackbar.LENGTH_LONG).show()
                    }else{
                        Snackbar.make(btnRecuperar, "E-mail inválido", Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }

        btnVoltar.setOnClickListener{
            startActivity(Intent(this, FirstScreenActivity::class.java))
        }
    }
}