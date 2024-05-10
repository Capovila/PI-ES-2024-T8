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
            binding.etAge,
            binding.etCPF,
            binding.etPhone
        )


        val textLayouts = listOf(
            binding.tlName,
            binding.tlEmail,
            binding.tlPassword,
            binding.tlAge,
            binding.tlCPF,
            binding.tlPhone
        )

        val c = Calendar.getInstance()


        binding.btnData.setOnClickListener{
            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
                binding.etAge.setText(" $mDay/ $mMonth / $mYear")
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))

            dpd.show()
        }

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
            if(isFilled()) {
                val user = User(
                    null,
                    binding.etName.text.toString(),
                    binding.etEmail.text.toString(),
                    binding.etPassword.text.toString(),
                    binding.etAge.text.toString().toInt(),
                    binding.etCPF.text.toString(),
                    binding.etPhone.text.toString()
                )

                auth.createUserWithEmailAndPassword(user.email!!, user.password!!)
                    .addOnCompleteListener { authResult ->
                        if(authResult.isSuccessful) {
                            user.uid = authResult.result.user!!.uid
                            user.password = ""

                            db.collection("users")
                                .add(user)
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
                            Snackbar.make(
                                binding.btnCadastrar,
                                authResult.exception!!.message.toString(),
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                    }
                hideKeybard(it)
            } else {
                showFieldErrors()
                Snackbar.make(
                    binding.btnCadastrar,
                    "Preencha todos os campos corretamente",
                    Snackbar.LENGTH_LONG
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
                || binding.etAge.text.toString().isEmpty()
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
            binding.etAge,
            binding.etCPF,
            binding.etPhone
        )

        val textLayouts = listOf(
            binding.tlName,
            binding.tlEmail,
            binding.tlPassword,
            binding.tlAge,
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