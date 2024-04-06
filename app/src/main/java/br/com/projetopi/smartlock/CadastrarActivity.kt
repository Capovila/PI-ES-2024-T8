package br.com.projetopi.smartlock

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.PatternMatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class CadastrarActivity : AppCompatActivity() {

    private lateinit var btnCadastrar: Button
    private lateinit var btnSair: Button

    private lateinit var nomeCadastrar: EditText
    private lateinit var emailCadastrar: EditText
    private lateinit var senhaCadastrar: EditText
    private lateinit var birthCadastrar: EditText
    private lateinit var cpfCadastrar: EditText
    private lateinit var telCadastrar: EditText

    private lateinit var tvNomeCadastro: TextView
    private lateinit var tvEmailCadastro: TextView
    private lateinit var tvSenhaCadastro: TextView
    private lateinit var tvBirthCadastro: TextView
    private lateinit var tvCPFCadastro: TextView
    private lateinit var tvtelCadastro: TextView

    private lateinit var auth: FirebaseAuth

    val db = Firebase.firestore

    private fun validarCampo(texto: TextView, campo:EditText){
        if(campo.text.toString().isEmpty()){
            texto.setTextColor(getColor(R.color.red))
        }
        else{
            texto.setTextColor(getColor(R.color.black))
        }
    }

    private fun emailNotValid(): Boolean{
        if(Patterns.EMAIL_ADDRESS.matcher(emailCadastrar.text.toString()).matches()
            && emailCadastrar.text.toString().isNotEmpty()){
            return false
        }else{
            tvEmailCadastro.setTextColor(getColor(R.color.red))
            return true
        }
    }

    private fun isNotFulfileld(): Boolean{
        return  emailNotValid() ||
                nomeCadastrar.text.toString().isEmpty() ||
                senhaCadastrar.text.toString().isEmpty() ||
                birthCadastrar.text.toString().isEmpty() ||
                cpfCadastrar.text.toString().isEmpty() ||
                telCadastrar.text.toString().isEmpty()

    }

    private fun hideKeybard(it: View) {
        var imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(it.windowToken, 0)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastrar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = Firebase.auth

        tvEmailCadastro = findViewById(R.id.tvEmailCadastro)
        tvBirthCadastro = findViewById(R.id.tvBirthCadastro)
        tvtelCadastro = findViewById(R.id.tvTelCadastro)
        tvSenhaCadastro = findViewById(R.id.tvSenhaCadastro)
        tvNomeCadastro = findViewById(R.id.tvNomeCadastro)
        tvCPFCadastro = findViewById(R.id.tvCPFCadastro)

        btnCadastrar = findViewById(R.id.btnCadastrarUser)
        btnSair = findViewById(R.id.btnSairCadastro)

        emailCadastrar = findViewById(R.id.etEmail)
        nomeCadastrar = findViewById(R.id.etName)
        senhaCadastrar = findViewById(R.id.etPassword)
        birthCadastrar = findViewById(R.id.etBrith)
        cpfCadastrar = findViewById(R.id.etCPF)
        telCadastrar = findViewById(R.id.etTel)


        btnSair.setOnClickListener{
            finish()
        }


        btnCadastrar.setOnClickListener{
            validarCampo(tvEmailCadastro, emailCadastrar)
            validarCampo(tvNomeCadastro, nomeCadastrar)
            validarCampo(tvSenhaCadastro, senhaCadastrar)
            validarCampo(tvBirthCadastro, birthCadastrar)
            validarCampo(tvCPFCadastro, cpfCadastrar)
            validarCampo(tvtelCadastro, telCadastrar)

           if(isNotFulfileld())
            {
                Snackbar.make(btnCadastrar, "Preencha todos os campos corretamente", Snackbar.LENGTH_LONG ).show()
            }else
            {
                val user = hashMapOf(
                    "name" to nomeCadastrar.text.toString(),
                    "email" to emailCadastrar.text.toString(),
                    "Idade" to birthCadastrar.text.toString().toInt(),
                    "CPF" to cpfCadastrar.text.toString().toInt(),
                    "Telefone" to telCadastrar.text.toString().toInt()
                )

                db.collection("users").add(user)

                auth.createUserWithEmailAndPassword(emailCadastrar.text.toString(),
                    senhaCadastrar.text.toString())
                    .addOnCompleteListener {
                        if(it.isSuccessful){
                            auth.currentUser?.sendEmailVerification()?.addOnCompleteListener{
                                Snackbar.make(btnCadastrar, "Confirmação de e-mail enviada", Snackbar.LENGTH_LONG).show()
                            }
                        }
                        else{
                            Snackbar.make(btnCadastrar, it.exception!!.message.toString(), Snackbar.LENGTH_LONG).show()
                        }

                    }
                hideKeybard(it)

            }
        }
    }
}