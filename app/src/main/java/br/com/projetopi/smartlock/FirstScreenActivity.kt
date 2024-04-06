package br.com.projetopi.smartlock

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth


//https://m2.material.io/
class FirstScreenActivity : AppCompatActivity() {

    private lateinit var btnCadastrar: Button
    private lateinit var btnLogar: Button
    private lateinit var btnConferir: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnCadastrar = findViewById(R.id.btnCadastrar)
        btnLogar = findViewById(R.id.btnLogar)
        btnConferir = findViewById(R.id.btnConferir)



        btnLogar.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
        }
        btnCadastrar.setOnClickListener{
            startActivity(Intent(this, CadastrarActivity::class.java))
        }

        btnConferir.setOnClickListener{
            Snackbar.make(btnConferir,"Geolocalização para consulta de armários",Snackbar.LENGTH_LONG).show()
        }
    }
}