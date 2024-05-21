package br.com.projetopi.smartlock

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.projetopi.smartlock.Classes.User
import br.com.projetopi.smartlock.databinding.ActivityNumberUsersBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class NumberUsersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNumberUsersBinding
    private lateinit var simpleStorage: SimpleStorage
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNumberUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        simpleStorage = SimpleStorage(this)
        db = Firebase.firestore
        val user: User = simpleStorage.getUserAccountData()

        binding.tvManagerName.setText(user.name.toString())
        binding.tvManagerEmail.setText(user.email.toString())

        val userId = intent.getStringExtra("UserID")

        binding.btnBack.setOnClickListener{
            finish()
        }

        binding.btnConfirmar.setOnClickListener{
            if(!binding.op1.isChecked && !binding.op2.isChecked){
                Toast.makeText(this, "Escolha uma opção", Toast.LENGTH_LONG).show()
            }else{
                db.collection("rentals")
                    .whereEqualTo("idUser", userId)
                    .get()
                    .addOnSuccessListener {
                        val numberUsers = hashMapOf(
                            if(binding.op1.isChecked){
                                "usersQuantity" to 1
                            }else{
                                "usersQuantity" to 2
                            }
                        )
                        for (document in it){
                            document.reference.update(numberUsers as Map<String, Any>)
                        }
                    }

                cameraProviderResult.launch(android.Manifest.permission.CAMERA)
            }
        }
    }

    private val cameraProviderResult=
        registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if(it){
                val intent = Intent(this, UserPhotoActivity::class.java)
                intent.putExtra("nUser", "1")
                startActivity(intent)
            }
            else{
                Toast.makeText(this, "habilite a camera para tirar fotos", Toast.LENGTH_LONG).show()
            }
        }
}