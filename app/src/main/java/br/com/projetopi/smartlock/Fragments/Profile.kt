package br.com.projetopi.smartlock.Fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.projetopi.smartlock.Classes.CreditCard
import br.com.projetopi.smartlock.Classes.User
import br.com.projetopi.smartlock.LoginActivity
import br.com.projetopi.smartlock.MainActivity
import br.com.projetopi.smartlock.SimpleStorage
import br.com.projetopi.smartlock.databinding.FragmentProfileBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
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

        db = Firebase.firestore

        binding.cvCard.visibility = View.GONE

        binding.tvUserEmail.text = user.email
        binding.tvUserName.text = user.name


        db.collection("cards").whereEqualTo("userId", user.uid).get().addOnSuccessListener {
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

        binding.btnLogout.setOnClickListener{
            simpleStorage.clearUserAccount()
            auth.signOut()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
        }


        binding.btnDelete.setOnClickListener{
            db.collection("cards").document(user.uid.toString()).delete().addOnSuccessListener {
                binding.btnAddCard.visibility = View.VISIBLE
                binding.tvGetCard.visibility = View.VISIBLE
                binding.cvCard.visibility = View.GONE

                binding.tvCardName.text = ""
                binding.tvCardNumber.text = " "
                binding.tvCardDate.text = " "
            }
        }

        binding.btnLogout.setOnClickListener{
            simpleStorage.clearUserAccount()
            auth.signOut()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
        }

        binding.btnAddCard.setOnClickListener {
            (activity as MainActivity).changeFragment(AddCard())

        }
        return binding.root
    }
}
