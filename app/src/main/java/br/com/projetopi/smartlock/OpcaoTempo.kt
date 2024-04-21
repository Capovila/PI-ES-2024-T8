package br.com.projetopi.smartlock

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

    private lateinit var placeID: String

    private lateinit var db: FirebaseFirestore

    private lateinit var simpleStorage: SimpleStorage

    private lateinit var radioGroup: RadioGroup
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_opcao_tempo, container, false)

        btnConfirmarLocacao = root.findViewById(R.id.btnConfirmarLocacao)
        btnBack = root.findViewById(R.id.btnBack)
        op1 = root.findViewById(R.id.op1)
        op2 = root.findViewById(R.id.op2)
        op3 = root.findViewById(R.id.op3)
        op4 = root.findViewById(R.id.op4)
        op5 = root.findViewById(R.id.op5)
        db = Firebase.firestore
        radioGroup = root.findViewById(R.id.radioGroup)

        simpleStorage = SimpleStorage(requireContext())

        val user: User = simpleStorage.getUserAccountData()

        //val sharedViewModelPlace: SharedViewModelPlace by activityViewModels()
        //sharedViewModelPlace.selectedPlace.observe(viewLifecycleOwner) { place ->
            //placeID = place.uid
            //Toast.makeText(requireContext(), placeID, Toast.LENGTH_LONG).show()
        //}

        btnBack.setOnClickListener{
            (activity as MainActivity).changeFragment(Mapa())
        }

        btnConfirmarLocacao.setOnClickListener {
            if(isSelected()){
                val locacaoAtual: Locacao = Locacao (
                    "172381729",
                    user.uid,
                    placeID,
                    null
                )
                db.collection("locacoes").add(locacaoAtual).addOnSuccessListener {
                    //sharedViewModelLocacao.selectLocacao(locacaoAtual)
                    Toast.makeText(
                        requireContext(),
                        "Locação realizada com sucesso",
                        Toast.LENGTH_LONG,
                    ).show()
                    (activity as MainActivity).changeFragment(QRCode())
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Selecione pelo menos uma das opções!",
                    Toast.LENGTH_LONG,
                ).show()
            }
        }

        return root
    }

    private fun isSelected(): Boolean {
        return op1.isChecked || op2.isChecked || op3.isChecked || op4.isChecked || op5.isChecked
    }
}