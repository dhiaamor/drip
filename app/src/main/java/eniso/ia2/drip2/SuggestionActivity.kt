package eniso.ia2.drip2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle

class SuggestionActivity : ComponentActivity() {

    private val outfitViewModel: OutfitViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val closetItems by outfitViewModel.closetItems.collectAsStateWithLifecycle()
            val styleFocus by outfitViewModel.styleFocus.collectAsStateWithLifecycle()
            val suggestion by outfitViewModel.suggestedOutfit.collectAsStateWithLifecycle()

            SuggestionScreen(
                closetItems = closetItems,
                initialStyle = styleFocus,
                suggestion = suggestion ?: "",
                onGenerateSuggestion = { selectedStyle, selectedSeason ->
                    outfitViewModel.generateAndUploadOutfit(selectedStyle, selectedSeason)
                }
            )
        }
    }
}
