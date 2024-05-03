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

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Executa a função startPeriodicUpdate
        startPeriodicUpdate(this, binding)

        // Mostra no fragment o fragmento Mapa
        replaceFragment(Mapa())

        // Define que o icone do mapa do bottom menu esteja como isChecked
        binding.bottomNavigationView.menu.findItem(R.id.page_2).isChecked = true

        /***
         * Quando um item do bottom menu é selecionado, é trocado o fragmento
         * exibido pelo fragmento correspondente ao item do bottom menu
         */
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.page_1 -> replaceFragment(Profile())
                R.id.page_2 -> replaceFragment(Mapa())
                R.id.page_3 -> replaceFragment(Locacoes())
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
        val intervalo = 1000L
        timer.schedule(timerTask, 0, intervalo)
    }

    // Override fun usada pela interface FragmentHandler que, quando executada,
    // muda o fragmento exibido nessa activity por outro fragmento desejado
    // de acordo com o comando executado em outro fragmento
    override fun changeFragment(fragment: Fragment) {
        replaceFragment(fragment)
    }
}


