package rodriguez.jairo.proyectofinal_reviewssystem.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import rodriguez.jairo.proyectofinal_reviewssystem.R
import rodriguez.jairo.proyectofinal_reviewssystem.entities.Content

class ContentAdapter(
    private var listaContenido: List<Content>,
    private val onClick: (Content) -> Unit
) : RecyclerView.Adapter<ContentAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titulo: TextView = itemView.findViewById(R.id.movie_title_cell)
        val imagen: ImageView = itemView.findViewById(R.id.image_movie_cell)

        // 5 estrellas individuales
        val star1: ImageView = itemView.findViewById(R.id.star1)
        val star2: ImageView = itemView.findViewById(R.id.star2)
        val star3: ImageView = itemView.findViewById(R.id.star3)
        val star4: ImageView = itemView.findViewById(R.id.star4)
        val star5: ImageView = itemView.findViewById(R.id.star5)
        val stars = listOf(star1, star2, star3, star4, star5)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cell_movie, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = listaContenido.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contenido = listaContenido[position]

        holder.titulo.text = contenido.titulo

        // Cargar imagen con Glide desde urlImagen si existe
        Glide.with(holder.itemView.context)
            .load(contenido.urlImagen.takeIf { !it.isNullOrBlank() })
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .into(holder.imagen)

        // Mostrar las estrellas llenas o vacías según el rating
        val rating = contenido.estrellas.coerceIn(0, 5)
        holder.stars.forEachIndexed { index, starView ->
            if (index < rating) {
                starView.setImageResource(android.R.drawable.btn_star_big_on)
            } else {
                starView.setImageResource(android.R.drawable.btn_star_big_off)
            }
        }

        holder.itemView.setOnClickListener {
            onClick(contenido)
        }
    }

    fun actualizarLista(nuevaLista: List<Content>) {
        listaContenido = nuevaLista
        notifyDataSetChanged()
    }
}