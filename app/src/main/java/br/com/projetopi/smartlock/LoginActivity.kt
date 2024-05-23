package br.com.projetopi.smartlock

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
import br.com.projetopi.smartlock.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var simpleStorage: SimpleStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        setupWindowInsets()

        simpleStorage = SimpleStorage(this)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        setupInputValidation()
        setupClickListeners()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupInputValidation() {
        val editTexts = listOf(binding.etEmail, binding.etPassword)
        val textLayouts = listOf(binding.tlEmail, binding.tlPassword)

        editTexts.forEachIndexed { index, editText ->
            editText.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus && editText.text.isNullOrEmpty()) {
                    textLayouts[index].error = getString(R.string.preencha_campo)
                } else {
                    textLayouts[index].error = null
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            btnEntrar.setOnClickListener { handleLogin(it) }
            btnRecuperarSenha.setOnClickListener { navigateTo(RecuperarSenhaActivity::class.java) }
            btnCadastrar.setOnClickListener { navigateTo(CadastrarActivity::class.java) }
            btnConferir.setOnClickListener { navigateTo(ConsultarMapaActivity::class.java) }
        }
    }

    private fun handleLogin(view: View) {
        if (isInputValid()) {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        handleSuccessfulLogin()
                    } else {
                        showToast(R.string.invalid_credentials)
                    }
                }
            hideKeyboard(view)
        } else {
            showFieldErrors()
            showToast(R.string.fill_all_fields)
        }
    }

    private fun handleSuccessfulLogin() {
        auth.currentUser?.let { user ->
            if (user.isEmailVerified) {
                fetchUserData(user.uid)
            } else {
                showToast(R.string.verify_email)
            }
        }
    }

    private fun fetchUserData(uid: String) {
        db.collection("users").whereEqualTo("uid", uid).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val user = document.toObject(User::class.java).apply {
                        this.uid = uid
                    }
                    simpleStorage.storageUserAccount(user)
                    navigateTo(if (user.manager == true) ManagerMainActivity::class.java else MainActivity::class.java)
                    finish()
                }
            }
            .addOnFailureListener {
                showToast(R.string.login_failed)
            }
    }

    private fun isInputValid(): Boolean {
        return binding.etEmail.text.isNotEmpty() && binding.etPassword.text.isNotEmpty()
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showFieldErrors() {
        val editTexts = listOf(binding.etEmail, binding.etPassword)
        val textLayouts = listOf(binding.tlEmail, binding.tlPassword)

        editTexts.forEachIndexed { index, editText ->
            if (editText.text.isNullOrEmpty()) {
                textLayouts[index].error = getString(R.string.preencha_campo)
            } else {
                textLayouts[index].error = null
            }
        }
    }

    private fun showToast(messageResId: Int) {
        Toast.makeText(this, messageResId, Toast.LENGTH_LONG).show()
    }

    private fun navigateTo(activityClass: Class<*>) {
        startActivity(Intent(this, activityClass))
    }
}
