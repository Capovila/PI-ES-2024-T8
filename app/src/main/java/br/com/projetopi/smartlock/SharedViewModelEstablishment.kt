import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.projetopi.smartlock.Establishment

class SharedViewModelEstablishment : ViewModel() {
    private val _selectedEstablishment = MutableLiveData<Establishment>()
    val selectedEstablishment: LiveData<Establishment> get() = _selectedEstablishment
    fun selectEstablishment(establishment: Establishment) {
        _selectedEstablishment.value = establishment

    }

}