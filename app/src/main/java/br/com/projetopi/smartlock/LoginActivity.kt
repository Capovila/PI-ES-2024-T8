package br.com.projetopi.smartlock

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.projetopi.smartlock.Classes.User
import br.com.projetopi.smartlock.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var simpleStorage: SimpleStorage

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        simpleStorage = SimpleStorage(this)

        db = Firebase.firestore
        auth = Firebase.auth

        val editTexts = listOf(
            binding.etEmail,
            binding.etPassword
        )

        val textLayouts = listOf(
            binding.tlEmail,
            binding.tlPassword
        )

        editTexts.forEachIndexed { lt, et ->
            setOnFocusChangeListenerInputCheck(et, textLayouts[lt])
        }

        binding.btnEntrar.setOnClickListener { it ->
            if (isFilled()) {
                val email = binding.etEmail.text.toString()
                val password = binding.etPassword.text.toString()
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val verification = auth.currentUser?.isEmailVerified

                            if (verification == true) {
                                val account = auth.currentUser!!
                                val user = User(
                                    account.uid,
                                    account.displayName,
                                    account.email,
                                    null,
                                    0,
                                    null,
                                    null
                                )

                                db.collection("users")
                                    .whereEqualTo("uid",user.uid)
                                    .get()
                                    .addOnSuccessListener { documents ->
                                        for (document in documents) {
                                            user.name = document.getString("name")
                                            user.CPF = document.getString("cpf")
                                            user.phone = document.getString("phone")
                                            user.age = (document.get("age") as Long).toInt()

                                            simpleStorage.storageUserAccount(user)

                                            startActivity(Intent(this, MainActivity::class.java))
                                            finish()
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        Snackbar.make(
                                            binding.btnEntrar,
                                            "Falha ao buscar os dados",
                                            Snackbar.LENGTH_LONG
                                        ).show()
                                    }
                            } else {
                                Snackbar.make(
                                    binding.btnEntrar,
                                    "Seu email ainda não foi verificado",
                                    Snackbar.LENGTH_LONG
                                ).show()
                            }
                        } else {
                            Snackbar.make(
                                binding.btnEntrar,
                                "E-mail ou senha incorretos",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                    }
                hideKeyboard(it)
            } else {
                showFieldErrors()
                Snackbar.make(
                    binding.btnEntrar,
                    "Preencha todos os campos corretamente",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        binding.btnRecuperarSenha.setOnClickListener{
            startActivity(Intent(this, RecuperarSenhaActivity::class.java))
        }

        binding.btnCadastrar.setOnClickListener{
            startActivity(Intent(this, CadastrarActivity::class.java))
        }

        binding.btnConferir.setOnClickListener{
            startActivity(Intent(this, ConsultarMapaActivity::class.java))
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
                binding.etEmail.text.toString().isEmpty()
                || binding.etPassword.text.toString().isEmpty()
                )
    }

    private fun hideKeyboard(it: View){
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(it.windowToken, 0)
    }

    private fun showFieldErrors() {
        val editTexts = listOf(
            binding.etEmail,
            binding.etPassword
        )

        val textLayouts = listOf(
            binding.tlEmail,
            binding.tlPassword
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
