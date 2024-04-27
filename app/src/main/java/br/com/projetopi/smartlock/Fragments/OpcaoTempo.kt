package br.com.projetopi.smartlock.Fragments

import SharedViewModelEstablishment
import SharedViewModelRental
import android.annotation.SuppressLint
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
import br.com.projetopi.smartlock.Classes.User
import br.com.projetopi.smartlock.MainActivity
import br.com.projetopi.smartlock.R
import br.com.projetopi.smartlock.Classes.Rental
import br.com.projetopi.smartlock.SimpleStorage
import br.com.projetopi.smartlock.databinding.FragmentOpcaoTempoBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class OpcaoTempo : Fragment() {

    private var _binding: FragmentOpcaoTempoBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: FirebaseFirestore

    // Variaveis que serao usada posteriormente para busca e show data
    private lateinit var establishmentID: String
    private lateinit var establishmentManagerName: String

    private lateinit var simpleStorage: SimpleStorage
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOpcaoTempoBinding.inflate(inflater,container,false)

        simpleStorage = SimpleStorage(requireContext())

        // Atrubui na variavel user os dados do usuario guardados no simpleStorage
        val user: User = simpleStorage.getUserAccountData()

        db = Firebase.firestore

        val sharedViewModelRental: SharedViewModelRental by activityViewModels()

        // Declarada e atribuida uma lista de descrições para cada plano de locação do armario
        val descList = listOf("30min", "1h", "2h", "4h", "Do momento até as 18h")

        // Pega as informações setadas no sharedViewModel e faz a busca do estabelecimento com o id do estabelecimento
        // setado no marcador
        val sharedViewModelEstablishment: SharedViewModelEstablishment by activityViewModels()
        sharedViewModelEstablishment.selectedEstablishment.observe(viewLifecycleOwner) { establishment ->
            establishmentID = establishment.uid.toString()
            establishmentManagerName = establishment.managerName.toString()
            db.collection("establishments").document(establishmentID).get().addOnSuccessListener { document ->
                // Atribui aos .text das opções (buttonRadio) as informações de cada plano, com o preço e descrição
                val preco1 = document.getDouble("planPrice1") ?: 0.0
                binding.op1.text = "${descList[0]} = R$ $preco1"

                val preco2 = document.getDouble("planPrice2") ?: 0.0
                binding.op2.text = "${descList[1]} = R$ $preco2"

                val preco3 = document.getDouble("planPrice3") ?: 0.0
                binding.op3.text = "${descList[2]} = R$ $preco3"

                val preco4 = document.getDouble("planPrice4") ?: 0.0
                binding.op4.text = "${descList[3]} = R$ $preco4"

                // Verifica o horario e dependendo do horario, mostra ou esconde essa opção
                val currentTime = getCurrentTime()
                if (currentTime < "07:00:00" || currentTime > "08:00:00") {
                    binding.op5.visibility = View.GONE
                } else {
                    val preco5 = document.getDouble("planPrice5") ?: 0.0
                    binding.op5.text = "${descList[4]} = R$ $preco5"
                }

                // Quando clicado, verifica se alguma opção dos buttonsRadio foi selecionada,
                // define as informações da locação que esta sendo realizado e adicona no firebase
                binding.btnConfirmarLocacao.setOnClickListener {
                    if(isSelected()){
                        val opSelected: RadioButton = binding.root.findViewById(binding.radioGroup.checkedRadioButtonId)
                        var locacaoAtual = Rental(
                            null,
                            user.uid,
                            establishmentID,
                            opSelected.text as String,
                            false,
                            true,
                            establishmentManagerName
                        )
                        db.collection("rentals").add(locacaoAtual).addOnSuccessListener {document ->
                            val locacaoAtualID = document.id
                            locacaoAtual.uid = locacaoAtualID
                            sharedViewModelRental.selectRental(locacaoAtual)
                            Toast.makeText(requireContext(), "Locação confirmada com sucesso", Toast.LENGTH_LONG).show()
                            (activity as MainActivity).changeFragment(QRCode())
                        }
                    } else {
                        Toast.makeText(requireContext(), "Você precisa selecionar pelo menos um plano", Toast.LENGTH_LONG).show()
                    }
                }

                binding.btnBack.setOnClickListener{
                    (activity as MainActivity).changeFragment(Mapa())
                }
            }
        }
        return binding.root
    }

    private fun isSelected(): Boolean {
        return binding.op1.isChecked || binding.op2.isChecked || binding.op3.isChecked ||
                binding.op4.isChecked || binding.op5.isChecked
    }

    @SuppressLint("SimpleDateFormat")
    private fun getCurrentTime(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("HH:mm:ss")

        return dateFormat.format(calendar.time)
    }
}