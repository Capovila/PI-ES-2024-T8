package br.com.projetopi.smartlock.Fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.projetopi.smartlock.Classes.User
import br.com.projetopi.smartlock.LoginActivity
import br.com.projetopi.smartlock.MainActivity
import br.com.projetopi.smartlock.SimpleStorage
import br.com.projetopi.smartlock.SplashScreenActivity
import br.com.projetopi.smartlock.databinding.FragmentProfileBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class Profile : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var simpleStorage: SimpleStorage
    private lateinit var db: FirebaseFirestore

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        simpleStorage = SimpleStorage(requireContext())

        val user: User = simpleStorage.getUserAccountData()

        auth = Firebase.auth
        db = Firebase.firestore

        // Esconde o cvCard
        binding.cvCard.visibility = View.GONE

        binding.tvUserEmail.text = user.email
        binding.tvUserName.text = user.name

        /***
         * Busca cartoes que estao relacionados ao id do usuario, caso encontrado,
         * define os textView's com as informações vindas do Firestore, esconde o
         * btnAddCard, o tvGetCard e mostra o cvCard
         */
        db.collection("cards")
            .whereEqualTo("userId", user.uid)
            .get()
            .addOnSuccessListener {
                for(documents in it){
                    val cardName = documents.getString("cardName")
                    binding.tvCardName.text = "Titular: $cardName"
                    val cardNumber = documents.getString("cardNumber")
                    if (cardNumber != null && cardNumber.length >= 15) {
                        binding.tvCardNumber.text = "Final: ${cardNumber.substring(11, 15)}"
                    } else {
                        binding.tvCardNumber.text = "Final: Invalid Card Number"
                    }
                    val cardDate = documents.getString("expireDate")
                    binding.tvCardDate.text = "Validade: $cardDate"

                    binding.btnAddCard.visibility = View.GONE
                    binding.tvGetCard.visibility = View.GONE
                    binding.cvCard.visibility = View.VISIBLE
                }

                binding.tvUserEmail.text = user.email
                binding.tvUserName.text = user.name
        }

        /***
         * Quando o btnDelete é clicado, busca cartoes que estejam relacionados
         * ao id do usuario e deleta esse cartao do Firestore, mostra o btnAddCard,
         * o tvGetCard e esconde o cvCard, removendo os text's dos textViews do cvCard
         * e define que o usuario nao tem cartao registrado em seu documento da coleçao
         * users
         */
        binding.btnDelete.setOnClickListener{
            db.collection("cards")
                .whereEqualTo("userId" , user.uid.toString())
                .get()
                .addOnSuccessListener {
                    for (document in it.documents) {
                        document.reference.delete()
                    }
                    binding.btnAddCard.visibility = View.VISIBLE
                    binding.tvGetCard.visibility = View.VISIBLE
                    binding.cvCard.visibility = View.GONE

                    binding.tvCardName.text = ""
                    binding.tvCardNumber.text = " "
                    binding.tvCardDate.text = " "

                    val newRentalState = hashMapOf(
                        "cardRegistred" to false
                    )
                    db.collection("users")
                        .whereEqualTo("uid", user.uid)
                        .get()
                        .addOnSuccessListener {
                            for (document in it.documents) {
                                document.reference.update(newRentalState as Map<String, Any>)
                            }
                        }
                }
        }

        /***
         * Quando o btnLogout é clicado, apaga a conta do usuario cadastrada
         * no simpleStorage, realiza o signOut do Firebase Auth e inicia a
         * activity LoginActivity
         */
        binding.btnLogout.setOnClickListener{
            simpleStorage.clearUserAccount()
            auth.signOut()
            startActivity(Intent(requireContext(), SplashScreenActivity::class.java))
        }

        /***
         * Quando o btnAddCard é clicado, muda o fragmento exibido na main
         * activity para o fragmento AddCard
         */
        binding.btnAddCard.setOnClickListener {
            (activity as MainActivity).changeFragment(AddCard())

        }
        return binding.root
    }
}
