package br.com.projetopi.smartlock.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.projetopi.smartlock.Classes.User
import br.com.projetopi.smartlock.SimpleStorage
import br.com.projetopi.smartlock.databinding.FragmentLocacoesBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder

class Locacoes : Fragment() {

    private var _binding:FragmentLocacoesBinding? = null
    private val binding get() = _binding!!
    private lateinit var simpleStorage: SimpleStorage
    private lateinit var db: FirebaseFirestore
    private lateinit var rentalID: String
    private lateinit var establishmentID: String
    private lateinit var establishmentManagerName: String

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLocacoesBinding.inflate(inflater, container, false)

        simpleStorage = SimpleStorage(requireContext())

        val user: User = simpleStorage.getUserAccountData()

        db = Firebase.firestore

        // Esconde o ImageView do QR Code
        binding.qrcode.visibility = View.GONE

        /***
         * Busca locações que estejam relacinadas ao id do Usuario e se a locação está aberta,
         * caso ache alguma, pega o id da locação o id do estabelecimento, faz um bitmap com o
         * id do estabelecimento e após buscar o nome do gerente do estabelecimento dessa locação,
         * define o textView com o nome do gerente buscado
         */
        db.collection("rentals")
            .whereEqualTo("idUser", user.uid)
            .whereEqualTo("rentalOpen", true)
            .get()
            .addOnSuccessListener {
                for (documents in it) {
                    rentalID = documents.id
                    establishmentID = documents.getString("idPlace").toString()
                    val multiFormatWriter = MultiFormatWriter()
                    val bitMatrix = multiFormatWriter.encode(rentalID, BarcodeFormat.QR_CODE, 300, 300)
                    val barcodeEncoder = BarcodeEncoder()
                    val bitmap = barcodeEncoder.createBitmap(bitMatrix)
                    binding.qrcode.visibility = View.VISIBLE
                    binding.qrcode.setImageBitmap(bitmap)
                    db.collection("establishments").document(establishmentID).get().addOnSuccessListener { document ->
                        establishmentManagerName = document.getString("managerName").toString()
                        binding.tvInfo.text = "Apresente esse QR Code ao gerente $establishmentManagerName"
                    }
                }
            }
        return binding.root
    }
}