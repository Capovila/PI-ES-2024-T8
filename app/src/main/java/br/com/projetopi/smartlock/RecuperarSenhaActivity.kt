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

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = Firebase.auth

        setOnFocusChangeListenerInputCheck(binding.etEmail, binding.tlEmail)

        binding.btnRecuperar.setOnClickListener{
            if(isFilled()) {
                auth.sendPasswordResetEmail(binding.etEmail.text.toString()).addOnCompleteListener{
                    if(it.isSuccessful){
                        Toast.makeText(baseContext, "E-mail enviado", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }else{
                        Snackbar.make(binding.btnRecuperar, "E-mail inválido", Snackbar.LENGTH_LONG).show()
                    }
                }
                hideKeyboard(it)
            } else {
                showFieldErrors()
                Snackbar.make(binding.btnRecuperar, "Preencha o campo corretamente", Snackbar.LENGTH_LONG).show()
            }
        }

        binding.btnBack.setOnClickListener {
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
        return binding.etEmail.text.toString().isNotEmpty()
    }

    private fun hideKeyboard(it: View){
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(it.windowToken, 0)
    }

    private fun showFieldErrors() {
        if (binding.etEmail.text.toString().isEmpty()) {
            binding.tlEmail.error = getString(R.string.preencha_campo)
        } else {
            binding.tlEmail.error = null
        }
    }
}