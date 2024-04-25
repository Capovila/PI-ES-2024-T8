package br.com.projetopi.smartlock

import SharedViewModelEstablishment
import SharedViewModelRental
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class OpcaoTempo : Fragment() {

    private lateinit var btnConfirmarLocacao: Button
    private lateinit var btnBack: ImageView

    private lateinit var op1: RadioButton
    private lateinit var op2: RadioButton
    private lateinit var op3: RadioButton
    private lateinit var op4: RadioButton
    private lateinit var op5: RadioButton
    private lateinit var radioGroup: RadioGroup

    private lateinit var db: FirebaseFirestore

    private lateinit var establishmentID: String
    private lateinit var establishmentManagerName: String

    private lateinit var simpleStorage: SimpleStorage
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_opcao_tempo, container, false)

        simpleStorage = SimpleStorage(requireContext())

        val user: User = simpleStorage.getUserAccountData()

        btnConfirmarLocacao = root.findViewById(R.id.btnConfirmarLocacao)
        btnBack = root.findViewById(R.id.btnBack)

        op1 = root.findViewById(R.id.op1)
        op2 = root.findViewById(R.id.op2)
        op3 = root.findViewById(R.id.op3)
        op4 = root.findViewById(R.id.op4)
        op5 = root.findViewById(R.id.op5)
        radioGroup = root.findViewById(R.id.radioGroup)

        db = Firebase.firestore

        val sharedViewModelRental: SharedViewModelRental by activityViewModels()

        val sharedViewModelEstablishment: SharedViewModelEstablishment by activityViewModels()
        sharedViewModelEstablishment.selectedEstablishment.observe(viewLifecycleOwner) { establishment ->
            establishmentID = establishment.uid.toString()
            establishmentManagerName = establishment.managerName.toString()
            db.collection("establishments").document(establishmentID).get().addOnSuccessListener { document ->
                val preco1 = document.getDouble("planPrice1") ?: 0.0
                val desc1 = document.getString("planDescription1") ?: ""
                op1.text = "$desc1 = R$ $preco1"

                val preco2 = document.getDouble("planPrice2") ?: 0.0
                val desc2 = document.getString("planDescription2") ?: ""
                op2.text = "$desc2 = R$ $preco2"

                val preco3 = document.getDouble("planPrice3") ?: 0.0
                val desc3 = document.getString("planDescription3") ?: ""
                op3.text = "$desc3 = R$ $preco3"

                val preco4 = document.getDouble("planPrice4") ?: 0.0
                val desc4 = document.getString("planDescription4") ?: ""
                op4.text = "$desc4 = R$ $preco4"

                val currentTime = getCurrentTime()
                if (currentTime < "07:00:00" || currentTime > "08:00:00") {
                    op5.visibility = View.GONE
                } else {
                    val preco5 = document.getDouble("planPrice5") ?: 0.0
                    val desc5 = document.getString("planDescription5") ?: ""
                    op5.text = "$desc5 = R$ $preco5"
                }

                btnConfirmarLocacao.setOnClickListener {
                    if(isSelected()){
                        val opSelected: RadioButton = root.findViewById(radioGroup.checkedRadioButtonId)
                        var locacaoAtual = Rental(
                            null,
                            user.uid,
                            establishmentID,
                            opSelected.text as String,
                            false,
                            establishmentManagerName
                        )
                        db.collection("rentals").add(locacaoAtual).addOnSuccessListener {document ->
                            val locacaoAtualID = document.id
                            locacaoAtual.uid = locacaoAtualID
                            sharedViewModelRental.selectRental(locacaoAtual)
                            (activity as MainActivity).changeFragment(QRCode())
                        }
                    } else {
                        Toast.makeText(requireContext(), "Você precisa selecionar pelo menos um plano", Toast.LENGTH_LONG).show()
                    }
                }

                btnBack.setOnClickListener{
                    (activity as MainActivity).changeFragment(Mapa())
                }
            }
        }
        return root
    }

    private fun isSelected(): Boolean {
        return op1.isChecked || op2.isChecked || op3.isChecked || op4.isChecked || op5.isChecked
    }

    private fun getCurrentTime(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("HH:mm:ss")

        return dateFormat.format(calendar.time)
    }
}