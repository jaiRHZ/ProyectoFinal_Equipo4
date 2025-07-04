package rodriguez.jairo.proyectofinal_reviewssystem.entities

data class Content(var titulo: String, var estrellas: Int, var imagen: Int,
                   var review: ArrayList<Review>, var type: String,
                   var categoria: String, var tag: ArrayList<Tag>)
