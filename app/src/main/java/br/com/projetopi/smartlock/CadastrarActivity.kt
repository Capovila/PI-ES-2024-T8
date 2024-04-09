package br.com.projetopi.smartlock

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

class CadastrarActivity : AppCompatActivity() {

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
        return !(etName.text.toString().isEmpty() || etEmail.text.toString().isEmpty() ||
                    etPassword.text.toString().isEmpty() || etAge.text.toString().isEmpty() ||
                    etCPF.text.toString().isEmpty() || etPhone.text.toString().isEmpty())
    }

    //Função que faz com que o teclado do celular se esconda
    private fun hideKeybard(it: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(it.windowToken, 0)
    }

    //Função que faz com que caso esteja um editText esteja vazio muda o TextLayout para erro
    private fun showFieldErrors() {
        val editTexts = listOf(etName, etEmail, etPassword, etAge, etCPF, etPhone)
        val textLayouts = listOf(tlName, tlEmail, tlPassword, tlAge, tlCPF, tlPhone)

        editTexts.forEachIndexed { index, et ->
            if (et.text.toString().isEmpty()) {
                textLayouts[index].error = getString(R.string.preencha_campo)
            } else {
                textLayouts[index].error = null
            }
        }
    }

    private lateinit var tlName: TextInputLayout
    private lateinit var tlEmail: TextInputLayout
    private lateinit var tlPassword: TextInputLayout
    private lateinit var tlAge: TextInputLayout
    private lateinit var tlCPF: TextInputLayout
    private lateinit var tlPhone: TextInputLayout

    private lateinit var etName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etAge: TextInputEditText
    private lateinit var etCPF: TextInputEditText
    private lateinit var etPhone: TextInputEditText

    private lateinit var btnCadastrar: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var simpleStorage: SimpleStorage


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastrar)

        simpleStorage = SimpleStorage(this)

        db = Firebase.firestore
        auth = Firebase.auth

        btnCadastrar = findViewById(R.id.btnCadastrar)

        tlName = findViewById(R.id.tlName)
        tlEmail = findViewById(R.id.tlEmail)
        tlPassword = findViewById(R.id.tlPassword)
        tlAge = findViewById(R.id.tlAge)
        tlCPF = findViewById(R.id.tlCPF)
        tlPhone = findViewById(R.id.tlPhone)

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etAge = findViewById(R.id.etAge)
        etCPF = findViewById(R.id.etCPF)
        etPhone = findViewById(R.id.etPhone)

        val editTexts = listOf(etName, etEmail, etPassword, etAge, etCPF, etPhone)
        val textLayouts = listOf(tlName, tlEmail, tlPassword, tlAge, tlCPF, tlPhone)

        editTexts.forEachIndexed { lt, et ->
            setOnFocusChangeListenerInputCheck(et, textLayouts[lt])
        }

        btnCadastrar.setOnClickListener{ it ->
            if(isFilled()) {
                val user = User(
                    null,
                    etName.text.toString(),
                    etEmail.text.toString(),
                    etPassword.text.toString(),
                    etAge.text.toString().toInt(),
                    etCPF.text.toString(),
                    etPhone.text.toString()
                )

                auth.createUserWithEmailAndPassword(user.email!!, user.password!!)
                    .addOnCompleteListener { authResult ->
                        if(authResult.isSuccessful){
                            user.uid = authResult.result.user!!.uid
                            user.password = ""

                            db.collection("users").add(user).addOnSuccessListener {
                                auth.currentUser?.sendEmailVerification()?.addOnCompleteListener {
                                    Toast.makeText(
                                        baseContext,
                                        "Confirmação de e-mail enviada",
                                        Toast.LENGTH_LONG,
                                    ).show()

                                    startActivity(Intent(this, LoginActivity::class.java))
                                    finish()
                                }
                            }
                        } else {
                                Snackbar.make(btnCadastrar, authResult.exception!!.message.toString(), Snackbar.LENGTH_LONG).show()
                                }

                    }
                hideKeybard(it)
            } else {
                showFieldErrors()
                Snackbar.make(btnCadastrar, "Preencha todos os campos corretamente", Snackbar.LENGTH_LONG ).show()
            }
        }
    }
}