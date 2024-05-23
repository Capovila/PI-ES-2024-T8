package br.com.projetopi.smartlock

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.projetopi.smartlock.databinding.ActivityAccessLockerBinding
import br.com.projetopi.smartlock.databinding.ActivityLockerDataBinding

class AccessLockerActivity : AppCompatActivity(), NfcAdapter.ReaderCallback {

    private lateinit var binding: ActivityAccessLockerBinding
    private lateinit var nfcAdapter: NfcAdapter
    private lateinit var imageRecordByte: ByteArray
    private lateinit var imagePath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccessLockerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        binding.btnBack.setOnClickListener{
            finish()
        }

        binding.btnAcessar.setOnClickListener{
            val intent = Intent(this, ConfirmUserActivity::class.java)
            intent.putExtra("Image", imagePath)
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        val options = Bundle()

        options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 250)

        nfcAdapter.enableReaderMode(
            this,
            this,
            NfcAdapter.FLAG_READER_NFC_A or
                    NfcAdapter.FLAG_READER_NFC_B or
                    NfcAdapter.FLAG_READER_NFC_F or
                    NfcAdapter.FLAG_READER_NFC_V,
            options
        )
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter.disableReaderMode(this);
    }

    override fun onTagDiscovered(tag: Tag?) {
        val ndef = Ndef.get(tag)
        if(ndef != null){
            try{
                ndef.connect()
                imageRecordByte = ndef.ndefMessage.records[0].payload
                imagePath = String(imageRecordByte, Charsets.UTF_8)

                runOnUiThread{
                    binding.btnAcessar.visibility = View.VISIBLE
                }
            }finally {
                ndef.close()
            }

        }
    }
}