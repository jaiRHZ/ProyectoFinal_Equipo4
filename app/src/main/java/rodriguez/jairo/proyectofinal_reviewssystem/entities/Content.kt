package rodriguez.jairo.proyectofinal_reviewssystem.entities

data class Content(var id: String = "",
                   var titulo: String =" ",
                   var estrellas: Int = 0,
                   var imagen: Int = 0,
                   var review: ArrayList<Review> = arrayListOf(),
                   var type: String = "",
                   var categoria: String = "",
                   var tag: ArrayList<Tag> = arrayListOf()
)
{
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "titulo" to titulo,
            "estrellas" to estrellas,
            "imagen" to imagen,
            "review" to review.map { it.toMap() },
            "type" to type,
            "categoria" to categoria,
            "tag" to tag.map { it.toMap() }
        )
    }
}
