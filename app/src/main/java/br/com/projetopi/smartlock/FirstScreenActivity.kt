package br.com.projetopi.smartlock

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar


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
            val intentLogin = Intent(this, LoginActivity::class.java)
            startActivity(intentLogin)
        }
        btnCadastrar.setOnClickListener{
            val intent = Intent(this, CadastrarActivity::class.java)
            startActivity(intent)
        }

        btnConferir.setOnClickListener{
            val message: String = "Geolocalização para consulta de armários"
            Snackbar.make(btnConferir,message,Snackbar.LENGTH_LONG).show()
        }
    }
}