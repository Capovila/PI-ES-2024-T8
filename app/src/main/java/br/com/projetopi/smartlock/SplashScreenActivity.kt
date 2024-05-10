package br.com.projetopi.smartlock

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.postDelayed
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var simpleStorage: SimpleStorage
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash_screen)

        db = Firebase.firestore

        // Executa a animação do icone
        val animatedIcon = findViewById<ImageView>(R.id.lockerSplash)
        val avd = animatedIcon.drawable as AnimatedVectorDrawable
        avd.start()

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
             * Verifica se possui um id de usuario no simpleStorage, caso tenha e
             * caso o usuario seja gerente, inicia direto a ManagerMainActivity e fecha
             * a activity atual, caso o usuario nao seja um gerente,
             * inicia direto a MainActivity e fecha a activity atual, caso nao tenha
             * um id de usuario no simpleStorage
             * inicia o LoginActivity e fecha a activity atual
             */
            db.collection("users")
                .whereEqualTo("uid",user.uid)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val isManager = document.getBoolean("manager")

                        if(user.uid != null && isManager == true){
                            startActivity(Intent(this, ManagerMainActivity::class.java))
                            finish()
                        }
                        if(user.uid != null && isManager == false) {
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                    }
                }

            if(user.uid == null){
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }, 1500)
    }
}