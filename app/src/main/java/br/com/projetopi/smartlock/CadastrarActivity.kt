package br.com.projetopi.smartlock

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class CadastrarActivity : AppCompatActivity() {

    //Função que verifica se o usuario saiu de foco de um EditText e caso esteja vazio muda o TextLayout para erro
    private fun setOnFocusChangeListener(editText: TextInputEditText, textLayout: TextInputLayout) {
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
                    etPassword.text.toString().isEmpty() || etBirth.text.toString().isEmpty() ||
                    etCPF.text.toString().isEmpty() || etPhone.text.toString().isEmpty())
    }

    //Função que faz com que o teclado do celular se esconda
    private fun hideKeybard(it: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(it.windowToken, 0)
    }

    //Função que faz com que caso esteja um editText esteja vazio muda o TextLayout para erro
    private fun showFieldErrors() {
        val editTexts = listOf(etName, etEmail, etPassword, etBirth, etCPF, etPhone)
        val textLayouts = listOf(tlName, tlEmail, tlPassword, tlBirth, tlCPF, tlPhone)

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
    private lateinit var tlBirth: TextInputLayout
    private lateinit var tlCPF: TextInputLayout
    private lateinit var tlPhone: TextInputLayout

    private lateinit var etName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etBirth: TextInputEditText
    private lateinit var etCPF: TextInputEditText
    private lateinit var etPhone: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastrar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val db = Firebase.firestore
        val auth = Firebase.auth

        val btnCadastrar = findViewById<Button>(R.id.btnCadastrar)

        tlName = findViewById(R.id.tlName)
        tlEmail = findViewById(R.id.tlEmail)
        tlPassword = findViewById(R.id.tlPassword)
        tlBirth = findViewById(R.id.tlBirth)
        tlCPF = findViewById(R.id.tlCPF)
        tlPhone = findViewById(R.id.tlPhone)

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etBirth = findViewById(R.id.etBirth)
        etCPF = findViewById(R.id.etCPF)
        etPhone = findViewById(R.id.etPhone)

        val editTexts = listOf(etName, etEmail, etPassword, etBirth, etCPF, etPhone)
        val textLayouts = listOf(tlName, tlEmail, tlPassword, tlBirth, tlCPF, tlPhone)

        editTexts.forEachIndexed { lt, et ->
            setOnFocusChangeListener(et, textLayouts[lt])
        }

        btnCadastrar.setOnClickListener{ it ->
            if(isFilled()) {
                val user = hashMapOf(
                    "Name" to etName.text.toString(),
                    "Email" to etEmail.text.toString(),
                    "Idade" to etBirth.text.toString().toInt(),
                    "CPF" to etCPF.text.toString().toInt(),
                    "Telefone" to etPhone.text.toString().toInt()
                )

                db.collection("users").add(user)

                auth.createUserWithEmailAndPassword(etEmail.text.toString(),
                    etPassword.text.toString())
                    .addOnCompleteListener {
                        if(it.isSuccessful){
                            auth.currentUser?.sendEmailVerification()?.addOnCompleteListener{
                                Snackbar.make(btnCadastrar, "Confirmação de e-mail enviada", Snackbar.LENGTH_LONG).show()
                            }
                        } else{
                            Snackbar.make(btnCadastrar, it.exception!!.message.toString(), Snackbar.LENGTH_LONG).show()
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