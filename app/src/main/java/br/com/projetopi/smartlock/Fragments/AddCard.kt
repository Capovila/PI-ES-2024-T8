package br.com.projetopi.smartlock.Fragments

import android.net.ConnectivityManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import br.com.projetopi.smartlock.Classes.CreditCard
import br.com.projetopi.smartlock.Classes.User
import br.com.projetopi.smartlock.MainActivity
import br.com.projetopi.smartlock.R
import br.com.projetopi.smartlock.SimpleStorage
import br.com.projetopi.smartlock.databinding.FragmentAddCardBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.util.Calendar

class AddCard : Fragment() {

    private var _binding: FragmentAddCardBinding? = null
    private val binding get() = _binding!!
    private lateinit var simpleStorage: SimpleStorage
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAddCardBinding.inflate(inflater, container, false)

        simpleStorage = SimpleStorage(requireContext())

        val connectivityManager = requireContext().getSystemService(ConnectivityManager::class.java)


        val user: User = simpleStorage.getUserAccountData()

        db = Firebase.firestore

        val editTexts =
            listOf(
                binding.etNumeroCartao,
                binding.etCVV,
                binding.etDataMes,
                binding.etDataAno,
                binding.etNomeTitular
            )

        val textLayouts =
            listOf(
                binding.tlNumeroCartao,
                binding.tlCVV,
                binding.tlDataMes,
                binding.tlDataAno,
                binding.tlNomeTitular
            )

        /***
         * Faz com que para cada editText junto com seu textLayout
         * execute a função setOnFocusChangeListenerInputCheck
         */
        editTexts.forEachIndexed { lt, et ->
            setOnFocusChangeListenerInputCheck(et, textLayouts[lt])
        }

        /***
         * Faz com que quando clicado o btnBack, mude o fragment mostrado
         * na MainActivity para o fragment Profile
         */
        binding.btnBack.setOnClickListener {
            (activity as MainActivity).changeFragment(Profile())
        }

        /***
         * Faz com que quando clicado o btnAdicionar, verifica
         * se todos os campos foram preenchidos, caso nao
         * tenha sido executa a função showFieldErrors e
         * mostra um snackbar instruindo o usuario, caso todos os
         * campos tenham sido preenchidos define a variavel card
         * do tipo CreditCard, atribui os dados inseridos,
         * adiciona no banco de dados na collection "cards"
         * a variavel card como um novo documento, muda no
         * documento do usuario da coleção users que o
         * usuario possui um cartao registrado, mostra um
         * toast instruindo o usuario e muda o fragment
         * mostrado na MainActivity para o fragment Profile
         */
        binding.btnAdicionar.setOnClickListener {
            if (isFilled() && currentDate()) {
                if(connectivityManager.activeNetwork == null){
                    Toast.makeText(requireContext(), "Internet necessária para adicioanr cartão no app", Toast.LENGTH_LONG).show()
                }else{

                val data:String = "${binding.etDataMes.text.toString()} / ${binding.etDataAno.text.toString()}"

                val card = CreditCard(
                    user.uid.toString(),
                    binding.etNumeroCartao.text.toString(),
                    binding.etCVV.text.toString(),
                    data,
                    binding.etNomeTitular.text.toString()
                )
                db.collection("cards")
                    .add(card)
                    .addOnSuccessListener {
                        val newRentalState = hashMapOf(
                            "cardRegistred" to true
                        )
                        db.collection("users")
                            .whereEqualTo("uid", user.uid)
                            .get()
                            .addOnSuccessListener {
                                for (document in it.documents) {
                                    document.reference
                                        .update(newRentalState as Map<String, Any>)
                                }
                            }
                        Toast.makeText(
                            requireContext(),
                            "Cartão adicionado!",
                            Toast.LENGTH_LONG
                        ).show()
                        (activity as MainActivity).changeFragment(Profile())
                    }
                }
            } else {
                showFieldErrors()
                Toast.makeText(
                    requireContext(),
                    "Preencha todos os campos corretamente",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        return binding.root
    }

    /***
     * Faz com que quando executada, recebe pela lista de
     * parametros o editText e o textLayout, define um listener
     * no editText e quando o foco sai do editText,
     * se nao estiver preenchido, define o textLayout.error
     * (funcionalidade do m2.material) como "Preencha o campo",
     * caso o editText tenha sido preenchido, define o
     * textLayout.error como null
     */
    private fun setOnFocusChangeListenerInputCheck(
        editText: TextInputEditText,
        textLayout: TextInputLayout
    ) {
        editText
            .setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (editText.text.toString().isEmpty()) {
                    textLayout.error = getString(R.string.preencha_campo)
                } else {
                    textLayout.error = null
                }
            }
        }
    }

    /***
     * Faz com que quando executada, retorna um boolean
     * True caso todos os editText tenham sido preenchidos, e False
     * caso um dos editText nao estiver preenchido
     */
    private fun isFilled(): Boolean {
        return !(
                binding.etNumeroCartao.text.toString().isEmpty()
                || binding.etCVV.text.toString().isEmpty()
                || binding.etDataMes.text.toString().isEmpty()
                || binding.etDataAno.text.toString().isEmpty()
                || binding.etNomeTitular.text.toString().isEmpty()
                )
    }

    private fun currentDate():Boolean {
        return if (binding.etDataAno.text.toString().toInt() > Calendar.getInstance().get(Calendar.YEAR) &&
            binding.etDataMes.text.toString().toInt() <= 12){
            true
        }else if (binding.etDataAno.text.toString().toInt() == Calendar.getInstance().get(Calendar.YEAR)){
            if(binding.etDataMes.text.toString().toInt() >= Calendar.getInstance().get(Calendar.MONTH)&&
                binding.etDataMes.text.toString().toInt() <= 12){
                true
            }else{
                false
            }
        }else{
            false
        }


    }

    /***
     * Faz com que quando executada, percorre a lista
     * de editText e para cada editText verifica se foi preenchido,
     * caso esteja preenchido o textLayout.error correspondente
     * é atribuido com null, caso contrario atribui o
     * textLayout.error correspondente com "Preencha o campo"
     */
    private fun showFieldErrors() {
        val editTexts =
            listOf(
                binding.etNumeroCartao,
                binding.etCVV,
                binding.etDataMes,
                binding.etDataAno,
                binding.etNomeTitular
            )
        val textLayouts =
            listOf(
                binding.tlNumeroCartao,
                binding.tlCVV,
                binding.tlDataMes,
                binding.tlDataAno,
                binding.tlNomeTitular
            )

        editTexts.forEachIndexed { index, et ->
            if (et.text.toString().isEmpty()) {
                textLayouts[index].error = getString(R.string.preencha_campo)
            } else {
                textLayouts[index].error = null
            }
        }
    }
}



