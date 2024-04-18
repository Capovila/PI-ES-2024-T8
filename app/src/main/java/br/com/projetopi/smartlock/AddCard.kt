package br.com.projetopi.smartlock

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class AddCard : Fragment() {
    private lateinit var btnAdicionar: Button

    private lateinit var etNumeroCartao: TextInputEditText
    private lateinit var etCVV: TextInputEditText
    private lateinit var etData: TextInputEditText
    private lateinit var etNomeTitular: TextInputEditText

    private lateinit var tlNumeroCartao: TextInputLayout
    private lateinit var tlCVV: TextInputLayout
    private lateinit var tlData: TextInputLayout
    private lateinit var tlNomeTitular: TextInputLayout

    private lateinit var simpleStorage: SimpleStorage

    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_add_card, container, false)

        simpleStorage = SimpleStorage(requireContext())

        val user: User = simpleStorage.getUserAccountData()

        btnAdicionar = root.findViewById(R.id.btnAdicionar)

        etNumeroCartao = root.findViewById(R.id.etNumeroCartao)
        etCVV = root.findViewById(R.id.etCVV)
        etData = root.findViewById(R.id.etData)
        etNomeTitular = root.findViewById(R.id.etNomeTitular)

        tlNumeroCartao = root.findViewById(R.id.tlNumeroCartao)
        tlCVV = root.findViewById(R.id.tlCVV)
        tlData = root.findViewById(R.id.tlData)
        tlNomeTitular = root.findViewById(R.id.tlNomeTitular)

        db = Firebase.firestore

        val editTexts = listOf(etNumeroCartao, etCVV, etData, etNomeTitular)
        val textLayouts = listOf(tlNumeroCartao, tlCVV, tlData, tlNomeTitular)

        editTexts.forEachIndexed { lt, et ->
            setOnFocusChangeListenerInputCheck(et, textLayouts[lt])
        }

        btnAdicionar.setOnClickListener{
            if(isFilled()) {
                val card = CreditCard(
                    user.uid.toString(),
                    etNumeroCartao.text.toString(),
                    etCVV.text.toString(),
                    etData.text.toString(),
                    etNomeTitular.text.toString()
                )
                db.collection("cards").add(card).addOnSuccessListener {
                    Toast.makeText(requireContext(), "Cartão adicionado!", Toast.LENGTH_LONG).show()
                    (activity as MainActivity).changeFragment(Profile())
                }
            } else {
                showFieldErrors()
                Snackbar.make(btnAdicionar, "Preencha todos os campos corretamente", Snackbar.LENGTH_LONG ).show()
            }
        }
        return root
    }
    private fun setOnFocusChangeListenerInputCheck(editText: TextInputEditText, textLayout: TextInputLayout) {
        editText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (editText.text.toString().isEmpty()) {
                    textLayout.error = getString(R.string.preencha_campo)
                } else {
                    textLayout.error = null
                }
            }
        }
    }

    private fun isFilled(): Boolean {
        return !(etNumeroCartao.text.toString().isEmpty() || etCVV.text.toString().isEmpty() ||
                etData.text.toString().isEmpty() || etNomeTitular.text.toString().isEmpty())
    }

    private fun showFieldErrors() {
        val editTexts = listOf(etNumeroCartao, etCVV, etData, etNomeTitular)
        val textLayouts = listOf(tlNumeroCartao, tlCVV, tlData, tlNomeTitular)

        editTexts.forEachIndexed { index, et ->
            if (et.text.toString().isEmpty()) {
                textLayouts[index].error = getString(R.string.preencha_campo)
            } else {
                textLayouts[index].error = null
            }
        }
    }
}