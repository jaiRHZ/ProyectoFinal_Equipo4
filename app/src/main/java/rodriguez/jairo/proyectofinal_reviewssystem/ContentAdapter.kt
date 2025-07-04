package rodriguez.jairo.proyectofinal_reviewssystem

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import rodriguez.jairo.proyectofinal_reviewssystem.entities.Content

class ContentAdapter(
    private var listaContenido: List<Content>,
    private val onClick: (Content) -> Unit
): RecyclerView.Adapter<ContentAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titulo: TextView = itemView.findViewById(R.id.movie_title_cell)
        val imagen: ImageView = itemView.findViewById(R.id.image_movie_cell)
        val estrellas: ImageView = itemView.findViewById(R.id.stars_home)
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
        holder.imagen.setImageResource(contenido.imagen)
        holder.estrellas.setImageResource(contenido.estrellas)

        holder.itemView.setOnClickListener {
            onClick(contenido)
        }
    }

    fun actualizarLista(nuevaLista: List<Content>) {
        listaContenido = nuevaLista
        notifyDataSetChanged()
    }
}