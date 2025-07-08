package rodriguez.jairo.proyectofinal_reviewssystem.entities

data class Review(var id: String = "",
                  var userId: String = "",
                  var contentId: String = "",
                  var rating: Int = 0,
                  var titulo: String = "",
                  var review: String = "",
                  var compartir: Boolean = false
)
{
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "userId" to userId,
            "contentId" to contentId,
            "rating" to rating,
            "titulo" to titulo,
            "review" to review,
            "compartir" to compartir
        )
    }
}