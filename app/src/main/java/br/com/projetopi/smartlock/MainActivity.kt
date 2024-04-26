package br.com.projetopi.smartlock

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import br.com.projetopi.smartlock.Classes.User
import br.com.projetopi.smartlock.Fragments.Locacoes
import br.com.projetopi.smartlock.Fragments.Mapa
import br.com.projetopi.smartlock.Fragments.Profile
import br.com.projetopi.smartlock.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.util.Timer
import java.util.TimerTask

class MainActivity : AppCompatActivity(), FragmentHandler {

    private lateinit var binding: ActivityMainBinding

    private lateinit var simpleStorage: SimpleStorage
    private lateinit var db: FirebaseFirestore

    private val timer = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        startPeriodicUpdate(this, binding)

        replaceFragment(Mapa())

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

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

    private fun startPeriodicUpdate(context: MainActivity, binding: ActivityMainBinding) {
        val timerTask = object : TimerTask() {
            override fun run() {
                simpleStorage = SimpleStorage(context)

                val user: User = simpleStorage.getUserAccountData()

                db = Firebase.firestore

                val badge = binding.bottomNavigationView.getOrCreateBadge(R.id.page_3)

                db.collection("rentals")
                    .whereEqualTo("idUser", user.uid)
                    .whereEqualTo("rentalOpen", true)
                    .get()
                    .addOnSuccessListener {
                        badge.isVisible = !it.isEmpty
                    }
            }
        }
        val intervalo = 1000L
        timer.schedule(timerTask, 0, intervalo)
    }

    override fun changeFragment(fragment: Fragment) {
        replaceFragment(fragment)
    }
}


