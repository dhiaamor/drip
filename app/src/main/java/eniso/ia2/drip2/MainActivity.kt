package eniso.ia2.drip2

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.firebase.auth.FirebaseAuth
import eniso.ia2.drip2.ui.theme.Drip2Theme

class MainActivity : ComponentActivity() {

    private val closetList = mutableListOf<ClothingItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "testUser"

        // Load closet from local JSON
        closetList.addAll(Utils.loadClosetFromLocal(this))

        // Fetch updated closet from Firestore
        Utils.fetchClosetFromFirestore(userId) { fetchedCloset ->
            closetList.clear()
            closetList.addAll(fetchedCloset)
            Toast.makeText(this, "Closet loaded from Firestore", Toast.LENGTH_SHORT).show()
        }

        // Fetch preferences for info or later use
        Utils.fetchPreferencesFromFirestore(userId) { styleFocus ->
            Toast.makeText(this, "Style Focus: $styleFocus", Toast.LENGTH_SHORT).show()
        }

        enableEdgeToEdge()

        setContent {
            Drip2Theme {
                MainScreen(
                    closetItems = closetList,
                    onAddItem = { newItem ->
                        closetList.add(newItem)
                        Utils.saveClosetToLocal(this, closetList)
                        Utils.uploadClosetToFirestore(this, closetList, userId)
                    },
                    onClearCloset = {
                        closetList.clear()
                        Utils.saveClosetToLocal(this, closetList)
                        Utils.uploadClosetToFirestore(this, closetList, userId)
                        Toast.makeText(this, "Closet cleared!", Toast.LENGTH_SHORT).show()
                    },
                    onSuggestOutfit = {
                        startActivity(Intent(this, SuggestionActivity::class.java))
                    }
                )
            }
        }
    }
}