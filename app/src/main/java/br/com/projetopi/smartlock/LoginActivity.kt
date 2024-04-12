package br.com.projetopi.smartlock

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.gson.Gson

class LoginActivity : AppCompatActivity() {

    //Função que verifica se o usuario saiu de foco de um EditText e caso esteja vazio muda o TextLayout para erro
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

    //Função que retorna "false" caso um dos EditText estiverem vazios
    private fun isFilled(): Boolean {
        return !(etEmail.text.toString().isEmpty() ||
                etPassword.text.toString().isEmpty())
    }

    private fun hideKeyboard(it: View){
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(it.windowToken, 0)
    }

    //Função que faz com que caso esteja um editText esteja vazio muda o TextLayout para erro
    private fun showFieldErrors() {
        val editTexts = listOf(etEmail, etPassword)
        val textLayouts = listOf(tlEmail, tlPassword)

        editTexts.forEachIndexed { index, et ->
            if (et.text.toString().isEmpty()) {
                textLayouts[index].error = getString(R.string.preencha_campo)
            } else {
                textLayouts[index].error = null
            }
        }
    }

    private lateinit var tlEmail: TextInputLayout
    private lateinit var tlPassword: TextInputLayout

    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText

    private lateinit var btnEntrar: Button
    private lateinit var btnRecuperarSenha: Button
    private lateinit var btnCadastrar: Button
    private lateinit var btnConferir: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var simpleStorage: SimpleStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        simpleStorage = SimpleStorage(this)

        db = Firebase.firestore
        auth = Firebase.auth

        btnRecuperarSenha = findViewById(R.id.btnRecuperarSenha)
        btnEntrar = findViewById(R.id.btnEntrar)
        btnCadastrar = findViewById(R.id.btnCadastrar)
        btnConferir = findViewById(R.id.btnConferir)

        tlEmail = findViewById(R.id.tlEmail)
        tlPassword = findViewById(R.id.tlPassword)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)

        val editTexts = listOf(etEmail, etPassword)
        val textLayouts = listOf(tlEmail, tlPassword)

        editTexts.forEachIndexed { lt, et ->
            setOnFocusChangeListenerInputCheck(et, textLayouts[lt])
        }

        btnEntrar.setOnClickListener { it ->
            if (isFilled()) {
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()
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

                                db.collection("users").whereEqualTo("uid",user.uid).get()
                                    .addOnSuccessListener { documents ->
                                        for (document in documents) {

                                            simpleStorage.storageUserAccount(user)

                                            val iMain = Intent(this, MainActivity::class.java)
                                            val gson = Gson()
                                            val userJSON = gson.toJson(user)

                                            iMain.putExtra("userJson", userJSON)
                                            startActivity(iMain)

                                            finish()
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        Snackbar.make(
                                            btnEntrar,
                                            "Falha ao buscar os dados",
                                            Snackbar.LENGTH_LONG
                                        ).show()
                                    }
                            } else {
                                Snackbar.make(
                                    btnEntrar,
                                    "Seu email ainda não foi verificado",
                                    Snackbar.LENGTH_LONG
                                ).show()
                            }
                        } else {
                            Snackbar.make(
                                btnEntrar,
                                "E-mail ou senha incorretos",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                    }
                hideKeyboard(it)
            } else {
                showFieldErrors()
                Snackbar.make(btnEntrar, "Preencha todos os campos corretamente", Snackbar.LENGTH_LONG ).show()
            }
        }


        btnRecuperarSenha.setOnClickListener{
            startActivity(Intent(this, RecuperarSenhaActivity::class.java))
        }

        btnCadastrar.setOnClickListener{
            startActivity(Intent(this, CadastrarActivity::class.java))
        }

        btnConferir.setOnClickListener{
            startActivity(Intent(this, ConsultarMapaActivity::class.java))
        }
    }
}
