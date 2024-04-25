package br.com.projetopi.smartlock

import android.annotation.SuppressLint
import android.content.Intent
import android.media.Image
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import org.w3c.dom.Text

class Profile : Fragment() {
    private lateinit var btnLogout: Button
    private lateinit var btnAddCard: CardView
    private lateinit var btnDelete: ImageView

    private lateinit var cvCard: CardView

    private lateinit var tvGetCard: TextView

    private lateinit var tvUserEmail: TextView
    private lateinit var tvUserName: TextView
    private lateinit var tvCardName: TextView
    private lateinit var tvCardNumber: TextView
    private lateinit var tvCardDate: TextView

    private lateinit var auth: FirebaseAuth
    private lateinit var simpleStorage: SimpleStorage
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_profile, container, false)

        btnLogout = root.findViewById(R.id.btnLogout)
        btnAddCard = root.findViewById(R.id.btnAddCard)
        btnDelete = root.findViewById(R.id.btnDelete)

        tvUserEmail = root.findViewById(R.id.tvUserEmail)
        tvUserName = root.findViewById(R.id.tvUserName)

        tvCardNumber = root.findViewById(R.id.tvCardNumber)
        tvCardName = root.findViewById(R.id.tvCardName)
        tvCardDate = root.findViewById(R.id.tvCardDate)
        tvGetCard = root.findViewById(R.id.tvGetCard)

        cvCard = root.findViewById(R.id.cvCard)
        simpleStorage = SimpleStorage(requireContext())

        val user: User = simpleStorage.getUserAccountData()

        db = Firebase.firestore

        cvCard.visibility = View.GONE

        tvUserEmail.text = user.email
        tvUserName.text = user.name


        val card: CreditCard = CreditCard(null, null, null, null, null)
        tvCardNumber.text = "Nenhum cartão cadastrado"

        db.collection("cards").whereEqualTo("userId", user.uid).get().addOnSuccessListener {
            for(documents in it){
                val cardName = documents.getString("cardName")
                tvCardName.text = "Titular: $cardName"
                val cardNumber = documents.getString("cardNumber")
                if (cardNumber != null && cardNumber.length >= 15) {
                    val str: String = cardNumber.substring(11, 15)
                    tvCardNumber.text = "Final: $str"
                } else {
                    tvCardNumber.text = "Final: Invalid Card Number"
                }
                val cardDate = documents.getString("expireDate")
                tvCardDate.text = "Vencimento: $cardDate"

                btnAddCard.visibility = View.GONE
                tvGetCard.visibility = View.GONE
                cvCard.visibility = View.VISIBLE
            }

            tvUserEmail.text = user.email
            tvUserName.text = user.name
        }

        btnLogout.setOnClickListener{
            simpleStorage.clearUserAccount()
            auth.signOut()
            startActivity(Intent(requireContext(),LoginActivity::class.java))
        }


        btnDelete.setOnClickListener{
            db.collection("cards").document(user.uid.toString()).delete().addOnSuccessListener {
                btnAddCard.visibility = View.VISIBLE
                tvGetCard.visibility = View.VISIBLE
                cvCard.visibility = View.GONE

                tvCardName.setText("")
                tvCardNumber.setText("")
                tvCardDate.setText("")
            }
        }

        btnLogout.setOnClickListener{
            simpleStorage.clearUserAccount()
            auth.signOut()
            startActivity(Intent(requireContext(),LoginActivity::class.java))
        }

        btnAddCard.setOnClickListener {
            (activity as MainActivity).changeFragment(AddCard())

        }
        return root
    }
}
