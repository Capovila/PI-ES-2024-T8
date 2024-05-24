package br.com.projetopi.smartlock.ManagerActivities

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.projetopi.smartlock.R
import br.com.projetopi.smartlock.databinding.ActivityAccessLockerBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class AccessLockerActivity : AppCompatActivity(), NfcAdapter.ReaderCallback {

    private lateinit var binding: ActivityAccessLockerBinding
    private lateinit var nfcAdapter: NfcAdapter
    private lateinit var imageRecordByte: ByteArray
    private lateinit var imagePath: String
    private  var qrCode: String? = null
    private lateinit var db: FirebaseFirestore

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
        db = Firebase.firestore


        binding.btnBack.setOnClickListener{
            finish()
        }

        binding.btnAcessar.setOnClickListener{
            val intent = Intent(this, ConfirmUserActivity::class.java)
            intent.putExtra("Image", imagePath)
            intent.putExtra("qrCodeId", qrCode)
            startActivity(intent)
            finish()
        }
    }

    /*Função on resume para, ao ler uma tag nfc, manter o foco no aplicativo e não abrir o app do android*/
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

    /*onPause para parar a leitura da nfc quando a Activity perde o foco*/
    override fun onPause() {
        super.onPause()
        nfcAdapter.disableReaderMode(this);
    }


    /*onDiscover da tag nfc mas para leitura da nfc, le a nfc e verifica se existe algum registro no banco com aquele usuário,
    * caso consiga ler, habilita para o usuário continuar e manda o resultado das requisições para a próxima activity*/
    override fun onTagDiscovered(tag: Tag?) {
        val ndef = Ndef.get(tag)
        if(ndef != null){
            try{
                ndef.connect()
                imageRecordByte = ndef.ndefMessage.records[0].payload
                imagePath = String(imageRecordByte, Charsets.UTF_8)

                db.collection("rentals")
                    .whereEqualTo("user1Photo", imagePath)
                    .get()
                    .addOnSuccessListener {
                        for(documents in it){
                            qrCode = documents.id
                        }
                    }

                db.collection("rentals")
                    .whereEqualTo("user2Photo", imagePath)
                    .get()
                    .addOnSuccessListener {
                        for(documents in it){
                            qrCode = documents.id
                        }
                    }

                runOnUiThread{
                    binding.btnAcessar.visibility = View.VISIBLE
                }
            }finally {
                ndef.close()
            }

        }
    }
}