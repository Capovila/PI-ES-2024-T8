package br.com.projetopi.smartlock.ManagerActivities

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.projetopi.smartlock.Classes.User
import br.com.projetopi.smartlock.R
import br.com.projetopi.smartlock.SimpleStorage
import br.com.projetopi.smartlock.SplashScreenActivity
import br.com.projetopi.smartlock.databinding.ActivityManagerMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions


class ManagerMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManagerMainBinding
    private lateinit var simpleStorage: SimpleStorage
    private lateinit var qrCodeResult: String
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var isRentalImplemented: Boolean? = null

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        isGranted: Boolean ->
        if(isGranted) {
            showCamera()
        }
    }

    /*val do resultado do scan do qrCode. Nesse qrCode é passado o id da locação, valida se a locação existe
    * e se a locação já não esta implementada*/
    private val scanLauncher = registerForActivityResult(ScanContract()) {
        result: ScanIntentResult ->
        run {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelado", Toast.LENGTH_LONG).show()
            } else {
                setResult(result.contents)
                if (qrCodeResult != null) {
                    db.collection("rentals")
                        .document(qrCodeResult)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            isRentalImplemented = querySnapshot.getBoolean("rentalImplemented")
                            if (isRentalImplemented != null && isRentalImplemented == true) {
                                Toast.makeText(this, "Locação já efetivada", Toast.LENGTH_LONG).show()
                            } else if(isRentalImplemented == null){
                                Toast.makeText(this, "Locação inexistente", Toast.LENGTH_LONG).show()
                            }else{
                                val intent: Intent = Intent(this, NumberUsersActivity::class.java)
                                //método para passar informação entre activities
                                intent.putExtra("qrCode", qrCodeResult)
                                startActivity(intent)
                            }

                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Erro ao buscar locação", Toast.LENGTH_LONG).show()
                        }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManagerMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = Firebase.firestore
        auth = Firebase.auth
        val connectivityManager = getSystemService(ConnectivityManager::class.java)


        simpleStorage = SimpleStorage(this)

        val user: User = simpleStorage.getUserAccountData()

        binding.tvManagerName.setText(user.name)
        binding.tvManagerEmail.setText(user.email)

        binding.btnLiberar.setOnClickListener{
            if(connectivityManager.activeNetwork == null){
                Toast.makeText(this, "Ative a internet para escanear o QrCode", Toast.LENGTH_LONG).show()
            }else{
                checkPermissionCamera(this)
            }
        }
        binding.btnAcessar.setOnClickListener{
           val intent = Intent(this, AccessLockerActivity::class.java)
           startActivity(intent)

        }

        binding.btnSair.setOnClickListener{
            simpleStorage.clearUserAccount()
            auth.signOut()
            startActivity(Intent(this, SplashScreenActivity::class.java))
            finish()
        }
    }

    /*função de permissão para acesso de câmera, caso não tenha pedido permissão, será pedida, caso permissão negada
    * indica que é necessário habilitar a câmera, e caso tudo esteja certo, inicializa a camera de scanner*/
    private fun checkPermissionCamera(context: Context) {
        if(ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            showCamera()
        } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
            Toast.makeText(context, "É necessario que você permita nosso acesso a sua camera", Toast.LENGTH_LONG).show()
        }
        else {
            requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }


    /*Inicializa a câmera de scan, escolhendo a camera, sentido da camera, etc.*/
    private fun  showCamera() {
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt("Escaneie o QR Code")
        options.setCameraId(0)
        options.setBeepEnabled(false)
        options.setBarcodeImageEnabled(true)
        options.setOrientationLocked(false)

        scanLauncher.launch(options)
    }

    private fun setResult(string: String) {
        qrCodeResult = string
    }


}