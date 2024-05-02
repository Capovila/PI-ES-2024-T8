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

    //Definindo e atribuindo as variaveis para o uso do ViewBinding no fragment
    private var _binding:FragmentLocacoesBinding? = null
    private val binding get() = _binding!!
<<<<<<< HEAD
    private lateinit var simpleStorage: SimpleStorage
    private lateinit var db: FirebaseFirestore
    private lateinit var rentalID: String
    private lateinit var establishmentID: String
    private lateinit var establishmentManagerName: String

=======

    //Definindo varivais que serão iniciadas posteriormente
    // (simpleStorage: banco de dados offline do celular, db: banco de dados online do firebase)
    private lateinit var simpleStorage: SimpleStorage
    private lateinit var db: FirebaseFirestore

    //Definindo as variaveis que serao iniciadas posteriormente
    // (rentalID: id da locação que estiver aberta, establishmentID: id do estabelecimento
    // da locação que estiver aberta, establishmentManagerName nome do gerente do estabelecimento da
    // locação que estiver aberta
    private lateinit var rentalID: String
    private lateinit var establishmentID: String
    private lateinit var establishmentManagerName: String
    @SuppressLint("SetTextI18n")
>>>>>>> master
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //Atribuindo o layout que sera inflado no fragment
        _binding = FragmentLocacoesBinding.inflate(inflater, container, false)

        //Atribuindo a classe SimpleStorage na variavel
        simpleStorage = SimpleStorage(requireContext())
        //Definindo e atribuindo os dados do usuario que esta no SimpleStorage para a variavel user
        val user: User = simpleStorage.getUserAccountData()

        //Atribuindo o Firebase Firestore usado no projeto na variavel db
        db = Firebase.firestore

<<<<<<< HEAD
        // Esconde o ImageView do QR Code
        binding.qrcode.visibility = View.GONE

        /***
         * Busca locações que estejam relacinadas ao id do Usuario e se a locação está aberta,
         * caso ache alguma, pega o id da locação o id do estabelecimento, faz um bitmap com o
         * id do estabelecimento e após buscar o nome do gerente do estabelecimento dessa locação,
         * define o textView com o nome do gerente buscado
         */
=======
        //Atribuindo a visibilidade do image view como view.GONE (esconder)
        binding.qrcode.visibility = View.GONE

        //Busca documentos dos quais o idUser seja igual o id do usuario guardado no simpleStorage e que
        // o rentalOpen seja true (locação aberta), caso seja encontrado algum documento, atribui ao
        // rentalID o id do documento, atribui ao establishmentID o id do lugar que esta a locação,
        // define e atribui um objeto MultiFormatWriter que cria códigos de barras em diferentes formatos,
        // define e atribui uma matriz de bits (bitMatrix) que representa o código de barras criado a partir
        // do ID do aluguel (rentalID) fornecido, o código de barras é do formato QR_CODE e possui dimensões de 300x300 pixels,
        // define e atribui um objeto BarcodeEncoder que é usado para converter a matriz de bits (bitMatrix) em um bitmap,
        // cria um bitmap a partir da matriz de bits (bitMatrix) do código de barras, atribuindo a visibilidade do image view
        // como view.VISIBLE (mostra), atribui o bitmap do qr code ao image view (qrcode), busca um documento da coleção
        // establishments que tenha o id do documento = establishmentID, atribui à variavel establishmentManagerName o
        // managerName do estabelecimento, atribui ao text do textView (tvInfo) o texto "Apresente esse QR Code ao gerente" +
        // o managerName
>>>>>>> master
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