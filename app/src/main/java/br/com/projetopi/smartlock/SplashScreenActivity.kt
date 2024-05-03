package br.com.projetopi.smartlock

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var simpleStorage: SimpleStorage

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash_screen)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        simpleStorage = SimpleStorage(this)

        // Atribui à variavel user os dados do usuario guardados no simpleStorage
        val user = simpleStorage.getUserAccountData()

        // Executa com um atraso de 2 segundos
        Handler().postDelayed({

            /***
             * Verifica se possui um id de usuario no simpleStorage, caso tenha,
             * inicia direto a MainActivity e fecha a activity atual, caso nao tenha
             * inicia o LoginActivity e fecha a activity atual
             */
            if(user.uid != null) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }else{
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }, 2000)
    }
}