package br.com.projetopi.smartlock

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.projetopi.smartlock.databinding.ActivityAddCardBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class AddCardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddCardBinding
    private lateinit var simpleStorage: SimpleStorage

    private lateinit var db: FirebaseFirestore

    private lateinit var card: CreditCard

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        simpleStorage = SimpleStorage(this)

        val user: User = simpleStorage.getUserAccountData()

        db = Firebase.firestore

        binding.btnAdicionar.setOnClickListener{
            if(isValid() && isFulfilled()){
                card = CreditCard(
                    user.uid.toString(),
                    binding.etNumeroCartao.text.toString(),
                    binding.etCVV.text.toString(),
                    binding.etData.text.toString(),
                    binding.etNomeTitular.text.toString(),
                )

                db.collection("cards").add(card)


//                db.collection("cards").add(card)
//                    .addOnSuccessListener {
//                        Snackbar.make(binding.btnAdicionar, "Cartão cadastrado", Snackbar.LENGTH_LONG).show()
//                    }
//                    .addOnFailureListener{
//                        Snackbar.make(binding.btnAdicionar, "Erro ao cadastrar cartão", Snackbar.LENGTH_LONG).show()
//                    }
            }

            else{
                Snackbar.make(binding.btnAdicionar, "Preencha os campos corretamente", Snackbar.LENGTH_LONG).show()
            }
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

    }



    private fun isValid():Boolean{
        return if(binding.etCVV.length() != 3 ||
            binding.etNumeroCartao.length() != 16){
            false
        }else{
            true
        }
    }

    private fun isFulfilled(): Boolean {
        return if(binding.etNomeTitular.text.toString().isEmpty()||
            binding.etNumeroCartao.text.toString().isEmpty()||
            binding.etCVV.text.toString().isEmpty()||
            binding.etData.toString().isEmpty()){
            false
        } else{
            true
        }
    }
}