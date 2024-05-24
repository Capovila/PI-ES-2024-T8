package br.com.projetopi.smartlock.ManagerActivities

import android.annotation.SuppressLint
import android.icu.util.Calendar
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.projetopi.smartlock.Classes.User
import br.com.projetopi.smartlock.R
import br.com.projetopi.smartlock.SimpleStorage
import br.com.projetopi.smartlock.databinding.ActivityClearNfcBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.io.File

@SuppressLint("RestrictedApi")
class ClearNfcActivity : AppCompatActivity(), NfcAdapter.ReaderCallback {

    private lateinit var binding: ActivityClearNfcBinding
    private lateinit var nfcAdapter: NfcAdapter
    private lateinit var imagePath: String
    private lateinit var db: FirebaseFirestore
    private lateinit var simpleStorage: SimpleStorage
    private lateinit var user: User
    private lateinit var imageRecordByte: ByteArray
    private lateinit var qrCodeId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClearNfcBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        qrCodeId = intent.getStringExtra("qrCodeId").toString()

        db = Firebase.firestore
        simpleStorage = SimpleStorage(this)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        user = simpleStorage.getUserAccountData()

        binding.btnBack.setOnClickListener{
            finish()
        }

        binding.btnFinalizar.setOnClickListener{
            db.collection("rentals")
                .document(qrCodeId!!)
                .get()
                .addOnSuccessListener {document->
                    var localId = ""
                        localId = document.getString("idPlace").toString()
                        document.reference.delete()

                    db.collection("lockers")
                        .whereEqualTo("idEstablishment", localId)
                        .get()
                        .addOnSuccessListener {
                            val lockerUpdate = hashMapOf(
                                "currentIdRental" to "",
                                "isRented" to false,
                            )

                            for(documents in it){
                                documents.reference.update(lockerUpdate as Map<String, Any>)
                            }
                        }
                }

                Toast.makeText(this, "Locação encerrada", Toast.LENGTH_LONG).show()
            finish()
        }

    }

    override fun onResume() {
        super.onResume()

        val options = Bundle()

        // Adicionar um delay para ler a Tag corretamente
        options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 250)

        // Habilitar o modo leitura da Tag NFC
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
        nfcAdapter.disableReaderMode(this)
    }

    override fun onTagDiscovered(tag: Tag?) {
        val ndef = Ndef.get(tag)
        if (ndef != null) {
            try {
                ndef.connect()

                imageRecordByte = ndef.ndefMessage.records[0].payload
                imagePath = String(imageRecordByte, Charsets.UTF_8)

                imagePath?.let { path->
                    if(File(path).exists()){
                        File(path).delete()
                    }
                }

                val message = NdefRecord.createMime("text/plain", "".toByteArray() )
                val arr = NdefRecord.createApplicationRecord(this.packageName)
                val ndefMessage = NdefMessage(arrayOf(message, arr))

                if(ndef.isWritable){
                    ndef.writeNdefMessage(ndefMessage)
                }

                db.collection("rentals")
                    .document(qrCodeId)
                    .get()
                    .addOnSuccessListener {
                        val time = it.getDouble("hourCurrent")?.toInt()
                        val price = it.getDouble("hourPrice")?.toInt()
                        var result = (getHour() - time!!) + 1


                        runOnUiThread {
                            Toast.makeText(this, "NFC Limpa", Toast.LENGTH_SHORT).show()
                            binding.btnFinalizar.visibility = View.VISIBLE
                            binding.tvTotal.setText("Valor da locação: R$${result * price!!}")
                        }
                    }
            } finally {
                ndef.close()
            }
        }
    }

    private fun getHour():Int{
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    }
}