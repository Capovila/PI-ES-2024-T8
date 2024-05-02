package br.com.projetopi.smartlock

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.projetopi.smartlock.Classes.User
import br.com.projetopi.smartlock.databinding.ActivityCadastrarBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CadastrarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastrarBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var simpleStorage: SimpleStorage

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityCadastrarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Habilita o modo de "edge-to-edge"
        enableEdgeToEdge()

        // Deixa transparente o statusBar e o navigationBar
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        simpleStorage = SimpleStorage(this)

        db = Firebase.firestore
        auth = Firebase.auth


        val editTexts = listOf(
            binding.etName,
            binding.etEmail,
            binding.etPassword,
            binding.etAge,
            binding.etCPF,
            binding.etPhone
        )

        val textLayouts = listOf(
            binding.tlName,
            binding.tlEmail,
            binding.tlPassword,
            binding.tlAge,
            binding.tlCPF,
            binding.tlPhone
        )


        // Verifica se os EditTexts perderam o foco e atualiza os TextLayouts correspondentes
        editTexts.forEachIndexed { lt, et ->
            setOnFocusChangeListenerInputCheck(et, textLayouts[lt])
        }

        // Botão de cadastro do usuário
        binding.btnCadastrar.setOnClickListener{ it ->
            //Verifica se todos os campos foram preenchidos
            if(isFilled()) {
                // Cria um objeto User com os dados dos EditTexts
                val user = User(
                    null,
                    binding.etName.text.toString(),
                    binding.etEmail.text.toString(),
                    binding.etPassword.text.toString(),
                    binding.etAge.text.toString().toInt(),
                    binding.etCPF.text.toString(),
                    binding.etPhone.text.toString()
                )

                // Cria o usuário no Firebase Authentication
                auth.createUserWithEmailAndPassword(user.email!!, user.password!!)
                    .addOnCompleteListener { authResult ->
                        if(authResult.isSuccessful) {
                            user.uid = authResult.result.user!!.uid
                            user.password = ""

                            db.collection("users")
                                .add(user)
                                .addOnSuccessListener {
                                    auth.currentUser?.sendEmailVerification()
                                        ?.addOnCompleteListener {
                                            Toast.makeText(
                                                baseContext,
                                                "Confirmação de e-mail enviada",
                                                Toast.LENGTH_LONG,
                                            ).show()

                                            startActivity(Intent(this, LoginActivity::class.java))
                                            finish()
                                        }
                                }
                        }
                            else {
                            Snackbar.make(
                                binding.btnCadastrar,
                                authResult.exception!!.message.toString(),
                                Snackbar.LENGTH_LONG
                            ).show()
                            // Exibe uma mensagem de erro em caso de falha no cadastro
                            Snackbar.make(binding.btnCadastrar, authResult.exception!!.message.toString(), Snackbar.LENGTH_LONG).show()
                        }

                    }
                hideKeybard(it)
            } else {
                // Exibe uma mensagem de erro se algum campo não estiver preenchido corretamente
                showFieldErrors()
                Snackbar.make(
                    binding.btnCadastrar,
                    "Preencha todos os campos corretamente",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        // Botão de voltar
        binding.btnBack.setOnClickListener{
            finish()
        }
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
        return !(
                binding.etName.text.toString().isEmpty()
                || binding.etEmail.text.toString().isEmpty()
                || binding.etPassword.text.toString().isEmpty()
                || binding.etAge.text.toString().isEmpty()
                || binding.etCPF.text.toString().isEmpty()
                || binding.etPhone.text.toString().isEmpty()
                )
    }

    private fun hideKeybard(it: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(it.windowToken, 0)
    }

    private fun showFieldErrors() {
        val editTexts = listOf(
            binding.etName,
            binding.etEmail,
            binding.etPassword,
            binding.etAge,
            binding.etCPF,
            binding.etPhone
        )

        val textLayouts = listOf(
            binding.tlName,
            binding.tlEmail,
            binding.tlPassword,
            binding.tlAge,
            binding.tlCPF,
            binding.tlPhone
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