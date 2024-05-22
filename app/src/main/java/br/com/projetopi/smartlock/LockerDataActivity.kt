package br.com.projetopi.smartlock

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.projetopi.smartlock.Classes.User
import br.com.projetopi.smartlock.databinding.ActivityLockerDataBinding
import br.com.projetopi.smartlock.databinding.ActivityUserPhotoBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class LockerDataActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLockerDataBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var simpleStorage: SimpleStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLockerDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = Firebase.firestore
        simpleStorage = SimpleStorage(this)
        val user: User = simpleStorage.getUserAccountData()

        var placeId: String = ""
        db.collection("rentals")
            .whereEqualTo("managerId", user.uid.toString())
            .get()
            .addOnSuccessListener {
                for(document in it){
                    placeId = document.getString("idPlace").toString()
                }

                db.collection("lockers")
                    .whereEqualTo("idEstablishment", placeId)
                    .get()
                    .addOnSuccessListener {
                        var nLocker: Int? = null
                        for(document in it){
                            nLocker = document.getDouble("nLocker")?.toInt()
                        }

                        binding.tvLockerInfo.setText("Armário ${nLocker.toString()}")
                    }
            }







        binding.btnVoltar.setOnClickListener{
            startActivity(Intent(this, ManagerMainActivity::class.java))
            finish()
        }


    }
}