import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.projetopi.smartlock.Classes.Rental

class SharedViewModelRental: ViewModel() {
    private val _selectedRental = MutableLiveData<Rental>()
    val selectedRental: LiveData<Rental> get() = _selectedRental
    fun selectRental(rental: Rental) {
        _selectedRental.value = rental

    }

}