package br.com.projetopi.smartlock

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.projetopi.smartlock.Classes.User
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

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        isGranted: Boolean ->
        if(isGranted) {
            showCamera()
        }
    }

    private val scanLauncher = registerForActivityResult(ScanContract()) {
        result: ScanIntentResult ->
        run {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelado", Toast.LENGTH_LONG).show()
            } else {
                setResult(result.contents)
                if (qrCodeResult != null) {
                    db.collection("rentals")
                        .whereEqualTo("idUser" , qrCodeResult)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            if (querySnapshot.isEmpty) {
                                Toast.makeText(this, "Locação não encontrada", Toast.LENGTH_LONG).show()
                            } else {
                                for (document in querySnapshot.documents) {
                                    val isRentalImplemented = document.getBoolean("rentalImplemented")
                                    if (isRentalImplemented != null && isRentalImplemented) {
                                        Toast.makeText(this, "Locação já efetivada", Toast.LENGTH_LONG).show()
                                    } else {
                                        proximoPasso()
                                    }
                                }
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

        simpleStorage = SimpleStorage(this)

        val user: User = simpleStorage.getUserAccountData()

        binding.tvManagerName.setText(user.name)
        binding.tvManagerEmail.setText(user.email)

        binding.btnLiberar.setOnClickListener{
            checkPermissionCamera(this)
        }

        binding.btnSair.setOnClickListener{
            simpleStorage.clearUserAccount()
            auth.signOut()
            startActivity(Intent(this, SplashScreenActivity::class.java))
        }
    }

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

    private fun showCamera() {
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt("Escaneie o QR Code")
        options.setCameraId(1)
        options.setBeepEnabled(false)
        options.setBarcodeImageEnabled(true)
        options.setOrientationLocked(false)

        scanLauncher.launch(options)
    }

    private fun setResult(string: String) {
        qrCodeResult = string
    }

    private fun proximoPasso() {
        startActivity(Intent(this, QuantasPessoasActivity::class.java))
    }

}