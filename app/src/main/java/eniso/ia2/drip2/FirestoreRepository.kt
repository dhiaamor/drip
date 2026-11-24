package eniso.ia2.drip2

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore


class FirestoreRepository {

    private val firestore = FirebaseFirestore.getInstance()

    fun fetchCloset(userId: String, callback: (List<ClothingItem>) -> Unit) {
        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                val closetData = document.get("closet") as? List<Map<String, Any>>
                val closetList = closetData?.mapNotNull { item ->
                    try {
                        ClothingItem(
                            name = item["name"].toString(),
                            type = item["type"].toString(),
                            color = item["color"].toString(),
                            style = item["style"].toString(),
                            season = item["season"].toString()
                        )
                    } catch (e: Exception) {
                        Log.e("FirestoreRepo", "Invalid item format: $item", e)
                        null
                    }
                } ?: emptyList()

                callback(closetList)
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreRepo", "Failed to fetch closet", e)
                callback(emptyList())
            }
    }

    fun fetchPreferences(userId: String, callback: (String) -> Unit) {
        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                val preferences = document.get("preferences") as? Map<*, *>
                val styleFocus = preferences?.get("styleFocus")?.toString() ?: "Casual"
                callback(styleFocus)
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreRepo", "Failed to fetch preferences", e)
                callback("Casual") // Default fallback
            }
    }

    fun saveSuggestion(userId: String, outfit: OutfitSuggestion) {
        val outfitMap = mapOf<String, Any>(
            "top" to outfit.top,
            "bottom" to outfit.bottom,
            "footwear" to outfit.footwear,
            "style" to outfit.style,
            "season" to outfit.season,
            "timestamp" to outfit.timestamp
        )

        firestore.collection("users")
            .document(userId)
            .collection("suggestedOutfits")
            .add(outfitMap)
            .addOnSuccessListener {
                Log.d("FirestoreRepo", "Outfit saved successfully")
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreRepo", "Failed to save outfit", e)
            }
    }
}

