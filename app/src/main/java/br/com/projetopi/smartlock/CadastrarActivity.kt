package br.com.projetopi.smartlock

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.projetopi.smartlock.Classes.User
import br.com.projetopi.smartlock.databinding.ActivityCadastrarBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class CadastrarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastrarBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var simpleStorage: SimpleStorage

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityCadastrarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        simpleStorage = SimpleStorage(this)

        db = Firebase.firestore
        auth = Firebase.auth


        val editTexts = listOf(
            binding.etName,
            binding.etEmail,
            binding.etPassword,
            binding.etYear,
            binding.etMonth,
            binding.etDay,
            binding.etCPF,
            binding.etPhone
        )


        val textLayouts = listOf(
            binding.tlName,
            binding.tlEmail,
            binding.tlPassword,
            binding.tlDay,
            binding.tlMonth,
            binding.tlYear,
            binding.tlCPF,
            binding.tlPhone
        )

        val c = Calendar.getInstance()


        /***
         * Faz com que para cada editText junto com seu textLayout
         * execute a função setOnFocusChangeListenerInputCheck
         */
        editTexts.forEachIndexed { lt, et ->
            setOnFocusChangeListenerInputCheck(et, textLayouts[lt])
        }

        /***
         * Faz com que quando clicado o btnCadastrar, verifica se todos os
         * campos foram preenchidos, caso True atribui à variavel user os
         * dados do usuario vindos do editText's, cria um usuario no auth com
         * o email e senha, caso seja criado com sucesso, adiciona um documento
         * com a variavel user na coleção users, caso seja adicionado com
         * sucesso, envia um email de verificação para o email do usuario
         * mostrando uma Toast com a mensagem de que foi enviado a confirmaçao
         * de email, após inicia a activity LoginActivity e finaliza a activity
         * atual escondendo o teclado
         */
        binding.btnCadastrar.setOnClickListener{ it ->
            if(isFilled() && dateValid()) {

                val birth: String = "${binding.etDay.text.toString()} / ${binding.etMonth.text.toString()} / ${binding.etYear.text.toString()}"

                val user = User(
                    null,
                    binding.etName.text.toString(),
                    binding.etEmail.text.toString(),
                    binding.etPassword.text.toString(),
                    birth,
                    binding.etCPF.text.toString(),
                    binding.etPhone.text.toString()
                )

                auth.createUserWithEmailAndPassword(user.email!!, user.password!!)
                    .addOnCompleteListener { authResult ->
                        if(authResult.isSuccessful) {
                            user.uid = authResult.result.user!!.uid
                            user.password = ""

                            db.collection("users")
                                .document(user.uid.toString())
                                .set(user)
                                .addOnSuccessListener {
                                    auth.currentUser?.sendEmailVerification()
                                        ?.addOnCompleteListener {
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
                            Toast.makeText(
                                this,
                                "E-mail já cadastrado",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                hideKeybard(it)
            } else {
                showFieldErrors()
                Toast.makeText(
                    this,
                    "Preencha todos os campos corretamente",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        /***
         * Faz com que quando clicado o btnBack, fecha a activity
         * CadastrarActivity, activity atual
         */
        binding.btnBack.setOnClickListener{
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

    private fun dateValid():Boolean{

        return if(binding.etYear.text.toString().toInt() > Calendar.getInstance().get(Calendar.YEAR)){
            false
        } else if(Calendar.getInstance().get(Calendar.YEAR) - binding.etYear.text.toString().toInt() < 18){
            Toast.makeText(this, "Apenas maiores de 18 anos", Toast.LENGTH_LONG).show()
            false
        }else if(binding.etDay.text.toString().toInt() > 31){
            false
        }else if(binding.etMonth.text.toString().toInt() > 12){
            false
        }else{
            true
        }
    }

    /***
     * Faz com que quando executada, retorna um boolean
     * True caso todos os editText tenham sido preenchidos, e False
     * caso um dos editText nao estiver preenchido
     */
    private fun isFilled(): Boolean {
        return !(
                binding.etName.text.toString().isEmpty()
                || binding.etEmail.text.toString().isEmpty()
                || binding.etPassword.text.toString().isEmpty()
                        || binding.etMonth.text.toString().isEmpty()
                        || binding.etDay.text.toString().isEmpty()
                        || binding.etYear.text.toString().isEmpty()
                        || binding.etCPF.text.toString().isEmpty()
                || binding.etPhone.text.toString().isEmpty()
                )
    }
    /***
     * Faz com que quando chamada, esconde o teclado do dispositivo
     */
    private fun hideKeybard(it: View) {
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
        val editTexts = listOf(
            binding.etName,
            binding.etEmail,
            binding.etPassword,
            binding.etDay,
            binding.etMonth,
            binding.etYear,
            binding.etCPF,
            binding.etPhone
        )

        val textLayouts = listOf(
            binding.tlName,
            binding.tlEmail,
            binding.tlPassword,
            binding.tlDay,
            binding.tlMonth,
            binding.tlYear,
            binding.tlCPF,
            binding.tlPhone
        )

        editTexts.forEachIndexed { index, et ->
            if (et.text.toString().isEmpty()) {
                textLayouts[index].error = getString(R.string.preencha_campo)
            } else {
                textLayouts[index].error = null
            }
        }
    }
}