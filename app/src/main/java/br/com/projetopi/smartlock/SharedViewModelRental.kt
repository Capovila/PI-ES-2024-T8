import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.projetopi.smartlock.Classes.Rental

/***
 * Esta classe é um ViewModel compartilhado responsável por
 *  armazenar e compartilhar dados relacionados às locações
 */
class SharedViewModelRental: ViewModel() {

    // LiveData que armazena a locação selecionada.
    private val _selectedRental = MutableLiveData<Rental>()

    // LiveData pública para acessar a locação selecionada
    val selectedRental: LiveData<Rental> get() = _selectedRental

    // Função para selecionar uma locação e atualizar o LiveData _selectedRental
    fun selectRental(rental: Rental) {
        _selectedRental.value = rental
    }
}