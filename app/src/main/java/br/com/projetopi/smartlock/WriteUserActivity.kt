package br.com.projetopi.smartlock

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.projetopi.smartlock.Classes.User
import br.com.projetopi.smartlock.databinding.ActivityWriteUserBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.io.File

class WriteUserActivity : AppCompatActivity(), NfcAdapter.ReaderCallback {

    private lateinit var binding: ActivityWriteUserBinding
    private lateinit var nfcAdapter: NfcAdapter
    private lateinit var pathBytes: ByteArray
    private var imagePath: String? = null
    private lateinit var simpleStorage: SimpleStorage
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWriteUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val uNumber = intent.getStringExtra("nUser").toString().toInt()
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        imagePath = intent.getStringExtra("Image").toString()
        db = Firebase.firestore
        simpleStorage = SimpleStorage(this)
        val user = simpleStorage.getUserAccountData()

        binding.btnBack.setOnClickListener{
            finish()
        }


        db.collection("rentals")
            .whereEqualTo("idUser", user.uid.toString())
            .get()
            .addOnSuccessListener {documents ->
                val rentalImplemented = hashMapOf(
                    "rentalImplemented" to true
                )
                var number: Double? = null
                for (document in documents){
                    number = document.getDouble("usersQuantity")
                }
                binding.btnMais.setOnClickListener{
                    if(number!!.toInt() == uNumber){
                        Toast.makeText(this, "Todos os usuários foram cadastrados", Toast.LENGTH_SHORT).show()
                    }else{
                        val intent = Intent(this, UserPhotoActivity::class.java)
                        intent.putExtra("nUser", "${uNumber+1}")
                        startActivity(intent)
                    }
                }
                binding.btnFim.setOnClickListener{
                    if(number!!.toInt() != uNumber){
                        Toast.makeText(this, "Existem usuários a serem cadastrados", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this, "Locação efetivada", Toast.LENGTH_SHORT).show()
                        for(document in documents){
                            document.reference.update(rentalImplemented as Map<String, Any>)
                        }
                    }
                }
            }

        imagePath?.let { path ->
            // Verificar se o arquivo existe
            if (File(path).exists()) {
                // Decodificar o arquivo de imagem em um Bitmap
                var bitmap = BitmapFactory.decodeFile(File(path).absolutePath)
                val matrix = Matrix()
                matrix.postRotate(90f)
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

                pathBytes = path.toByteArray(Charsets.UTF_8)

                // Definir o Bitmap no ImageView
                binding.userPhoto.setImageBitmap(bitmap)
            }
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

    override fun onTagDiscovered(tag: Tag?) {
        val ndef = Ndef.get(tag)

        if(ndef != null){
            try{
                ndef.connect()
                val message = NdefRecord.createMime("text/plain", imagePath?.toByteArray() )
                val arr = NdefRecord.createApplicationRecord(this.packageName)

                val ndefMessage = NdefMessage(arrayOf(message, arr))

                if(ndef.isWritable){
                    ndef.writeNdefMessage(ndefMessage)
                }

                runOnUiThread {
                    Toast.makeText(this, "Usuário salvo", Toast.LENGTH_SHORT).show()
                    binding.btnFim.visibility = View.VISIBLE
                    binding.btnMais.visibility = View.VISIBLE
                }
            }catch(e: Exception){
                Toast.makeText(this, "Falha ao salvar usuário", Toast.LENGTH_SHORT).show()
            }finally {
                ndef.close()


            }
        }
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter.disableReaderMode(this)
    }

}