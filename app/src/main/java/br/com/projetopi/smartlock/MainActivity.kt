package br.com.projetopi.smartlock

import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
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
        binding = ActivityMainBinding.inflate(layoutInflater)

        val connectivityManager = getSystemService(ConnectivityManager::class.java)
        if(connectivityManager.activeNetwork == null){
            Toast.makeText(this, "Ative a internet para o funcionamento correto do app", Toast.LENGTH_LONG).show()
        }


        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        replaceFragment(Mapa())

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Executa a função startPeriodicUpdate
        startPeriodicUpdate(this, binding)

        binding.bottomNavigationView.menu.findItem(R.id.page_2).isChecked = true



        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.page_1 ->
                    Handler().postDelayed({ replaceFragment(Profile()) }, 100L)
                R.id.page_2 ->
                    Handler().postDelayed({ replaceFragment(Mapa()) }, 100L)
                R.id.page_3 ->
                    Handler().postDelayed({ replaceFragment(Locacoes()) }, 100L)
            }
            true
        }
    }

    /***
     * Faz com que quando executada, troca o fragmento exibido no fragmento
     * que foi passado pela lista de parametros
     */
    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

    /***
     * Faz com que quando executado, realiza uma verificação no Firestore
     * a cada 1 segundo se o usuario possui uma locação aberta, caso tenha,
     * define um badge visivel no item 3 (Locacoes)
     */
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
        timer.schedule(timerTask, 0, 1000L)
    }

    // Override fun usada pela interface FragmentHandler que, quando executada,
    // muda o fragmento exibido nessa activity por outro fragmento desejado
    // de acordo com o comando executado em outro fragmento
    override fun changeFragment(fragment: Fragment) {
        replaceFragment(fragment)
    }
}


