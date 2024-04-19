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
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import org.w3c.dom.Text

class Profile : Fragment() {
    private lateinit var btnLogout: Button
    private lateinit var btnAddCard: ImageView
    private lateinit var btnDelete: ImageView

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
        btnDelete = root.findViewById(R.id.btnDelete)

        tvUserEmail = root.findViewById(R.id.tvUserEmail)
        tvUserName = root.findViewById(R.id.tvUserName)

        tvCardNumber = root.findViewById(R.id.tvCardNumber)
        tvCardName = root.findViewById(R.id.tvCardName)
        tvCardDate = root.findViewById(R.id.tvCardDate)

        simpleStorage = SimpleStorage(requireContext())

        auth = Firebase.auth

        val user: User = simpleStorage.getUserAccountData()

        btnDelete.visibility = View.GONE

        db = Firebase.firestore

        val card: CreditCard = CreditCard(null, null, null, null, null)

        db.collection("cards").whereEqualTo("userId", user.uid.toString()).get().addOnSuccessListener {
            for(documents in it){
                card.cardNumber = documents.getString("cardNumber")
                card.cardName = documents.getString("cardName")
                card.expireDate = documents.getString("expireDate")
            }

                btnAddCard.visibility = View.GONE
                btnDelete.visibility = View.VISIBLE


                var str: String = card.cardNumber!!.substring(11, 15)


                tvCardName.setText("Titular: ${card.cardName}")
                tvCardNumber.setText("Final: $str")
                tvCardDate.setText("Vencimento: ${card.expireDate}")

        }

        btnDelete.setOnClickListener{
            db.collection("cards").document(user.uid.toString()).delete().addOnSuccessListener {
                Toast.makeText(requireContext(), "Cartão removido com sucesso", Toast.LENGTH_SHORT).show()
                btnDelete.visibility = View.GONE
                btnAddCard.visibility = View.VISIBLE

                tvCardName.setText("Buscando cartão...")
                tvCardNumber.setText("")
                tvCardDate.setText("")
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