package br.com.projetopi.smartlock

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import org.w3c.dom.Text

class Profile : Fragment() {
    private lateinit var btnLogout: Button
    private lateinit var btnAddCard: CardView

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

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_profile, container, false)

        btnLogout = root.findViewById(R.id.btnLogout)
        btnAddCard = root.findViewById(R.id.btnAddCard)

        tvUserEmail = root.findViewById(R.id.tvUserEmail)
        tvUserName = root.findViewById(R.id.tvUserName)

        tvCardNumber = root.findViewById(R.id.tvCardNumber)
        tvCardName = root.findViewById(R.id.tvCardName)
        tvCardDate = root.findViewById(R.id.tvCardDate)
        tvGetCard = root.findViewById(R.id.tvGetCard)

        cvCard = root.findViewById(R.id.cvCard)
        simpleStorage = SimpleStorage(requireContext())

        auth = Firebase.auth

        val user: User = simpleStorage.getUserAccountData()

        db = Firebase.firestore

        cvCard.visibility = View.GONE

        val card: CreditCard = CreditCard(null, null, null, null, null)

        db.collection("cards").whereEqualTo("userId", user.uid.toString()).get().addOnSuccessListener {
            for(documents in it){
                card.cardNumber = documents.getString("cardNumber")
                card.cardName = documents.getString("cardName")
                card.expireDate = documents.getString("expireDate")
            }

            if(card.cardNumber != null && card.cardName != null){
                btnAddCard.visibility = View.GONE
                tvGetCard.visibility = View.GONE
                cvCard.visibility = View.VISIBLE


                tvCardName.setText("Titular: ${card.cardName}")
                tvCardNumber.setText("Final: ${card.cardNumber!!.substring(11, 15)}")
                tvCardDate.setText("Vencimento: ${card.expireDate}")

            }
        }


        tvUserEmail.text = user.email
        tvUserName.text = user.name

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