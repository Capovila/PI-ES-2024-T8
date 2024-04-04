package br.com.projetopi.smartlock

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class LoginActivity : AppCompatActivity() {

    private lateinit var btnEntrar: Button
    private lateinit var btnRecuperarSenha: Button

    private lateinit var etEmailLogin: EditText
    private lateinit var etSenha: EditText

    private lateinit var tvTextoEmail: TextView
    private lateinit var tvSenhaLogin: TextView

    private lateinit var auth: FirebaseAuth



    private fun validarCampo(texto:TextView, campo:EditText){
        if(campo.text.toString().isEmpty()){
            texto.setTextColor(getColor(R.color.red))
        }
        else{
            texto.setTextColor(getColor(R.color.black))
        }
    }

    private fun isNotFulfileld(): Boolean{
        return etEmailLogin.text.toString().isEmpty() ||
                etSenha.text.toString().isEmpty()
    }


    private fun hideKeyboard(it: View){
        var imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(it.windowToken, 0)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnRecuperarSenha = findViewById(R.id.btnRecuperarSenha)
        btnEntrar = findViewById(R.id.btnEntrar)

        etSenha = findViewById(R.id.etSenha)
        etEmailLogin = findViewById(R.id.etEmailLogin)

        tvTextoEmail = findViewById(R.id.tvTextoEmail)
        tvSenhaLogin = findViewById(R.id.tvSenhaLogin)

        auth = Firebase.auth



            btnEntrar.setOnClickListener{


                    validarCampo(tvTextoEmail, etEmailLogin)
                    validarCampo(tvSenhaLogin, etSenha)

                    if(isNotFulfileld()){
                        Snackbar.make(btnEntrar, "Insira seu e-mail e senha corretamente", Snackbar.LENGTH_LONG ).show()
                        hideKeyboard(it)

                    }else {

                        auth.signInWithEmailAndPassword(
                            etEmailLogin.text.toString(),
                            etSenha.text.toString()
                        )
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    startActivity(Intent(this, MainActivity::class.java))
                                } else {
                                    Snackbar.make(
                                        btnEntrar,
                                        "E-mail ou senha incorretos",
                                        Snackbar.LENGTH_LONG
                                    ).show()
                                }

                            }

                        hideKeyboard(it)
                    }
            }


        btnRecuperarSenha.setOnClickListener{
            val intent = Intent(this, RecuperarSenhaActivity::class.java)
            startActivity(intent)
        }
    }
}
