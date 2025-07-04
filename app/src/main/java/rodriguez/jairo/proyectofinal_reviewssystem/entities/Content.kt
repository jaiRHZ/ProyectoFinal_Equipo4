package rodriguez.jairo.proyectofinal_reviewssystem.entities

data class Content(var id: String = "",
                   var titulo: String =" ",
                   var sinopsis: String = "",
                   var estrellas: Int = 0,
                   var imagen: Int = 0,
                   val urlImagen: String? = null,
                   var review: ArrayList<Review> = arrayListOf(),
                   var type: String = "",
                   var categoria: String = "",
                   var isbn:String? = "",
                   var tag: ArrayList<Tag> = arrayListOf()
)
{
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "titulo" to titulo,
            "sinopsis" to sinopsis,
            "estrellas" to estrellas,
            "imagen" to imagen,
            "urlImagen" to urlImagen,
            "review" to review.map { it.toMap() },
            "type" to type,
            "categoria" to categoria,
            "isbn" to isbn,
            "tag" to tag.map { it.toMap() }
        )
    }
}
