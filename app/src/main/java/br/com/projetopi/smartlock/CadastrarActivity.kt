package br.com.projetopi.smartlock

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.projetopi.smartlock.Classes.User
import br.com.projetopi.smartlock.databinding.ActivityCadastrarBinding
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

        setupWindowInsets()
        initializeFirebase()
        initializeStorage()
        setupFocusChangeListeners()
        setupButtonListeners()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initializeFirebase() {
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }

    private fun initializeStorage() {
        simpleStorage = SimpleStorage(this)
    }

    private fun setupFocusChangeListeners() {
        val editTexts = listOf(
            binding.etName, binding.etEmail, binding.etPassword,
            binding.etYear, binding.etMonth, binding.etDay,
            binding.etCPF, binding.etPhone
        )

        val textLayouts = listOf(
            binding.tlName, binding.tlEmail, binding.tlPassword,
            binding.tlDay, binding.tlMonth, binding.tlYear,
            binding.tlCPF, binding.tlPhone
        )

        editTexts.forEachIndexed { index, editText ->
            setOnFocusChangeListenerInputCheck(editText, textLayouts[index])
        }
    }

    private fun setupButtonListeners() {
        binding.btnCadastrar.setOnClickListener {
            if (isFilled() && isDateValid()) {
                registerUser(it)
            } else {
                showFieldErrors()
                showToast(getString(R.string.preencha_todos_os_campos))
            }
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun registerUser(view: View) {
        val birthDate = "${binding.etDay.text}/${binding.etMonth.text}/${binding.etYear.text}"
        val user = User(
            uid = null,
            name = binding.etName.text.toString(),
            email = binding.etEmail.text.toString(),
            password = binding.etPassword.text.toString(),
            birthDate = birthDate,
            cpf = binding.etCPF.text.toString(),
            phone = binding.etPhone.text.toString()
        )

        auth.createUserWithEmailAndPassword(user.email!!, user.password!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    user.uid = task.result.user?.uid
                    user.password = ""
                    saveUserToDatabase(user)
                } else {
                    showToast(getString(R.string.email_ja_cadastrado))
                }
            }
        hideKeyboard(view)
    }

    private fun saveUserToDatabase(user: User) {
        db.collection("users")
            .document(user.uid!!)
            .set(user)
            .addOnSuccessListener {
                sendEmailVerification()
            }
    }

    private fun sendEmailVerification() {
        auth.currentUser?.sendEmailVerification()
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    showToast(getString(R.string.confirmacao_email_enviada))
                    navigateToLogin()
                }
            }
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun setOnFocusChangeListenerInputCheck(editText: TextInputEditText, textLayout: TextInputLayout) {
        editText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && editText.text.isNullOrEmpty()) {
                textLayout.error = getString(R.string.preencha_campo)
            } else {
                textLayout.error = null
            }
        }
    }

    private fun isDateValid(): Boolean {
        val year = binding.etYear.text.toString().toIntOrNull() ?: return false
        val month = binding.etMonth.text.toString().toIntOrNull() ?: return false
        val day = binding.etDay.text.toString().toIntOrNull() ?: return false

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        if (year > currentYear || currentYear - year < 18) {
            showToast(getString(R.string.maiores_de_18))
            return false
        }
        if (day !in 1..31 || month !in 1..12) {
            return false
        }
        return true
    }

    private fun isFilled(): Boolean {
        return listOf(
            binding.etName, binding.etEmail, binding.etPassword,
            binding.etYear, binding.etMonth, binding.etDay,
            binding.etCPF, binding.etPhone
        ).all { it.text?.isNotEmpty() == true }
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showFieldErrors() {
        val editTexts = listOf(
            binding.etName, binding.etEmail, binding.etPassword,
            binding.etDay, binding.etMonth, binding.etYear,
            binding.etCPF, binding.etPhone
        )

        val textLayouts = listOf(
            binding.tlName, binding.tlEmail, binding.tlPassword,
            binding.tlDay, binding.tlMonth, binding.tlYear,
            binding.tlCPF, binding.tlPhone
        )

        editTexts.forEachIndexed { index, editText ->
            textLayouts[index].error = if (editText.text.isNullOrEmpty()) {
                getString(R.string.preencha_campo)
            } else {
                null
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
