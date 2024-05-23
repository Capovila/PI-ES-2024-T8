package br.com.projetopi.smartlock

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.projetopi.smartlock.R
import br.com.projetopi.smartlock.databinding.ActivityLockerOptionsBinding

class LockerOptionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLockerOptionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLockerOptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnEnd.setOnClickListener{
            startActivity(Intent(this, ClearNfcActivity::class.java))
            finish()
        }

        binding.btnBack.setOnClickListener{
            finish()
        }

        binding.btnOpen.setOnClickListener{
            Toast.makeText(this, "Armário liberado", Toast.LENGTH_LONG).show()
        }
    }
}