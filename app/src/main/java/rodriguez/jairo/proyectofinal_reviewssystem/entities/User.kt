package rodriguez.jairo.proyectofinal_reviewssystem.entities

data class User(
    var name: String = "",
    var gender: String = "",
    var birthdate: String = "",
    var urlImagen: String = "",
    var myReviewIds: List<String> = emptyList()
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "name" to name,
            "gender" to gender,
            "birthdate" to birthdate,
            "urlImagen" to urlImagen,
            "myReviewIds" to myReviewIds
        )
    }
}
