package eniso.ia2.drip2

data class OutfitSuggestion(
    val top: String = "",
    val bottom: String = "",
    val footwear: String = "",
    val style: String = "",
    val season: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
