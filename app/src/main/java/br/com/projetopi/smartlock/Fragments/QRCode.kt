package br.com.projetopi.smartlock.Fragments

import SharedViewModelRental
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import br.com.projetopi.smartlock.MainActivity
import br.com.projetopi.smartlock.databinding.FragmentQRCodeBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder

class QRCode : Fragment() {

    private var _binding: FragmentQRCodeBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentQRCodeBinding.inflate(inflater,container,false)

        db = Firebase.firestore

        /***
         * Pega as informações vindas do sharedViewModelRental, define o textView com a mensagem instruindo
         * o usuario a apresentar o qr code ao gerente do estabelecimento, cria um bitmap com o QR Code do id da locação com o tamanho de 300x300px
         * e define esse bitmap no imageView qrcode
         */
        val sharedViewModelRental: SharedViewModelRental by activityViewModels()
        sharedViewModelRental.selectedRental.observe(viewLifecycleOwner) { rental ->
            val rentalID = rental.uid.toString()

            Toast.makeText(
                requireContext(),
                rentalID,
                Toast.LENGTH_LONG
            ).show()

            db.collection("rentals")
                .whereEqualTo("idUser", rentalID)
                .get()
                .addOnSuccessListener { document ->
                    var managerId: String? = null
                    for (documents in document){
                        managerId = documents.getString("managerId")
                    }

                            db.collection("users")
                                .document(managerId!!)
                                .get()
                                .addOnSuccessListener { document ->
                                    val establishmentManagerName = document.getString("name").toString()
                                    binding.tvApresenteGerente.text = "Apresente esse QR Code para o(a) gerente $establishmentManagerName para que ele possa efetivar sua locação"
                                    val multiFormatWriter = MultiFormatWriter()
                                    val bitMatrix = multiFormatWriter.encode(rentalID, BarcodeFormat.QR_CODE, 300, 300)
                                    val barcodeEncoder = BarcodeEncoder()
                                    val bitmap = barcodeEncoder.createBitmap(bitMatrix)

                                    binding.qrcode.setImageBitmap(bitmap)
                                }

                }

            /***
             * Quando o btnConcluir é clicado, muda o fragmento exibido na main activity
             * para o fragmento Mapa
             */
            binding.btnConcluir.setOnClickListener {
                (activity as MainActivity).changeFragment(Mapa())
            }
        }
        return binding.root
    }
}