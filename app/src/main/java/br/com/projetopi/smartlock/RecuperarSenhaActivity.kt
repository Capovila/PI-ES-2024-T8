package br.com.projetopi.smartlock

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class RecuperarSenhaActivity : AppCompatActivity() {

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
        return etEmail.text.toString().isNotEmpty()
    }

    private fun hideKeyboard(it: View){
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(it.windowToken, 0)
    }

    //Função que faz com que caso esteja um editText esteja vazio muda o TextLayout para erro
    private fun showFieldErrors() {
        if (etEmail.text.toString().isEmpty()) {
            tlEmail.error = getString(R.string.preencha_campo)
        } else {
            tlEmail.error = null
        }
    }

    private lateinit var tlEmail: TextInputLayout
    private lateinit var etEmail: TextInputEditText
    private lateinit var btnRecuperar: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_senha)

        auth = Firebase.auth
        btnRecuperar = findViewById(R.id.btnRecuperar)
        tlEmail = findViewById(R.id.tlEmail)
        etEmail = findViewById(R.id.etEmail)

        setOnFocusChangeListenerInputCheck(etEmail, tlEmail)

        btnRecuperar.setOnClickListener{
            if(isFilled()) {
                auth.sendPasswordResetEmail(etEmail.text.toString()).addOnCompleteListener{
                    if(it.isSuccessful){
                        Toast.makeText(baseContext, "E-mail enviado", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }else{
                        Snackbar.make(btnRecuperar, "E-mail inválido", Snackbar.LENGTH_LONG).show()
                    }
                }
                hideKeyboard(it)
            } else {
                showFieldErrors()
                Snackbar.make(btnRecuperar, "Preencha o campo corretamente", Snackbar.LENGTH_LONG).show()
            }
        }
    }
}