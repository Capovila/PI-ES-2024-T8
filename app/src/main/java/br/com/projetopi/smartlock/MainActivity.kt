package br.com.projetopi.smartlock

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : AppCompatActivity() {

    private lateinit var btnLogout: Button

    private lateinit var auth: FirebaseAuth

    private lateinit var simpleStorage: SimpleStorage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        simpleStorage = SimpleStorage(this)

        auth = Firebase.auth

        btnLogout = findViewById(R.id.btnLogout)

        btnLogout.setOnClickListener{
            simpleStorage.clearUserAccount()
            auth.signOut()
            startActivity(Intent(this,LoginActivity::class.java))
        }


    }
}