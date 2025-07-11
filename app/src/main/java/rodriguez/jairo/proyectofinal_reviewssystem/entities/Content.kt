package rodriguez.jairo.proyectofinal_reviewssystem.entities

data class Content(
    var id: String = "",
    var titulo: String = "",
    var sinopsis: String = "",
    var estrellas: Int = 0,
    val urlImagen: String? = null,
    var reviewIds: ArrayList<String> = arrayListOf(),
    var type: String = "",
    var categoria: String = "",
    var isbn: String? = "",
    var tagIds: ArrayList<String> = arrayListOf()
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "titulo" to titulo,
            "sinopsis" to sinopsis,
            "estrellas" to estrellas,
            "urlImagen" to urlImagen,
            "reviewIds" to reviewIds,
            "type" to type,
            "categoria" to categoria,
            "isbn" to isbn,
            "tagIds" to tagIds
        )
    }
}
