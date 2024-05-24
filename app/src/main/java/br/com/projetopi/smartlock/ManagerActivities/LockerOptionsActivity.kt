package br.com.projetopi.smartlock.ManagerActivities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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

        val qrCodeId = intent.getStringExtra("qrCodeId")

        binding.btnEnd.setOnClickListener{
            val intent = Intent(this, ClearNfcActivity::class.java)
            intent.putExtra("qrCodeId", qrCodeId)
            startActivity(intent)
            finish()
        }

        binding.btnBack.setOnClickListener{
            finish()
        }

        binding.btnOpen.setOnClickListener{
            Toast.makeText(this, "Armário liberado", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}