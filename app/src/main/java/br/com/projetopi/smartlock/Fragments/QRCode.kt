package br.com.projetopi.smartlock.Fragments

import SharedViewModelRental
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import br.com.projetopi.smartlock.Classes.Rental
import androidx.fragment.app.activityViewModels
import br.com.projetopi.smartlock.MainActivity
import br.com.projetopi.smartlock.R
import br.com.projetopi.smartlock.databinding.FragmentOpcaoTempoBinding
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

    private lateinit var rentalID: String

    private lateinit var db: FirebaseFirestore

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentQRCodeBinding.inflate(inflater,container,false)

        db = Firebase.firestore

        val sharedViewModelRental: SharedViewModelRental by activityViewModels()
        sharedViewModelRental.selectedRental.observe(viewLifecycleOwner) { rental ->
            rentalID = rental.uid.toString()
            binding.tvApresenteGerente.text = "Apresente esse QR Code ao gerente "
            val multiFormatWriter = MultiFormatWriter()
            val bitMatrix = multiFormatWriter.encode(rentalID, BarcodeFormat.QR_CODE, 300, 300)
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.createBitmap(bitMatrix)

            binding.qrcode.setImageBitmap(bitmap)

            binding.btnConcluir.setOnClickListener {
                (activity as MainActivity).changeFragment(Mapa())
            }
        }
        return binding.root
    }
}