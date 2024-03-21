package br.com.projetopi.smartlock

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {

    private lateinit var btnEntrar: Button
    private lateinit var etEmailLogin: EditText
    private lateinit var etSenha: EditText
    private lateinit var tvTextoEmail: TextView
    private lateinit var tvSenhaLogin: TextView

    private fun hideKeyboard(it: View){
        var imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(it.windowToken, 0)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnEntrar = findViewById(R.id.btnEntrar)
        etSenha = findViewById(R.id.etSenha)
        etEmailLogin = findViewById(R.id.etEmailLogin)
        tvTextoEmail = findViewById(R.id.tvTextoEmail)
        tvSenhaLogin = findViewById(R.id.tvSenhaLogin)

        btnEntrar.setOnClickListener{
            if(etEmailLogin.text.toString().isEmpty() ||
                etSenha.text.toString().isEmpty()){
                var mensagemVazio: String = "Insira seu e-mail e senha corretamente"
                Snackbar.make(btnEntrar, mensagemVazio, Snackbar.LENGTH_LONG ).show()
                hideKeyboard(it)
                if(etEmailLogin.text.toString().isEmpty()){
                    var textoEmail: String = tvTextoEmail.text.toString()
                    tvTextoEmail.setTextColor(getColor(R.color.red))
                    tvTextoEmail.text = "$textoEmail *"
                }
                if(etSenha.text.toString().isEmpty()) {
                    var textoSenha: String = tvSenhaLogin.text.toString()
                    tvSenhaLogin.setTextColor(getColor(R.color.red))
                    tvSenhaLogin.text = "$textoSenha *"
                }
            }else{
                var mensagem = etSenha.text.toString()
                Snackbar.make(btnEntrar, mensagem, Snackbar.LENGTH_LONG ).show()
                hideKeyboard(it)
            }
        }
    }
}