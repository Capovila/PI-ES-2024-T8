import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.projetopi.smartlock.Classes.Establishment

/***
 * Esta classe é um ViewModel compartilhado responsável por
 *  armazenar e compartilhar dados relacionados aos estabelecimentos
 */
class SharedViewModelEstablishment : ViewModel() {

    // LiveData que armazena o estabelecimento selecionado
    private val _selectedEstablishment = MutableLiveData<Establishment>()

    // LiveData pública para acessar o estabelecimento selecionado
    val selectedEstablishment: LiveData<Establishment> get() = _selectedEstablishment

    // Função para selecionar um estabelecimento e atualizar o LiveData _selectedEstablishment
    fun selectEstablishment(establishment: Establishment) {
        _selectedEstablishment.value = establishment
    }
}