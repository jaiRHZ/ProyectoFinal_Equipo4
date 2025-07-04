package rodriguez.jairo.proyectofinal_reviewssystem.entities

data class Tag(var id: String = "",
               var nombre: String = "")
{
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "nombre" to nombre
        )
    }
}
