package br.com.projetopi.smartlock.Fragments

import SharedViewModelEstablishment
import SharedViewModelRental
import android.annotation.SuppressLint
import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import br.com.projetopi.smartlock.Classes.User
import br.com.projetopi.smartlock.MainActivity
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
    private lateinit var establishmentID: String
    private lateinit var establishmentManagerName: String
    private lateinit var simpleStorage: SimpleStorage
    private lateinit var managerId: String

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentOpcaoTempoBinding.inflate(inflater,container,false)

        simpleStorage = SimpleStorage(requireContext())

        val user: User = simpleStorage.getUserAccountData()

        db = Firebase.firestore

        val sharedViewModelRental: SharedViewModelRental by activityViewModels()

        // Lista de descrições fixas para os planos de locação do armário
        val descList = listOf("30min", "1h", "2h", "4h", "Do momento até as 18h")

        /***
         * Pega as informações vindas do sharedViewModelEstablishment, busca
         * o estabelecimento com o id do estabelecimento vindo do sharedViewModelEstablishment
         * e define os text's dos RadioButton com o valor e preço de cada plano de locação
         */
        val sharedViewModelEstablishment: SharedViewModelEstablishment by activityViewModels()
        sharedViewModelEstablishment.selectedEstablishment.observe(viewLifecycleOwner) { establishment ->
            establishmentID = establishment.uid.toString()
            establishmentManagerName = establishment.managerName.toString()

            Toast.makeText(
                requireContext(),
                "O estabelecimento fecha as 18 horas",
                Toast.LENGTH_LONG
            ).show()

            db.collection("establishments")
                .document(establishmentID)
                .get()
                .addOnSuccessListener { document ->
                    val preco1 = document.getDouble("planPrice1") ?: 0.0
                    binding.op1.text = "${descList[0]} = R$ $preco1"

                    val preco2 = document.getDouble("planPrice2") ?: 0.0
                    binding.op2.text = "${descList[1]} = R$ $preco2"

                    val preco3 = document.getDouble("planPrice3") ?: 0.0
                    binding.op3.text = "${descList[2]} = R$ $preco3"

                    val preco4 = document.getDouble("planPrice4") ?: 0.0
                    binding.op4.text = "${descList[3]} = R$ $preco4"

                    val preco5 = document.getDouble("planPrice5") ?: 0.0
                    binding.op5.text = "${descList[4]} = R$ $preco5"

                    managerId = document.getString("managerId").toString()




                    if(getHour() >= 8 && getMin() > 0) {
                        binding.op5.isEnabled  = false
                    }
                    if(getHour() >= 17 && getMin() > 0){
                        binding.op2.isEnabled = false
                        if((getHour() >= 17 && getMin() > 30 )|| getHour() >= 18){
                            binding.op1.isEnabled = false
                            binding.btnConfirmarLocacao.setText("Estabelecimento Fechado")
                            binding.btnConfirmarLocacao.isEnabled = false
                        }

                    }

                    if(getHour() >= 16 && getMin() > 0){
                        binding.op3.isEnabled = false
                    }
                    if(getHour() >= 14 && getMin() > 0){
                        binding.op4.isEnabled = false
                    }




                    /***
                     * Quando o btnConfirmarLocacao é clicado, verifica se algum RadioButton foi selecionado,
                     * caso True, pega o RadioButton selecionado e atribui a variavel locacaoAtual os dados
                     * da locação usando uma classe Rental, adiciona essas informações ao Firestore, pega o id
                     * do documento adicionado e atribui a varivael que guarda as informações da locação,
                     * passa as informações do estabelecimento pelo SharedViewModelRental e muda o fragmento
                     * exibido na main activity para o fragmento QRCode
                     */


                    binding.btnConfirmarLocacao.setOnClickListener {
                        if (isSelected()) {
                            val opSelected: RadioButton =
                                binding.root.findViewById(binding.radioGroup.checkedRadioButtonId)
                            val locacaoAtual = Rental(
                                preco2.toInt(),
                                getHour(),
                                preco5.toInt(),
                                managerId,
                                null,
                                user.uid,
                                establishmentID,
                                opSelected.text as String,
                                false,
                                true,
                                establishmentManagerName,
                            )
                            db.collection("rentals")
                                .add(locacaoAtual)
                                .addOnSuccessListener { document ->
                                    locacaoAtual.uid = user.uid
                                    sharedViewModelRental.selectRental(locacaoAtual)
                                    (activity as MainActivity).changeFragment(QRCode())
                                }
                            val newRentalState = hashMapOf(
                                "isRented" to true,
                                "currentIdRental" to "${user.uid}"
                            )
                            db.collection("lockers")
                                .whereEqualTo("idEstablishment", establishmentID)
                                .whereEqualTo("isRented", false)
                                .get()
                                .addOnSuccessListener { querySnapshot ->
                                    if (!querySnapshot.isEmpty) {
                                        val firstDocument = querySnapshot.documents[0]
                                        firstDocument.reference.update(newRentalState as Map<String, Any>)
                                    }
                                }
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Você precisa selecionar pelo menos um plano",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    /***
                     * Quando o btnBack é clicado muda o fragmento exibido na main activity
                     * para o fragmento Mapa
                     */
                    binding.btnBack.setOnClickListener {
                        (activity as MainActivity).changeFragment(Mapa())
                    }
                }
        }
        return binding.root
    }

    /***
     * Faz com que quando executada, verifica se alguma opção do
     * radio group foi selecionada e retorna True ou False
     */
    private fun isSelected(): Boolean {
        return binding.op1.isChecked
                || binding.op2.isChecked
                || binding.op3.isChecked
                || binding.op4.isChecked
                || binding.op5.isChecked
    }

    private fun getHour():Int{
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    }
    private fun getMin():Int{
        return Calendar.getInstance().get(Calendar.MINUTE)
    }



}