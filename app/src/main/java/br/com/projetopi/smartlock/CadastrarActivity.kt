package br.com.projetopi.smartlock

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
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
    private lateinit var cbGerente: CheckBox
    private lateinit var cbCliente: CheckBox
    //Colocar os outros campos dps

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

        btnCadastrar = findViewById(R.id.btnCadastrarUser)
        nomeCadastrar = findViewById(R.id.etName)
        emailCadastrar = findViewById(R.id.etEmail)
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
               telCadastrar.text.toString().isEmpty()||
               !cbGerente.isChecked && !cbCliente.isChecked ){
                var camposVazio: String = "Preencha todos os campos"
                Snackbar.make(btnCadastrar, camposVazio, Snackbar.LENGTH_LONG ).show()
            }else{
                hideKeybard(it)
                var intent = Intent(this, FirstScreenActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}