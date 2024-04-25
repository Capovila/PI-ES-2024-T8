package br.com.projetopi.smartlock

import SharedViewModelRental
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder

class QRCode : Fragment() {
    private lateinit var qrCode: ImageView
    private lateinit var btnConcluir: Button
    private lateinit var tvApresenteGerente: TextView

    private lateinit var rentalID: String
    private lateinit var establishmentmanagerName: String

    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_q_r_code, container, false)

        qrCode = root.findViewById(R.id.qrcode)
        btnConcluir = root.findViewById(R.id.btnConcluir)
        tvApresenteGerente = root.findViewById(R.id.tvApresenteGerente)

        db = Firebase.firestore

        val sharedViewModelRental: SharedViewModelRental by activityViewModels()
        sharedViewModelRental.selectedRental.observe(viewLifecycleOwner) { rental ->
            rentalID = rental.uid.toString()
            establishmentmanagerName = rental.establishmentManagerName.toString()
            tvApresenteGerente.text = "Apresente esse QR Code ao gerente $establishmentmanagerName"
            val multiFormatWriter = MultiFormatWriter()
            val bitMatrix = multiFormatWriter.encode("$rentalID", BarcodeFormat.QR_CODE, 300, 300)
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.createBitmap(bitMatrix)

            qrCode.setImageBitmap(bitmap)

            btnConcluir.setOnClickListener {
                val newRentalState = hashMapOf(
                    "rentalImplemented" to true
                )
                db.collection("rentals").document(rentalID).update(newRentalState as Map<String, Any>)
                (activity as MainActivity).changeFragment(Mapa())
            }
        }
        return root
    }
}