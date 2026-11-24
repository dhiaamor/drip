package eniso.ia2.drip2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class OutfitViewModel : ViewModel() {

    private val firestoreRepo = FirestoreRepository()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "testUser"

    private val _closetItems = MutableStateFlow<List<ClothingItem>>(emptyList())
    val closetItems: StateFlow<List<ClothingItem>> = _closetItems

    private val _styleFocus = MutableStateFlow("Casual")
    val styleFocus: StateFlow<String> = _styleFocus

    private val _suggestedOutfit = MutableStateFlow<String?>(null)
    val suggestedOutfit: StateFlow<String?> = _suggestedOutfit

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            firestoreRepo.fetchCloset(userId) { items ->
                _closetItems.value = items
            }
            firestoreRepo.fetchPreferences(userId) { style ->
                if (!style.isNullOrBlank()) {
                    _styleFocus.value = style
                }
            }
        }
    }

    fun generateAndUploadOutfit(style: String, season: String) {
        val filteredItems = _closetItems.value.filter {
            it.style.equals(style, ignoreCase = true) &&
                    it.season.equals(season, ignoreCase = true)
        }

        val top = filteredItems.firstOrNull { it.type.equals("Top", ignoreCase = true) }
        val bottom = filteredItems.firstOrNull { it.type.equals("Bottom", ignoreCase = true) }
        val footwear = filteredItems.firstOrNull { it.type.equals("Footwear", ignoreCase = true) }

        val missing = mutableListOf<String>()
        if (top == null) missing.add("Top")
        if (bottom == null) missing.add("Bottom")
        if (footwear == null) missing.add("Footwear")

        if (missing.isEmpty()) {
            val outfit = """
                Top: ${top!!.name} (${top.color})
                Bottom: ${bottom!!.name} (${bottom.color})
                Footwear: ${footwear!!.name} (${footwear.color})
            """.trimIndent()

            _suggestedOutfit.value = outfit

            // Upload to Firestore

            val outfitSuggestion = OutfitSuggestion(
                top = top.name,
                bottom = bottom.name,
                footwear = footwear.name,
                style = style,
                season = season
            )

            firestoreRepo.saveSuggestion(userId, outfitSuggestion)
        } else {
            _suggestedOutfit.value = "Missing: ${missing.joinToString(", ")} for $style / $season"
        }
    }
}
