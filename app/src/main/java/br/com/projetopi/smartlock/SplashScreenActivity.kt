package br.com.projetopi.smartlock

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        user = Firebase.auth

        //Handler lib faz o fade out do splasher
        Handler(Looper.getMainLooper()).postDelayed({
            val validate = user.currentUser?.isEmailVerified

            if(auth.currentUser == null || validate == false){
                startActivity(Intent(this, FirstScreenActivity::class.java))
            }
            else{
                startActivity(Intent(this, MainActivity::class.java))
            }

            finish()
        }, 2000)
    }
}