package eniso.ia2.drip2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SuggestionViewModel : ViewModel() {

    private val firestoreRepo = FirestoreRepository()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "testUser"

    private val _closetItems = MutableStateFlow<List<ClothingItem>>(emptyList())
    val closetItems: StateFlow<List<ClothingItem>> = _closetItems

    private val _styleFocus = MutableStateFlow("Casual")
    val styleFocus: StateFlow<String> = _styleFocus

    init {
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            firestoreRepo.fetchCloset(userId) { items ->
                _closetItems.value = items
            }
            firestoreRepo.fetchPreferences(userId) { style ->
                _styleFocus.value = style ?: "Casual"
            }
        }
    }
}
