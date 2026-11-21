package eniso.ia2.drip2

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object Utils {

    fun fetchClosetFromFirestore(
        userId: String,
        param: (List<ClothingItem>) -> Unit
    ) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val closetList = mutableListOf<ClothingItem>()
                val closetData = document.get("closet") as? List<Map<String, String>> ?: emptyList()
                for (itemMap in closetData) {
                    closetList.add(
                        ClothingItem(
                            name = itemMap["name"] ?: "",
                            type = itemMap["type"] ?: "",
                            color = itemMap["color"] ?: "",
                            style = itemMap["style"] ?: "",
                            season = itemMap["season"] ?: ""
                        )
                    )
                }
                param(closetList)
            }
            .addOnFailureListener {
                Log.e("Firestore", "Failed to fetch closet", it)
                param(emptyList())
            }
    }

    fun fetchPreferencesFromFirestore(
        userId: String,
        onResult: (String?) -> Unit
    ) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val prefs = document.get("preferences") as? Map<*, *>
                val styleFocus = prefs?.get("styleFocus") as? String ?: "Casual"
                onResult(styleFocus)
            }
            .addOnFailureListener {
                Log.e("Firestore", "Failed to fetch preferences", it)
                onResult("Casual")
            }
    }

    fun saveClosetToLocal(context: Context, closet: List<ClothingItem>) {
        try {
            val json = Gson().toJson(closet)
            context.openFileOutput("closet_data.json", Context.MODE_PRIVATE).use {
                it.write(json.toByteArray())
            }
            Log.d("LocalSave", "Closet saved locally")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadClosetFromLocal(context: Context): List<ClothingItem> {
        return try {
            val json = context.openFileInput("closet_data.json")
                .bufferedReader().use { it.readText() }
            val type = object : TypeToken<List<ClothingItem>>() {}.type
            Gson().fromJson(json, type)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun uploadClosetToFirestore(context: Context, closet: List<ClothingItem>, userId: String) {
        val firestore = FirebaseFirestore.getInstance()
        val userRef = firestore.collection("users").document(userId)

        val closetMapList = closet.map {
            mapOf(
                "name" to it.name,
                "type" to it.type,
                "color" to it.color,
                "style" to it.style,
                "season" to it.season
            )
        }

        val data = mapOf(
            "closet" to closetMapList,
            "preferences" to mapOf(
                "weatherSensitive" to false,
                "styleFocus" to "Casual"
            ),
            "outfits" to emptyList<Map<String, String>>()
        )

        userRef.set(data)
            .addOnSuccessListener {
                Toast.makeText(context, "Synced with Firestore!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Upload failed", e)
                Toast.makeText(context, "Upload failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}