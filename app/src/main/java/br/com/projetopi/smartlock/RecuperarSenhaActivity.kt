package br.com.projetopi.smartlock

import android.content.Intent
import android.net.ConnectivityManager
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
import br.com.projetopi.smartlock.databinding.ActivityRecuperarSenhaBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class RecuperarSenhaActivity : AppCompatActivity() {

    private lateinit var binding:ActivityRecuperarSenhaBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityRecuperarSenhaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = Firebase.auth
        val connectivityManager = getSystemService(ConnectivityManager::class.java)

        /***
         * Faz com que para cada editText junto com seu textLayout
         * execute a função setOnFocusChangeListenerInputCheck
         */
        setOnFocusChangeListenerInputCheck(binding.etEmail, binding.tlEmail)

        /***
         * Faz com que quando o btnRecuperar é clicado, verifica se
         * todos os campos foram preenchidos, caso True, envia o email
         * de recuperação de senha para o email digitado, caso seja
         * enviado com sucesso, mostra um Toast com a mensagem de
         * sucesso e inicia a activity LoginActivity, fecha a activity
         * atual e esconde o teclado
         */
        binding.btnRecuperar.setOnClickListener{
            if(isFilled()) {
                if(connectivityManager.activeNetwork == null){
                    Toast.makeText(this, "Internet necessária para recuperar a senha do app", Toast.LENGTH_LONG).show()
                }else{
                auth.sendPasswordResetEmail(binding.etEmail.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(
                            baseContext,
                            "E-mail enviado",
                            Toast.LENGTH_LONG
                        ).show()

                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "E-mail inválido",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                hideKeyboard(it)
                }
            } else {
                showFieldErrors()
                Toast.makeText(
                    this,
                    "Preencha o campo corretamente",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        /***
         * Faz com que quando clicado o btnBack, fecha a activity
         * RecuperarSenhaActivity, activity atual
         */
        binding.btnBack.setOnClickListener {
            finish()
        }
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

    /***
     * Faz com que quando executada, retorna um boolean
     * True caso todos os editText tenham sido preenchidos, e False
     * caso um dos editText nao estiver preenchido
     */
    private fun isFilled(): Boolean {
        return binding.etEmail.text.toString().isNotEmpty()
    }

    /***
     * Faz com que quando chamada, esconde o teclado do dispositivo
     */
    private fun hideKeyboard(it: View){
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(it.windowToken, 0)
    }

    /***
     * Faz com que quando executada, percorre a lista
     * de editText e para cada editText verifica se foi preenchido,
     * caso esteja preenchido o textLayout.error correspondente
     * é atribuido com null, caso contrario atribui o
     * textLayout.error correspondente com "Preencha o campo"
     */
    private fun showFieldErrors() {
        if (binding.etEmail.text.toString().isEmpty()) {
            binding.tlEmail.error = getString(R.string.preencha_campo)
        } else {
            binding.tlEmail.error = null
        }
    }
}