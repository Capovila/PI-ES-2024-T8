package br.com.projetopi.smartlock

import android.content.Intent
import android.os.Bundle
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
class CadastrarActivity : AppCompatActivity() {

    private lateinit var btnCadastrar: Button
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
    private fun validarCampo(texto: TextView, campo:EditText){
        if(campo.text.toString().isEmpty()){
            texto.setTextColor(getColor(R.color.red))
        }
        else{
            texto.setTextColor(getColor(R.color.black))
        }
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


        tvEmailCadastro = findViewById(R.id.tvEmailCadastro)
        tvBirthCadastro = findViewById(R.id.tvBirthCadastro)
        tvtelCadastro = findViewById(R.id.tvTelCadastro)
        tvSenhaCadastro = findViewById(R.id.tvSenhaCadastro)
        tvNomeCadastro = findViewById(R.id.tvNomeCadastro)
        tvCPFCadastro = findViewById(R.id.tvCPFCadastro)

        emailCadastrar = findViewById(R.id.etEmail)
        nomeCadastrar = findViewById(R.id.etName)
        btnCadastrar = findViewById(R.id.btnCadastrarUser)
        senhaCadastrar = findViewById(R.id.etPassword)
        birthCadastrar = findViewById(R.id.etBrith)
        cpfCadastrar = findViewById(R.id.etCPF)
        telCadastrar = findViewById(R.id.etTel)


        btnCadastrar.setOnClickListener{
            if(nomeCadastrar.text.toString().isEmpty()||
               emailCadastrar.text.toString().isEmpty()||
               senhaCadastrar.text.toString().isEmpty()||
               birthCadastrar.text.toString().isEmpty()||
               cpfCadastrar.text.toString().isEmpty()||
               telCadastrar.text.toString().isEmpty())
            {
                var camposVazio: String = "Preencha todos os campos corretamente"
                Snackbar.make(btnCadastrar, camposVazio, Snackbar.LENGTH_LONG ).show()
                validarCampo(tvEmailCadastro, emailCadastrar)
                validarCampo(tvNomeCadastro, nomeCadastrar)
                validarCampo(tvSenhaCadastro, senhaCadastrar)
                validarCampo(tvBirthCadastro, birthCadastrar)
                validarCampo(tvCPFCadastro, cpfCadastrar)
                validarCampo(tvtelCadastro, telCadastrar)
            }else
            {
                hideKeybard(it)
                var intent = Intent(this, FirstScreenActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}