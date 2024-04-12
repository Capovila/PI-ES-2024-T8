package br.com.projetopi.smartlock

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.replace
import br.com.projetopi.smartlock.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var auth: FirebaseAuth

    private lateinit var simpleStorage: SimpleStorage

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(Mapa())

        var badge = binding.bottomNavigationView.getOrCreateBadge(R.id.page_3)

        badge.isVisible = true

        binding.bottomNavigationView.menu.findItem(R.id.page_2).isChecked = true

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.page_1 -> replaceFragment(Profile())
                R.id.page_2 -> replaceFragment(Mapa())
                R.id.page_3 -> replaceFragment(Locacoes())
            }
            true
        }

    }
}


