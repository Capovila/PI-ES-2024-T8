package br.com.projetopi.smartlock.Fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.marginTop
import androidx.core.view.updateLayoutParams
import br.com.projetopi.smartlock.Classes.User
import br.com.projetopi.smartlock.MainActivity
import br.com.projetopi.smartlock.R
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

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLocacoesBinding.inflate(inflater, container, false)

        simpleStorage = SimpleStorage(requireContext())

        val user: User = simpleStorage.getUserAccountData()

        db = Firebase.firestore

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
                    val bitMatrix = multiFormatWriter.encode(
                        rentalID,
                        BarcodeFormat.QR_CODE,
                        300,
                        300
                    )
                    val barcodeEncoder = BarcodeEncoder()
                    val bitmap = barcodeEncoder.createBitmap(bitMatrix)
                    binding.qrcode.setImageBitmap(bitmap)
                    db.collection("establishments")
                        .document(establishmentID)
                        .get()
                        .addOnSuccessListener { document ->
                            val managerName = document.getString("managerName")
                            val layoutParams = binding.tvInfo2.layoutParams as ViewGroup.MarginLayoutParams
                            layoutParams.topMargin = 50
                            binding.main.setBackgroundResource(R.color.main_dark_blue)
                            binding.tvInfo2.text = "Locação aberta"
                            binding.tvInfo2.layoutParams = layoutParams
                            val whiteColor = ContextCompat.getColor(requireContext(), R.color.white)
                            binding.tvInfo2.setTextColor(whiteColor)
                            binding.qrcode.visibility = View.VISIBLE
                            binding.btnCancelar.visibility = View.VISIBLE
                            binding.tvInfo.text = "Apresente esse QR Code para o gerente $managerName caso você ainda não tenha efetivado sua locação"
                        }
                }
            }

        binding.btnCancelar.setOnClickListener {

            db.collection("rentals")
                .whereEqualTo("idUser", user.uid.toString())
                .get()
                .addOnSuccessListener{
                    var implemented: Boolean = false
                    for(documents in it){
                        implemented = documents!!.getBoolean("rentalImplemented") == true
                    }

                    if(implemented == true){
                        Toast.makeText(requireContext(), "Locação implementada não pode ser finalizada", Toast.LENGTH_LONG).show()
                    }else{
                        db.collection("lockers")
                            .whereEqualTo("idEstablishment", establishmentID)
                            .get()
                            .addOnSuccessListener {
                                val newRentalState = hashMapOf(
                                    "isRented" to false,
                                    "currentIdRental" to ""
                                )
                                for (document in it.documents) {
                                    document.reference.update(newRentalState as Map<String, Any>)
                                }
                            }

                        Toast.makeText(requireContext(), "Locação cancelada com sucesso", Toast.LENGTH_LONG).show()
                        for (document in it.documents) {
                            document.reference.delete()
                        }
                        startActivity(Intent(requireContext(), MainActivity::class.java))
                    }
                }
        }

        return binding.root
    }


}