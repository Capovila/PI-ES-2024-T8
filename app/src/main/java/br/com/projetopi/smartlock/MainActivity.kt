package br.com.projetopi.smartlock

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import br.com.projetopi.smartlock.Fragments.Locacoes
import br.com.projetopi.smartlock.Fragments.Mapa
import br.com.projetopi.smartlock.Fragments.Profile
import br.com.projetopi.smartlock.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity(), FragmentHandler {

    private lateinit var binding: ActivityMainBinding

    private lateinit var db: FirebaseFirestore

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

        db = Firebase.firestore

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

    override fun changeFragment(fragment: Fragment) {
        replaceFragment(fragment)
    }
}


