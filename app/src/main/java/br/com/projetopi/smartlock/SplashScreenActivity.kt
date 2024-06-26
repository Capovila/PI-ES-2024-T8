package br.com.projetopi.smartlock

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.projetopi.smartlock.Classes.User
import br.com.projetopi.smartlock.ManagerActivities.ManagerMainActivity
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var simpleStorage: SimpleStorage
    private lateinit var db: FirebaseFirestore
    private lateinit var user: User


    // Caso o app não abra no celular, zere os dados e o cache, vá em config -> aplicativos -> SmartLock -> Armazenamento
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash_screen)

        db = Firebase.firestore

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        simpleStorage = SimpleStorage(this)

        // Atribui à variavel user os dados do usuario guardados no simpleStorage
        user = simpleStorage.getUserAccountData()

        val permissionGranted = isPermissionGranted()

        if(permissionGranted){
            continueExecution(user)
        } else {
            requestPermission()
        }
    }

    private fun continueExecution(user: User) {
        // Executa a animação do icone
        val animatedIcon = findViewById<ImageView>(R.id.lockerSplash)
        val avd = animatedIcon.drawable as AnimatedVectorDrawable
        avd.start()

        // Executa com um atraso de 1.5 segundos
        Handler(Looper.myLooper()!!).postDelayed({
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
        }, 1000)
    }

    /***
     * Faz com que quando executada, faz um request das permições de ACCESS_COARSE_LOCATION,
     * ACCESS_FINE_LOCATION e ACCESS_NETWORK_STATE
     */
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this as Activity, arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_NETWORK_STATE
            ),
            100
        )
    }

    /***
     * Faz com que quando executada, verifica se o usuario permitiu que o aplicativo
     * use sua localização e o estado de sua internet
     */
    private fun isPermissionGranted() : Boolean {
        return (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)
                == PackageManager.PERMISSION_GRANTED)
                || shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
                || shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
                || shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_NETWORK_STATE)
    }

    /***
     * Faz com que quando o request permission volta um resultado, dependendo do resultado,
     * executa o metodo continueExecution ou o mesmo com um toast bar avisando que algumas
     * funções do app podem nao funcionar
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                continueExecution(user)
            } else {
                continueExecution(user)
                Toast.makeText(this, "Algumas permissões necessárias não foram concedidas", Toast.LENGTH_SHORT).show()
                Toast.makeText(this, "Algumas funções do aplicativo podem não funcionar", Toast.LENGTH_LONG).show()
            }
        }
    }
}
