package br.com.projetopi.smartlock

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class Profile : Fragment() {
    private lateinit var btnLogout: Button
    private lateinit var tvUserEmail: TextView

    private lateinit var auth: FirebaseAuth
    private lateinit var simpleStorage: SimpleStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_profile, container, false)

        btnLogout = root.findViewById(R.id.btnLogout)
        simpleStorage = SimpleStorage(requireContext())
        tvUserEmail = root.findViewById(R.id.tvUserEmail)

        auth = Firebase.auth

        val user: User = simpleStorage.getUserAccountData()

        tvUserEmail.setText(user.email)


        btnLogout.setOnClickListener{
            simpleStorage.clearUserAccount()
            auth.signOut()
            startActivity(Intent(requireContext(),LoginActivity::class.java))
        }

        return root
    }
}