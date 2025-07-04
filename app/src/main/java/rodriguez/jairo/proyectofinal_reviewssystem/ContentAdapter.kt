package rodriguez.jairo.proyectofinal_reviewssystem

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import rodriguez.jairo.proyectofinal_reviewssystem.entities.Content

class ContentAdapter(
    var listaContenidos: List<Content>,
    var onBorrarClic: (String) -> Unit,
    var onActualizarClick: (Content) -> Unit
    ): RecyclerView.Adapter<ContentAdapter.ViewHolder>() {
        class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
            val cvContenido: CardView = itemView.findViewById(R.id.cvContenido)
            val tvTitulo: TextView = itemView.findViewById(R.id.tvTitulo)
            val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
            val ibtnBorrar: ImageButton = itemView.findViewById(R.id.ibtnButton)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rv_tarea, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return listaContenidos.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val tarea = listaContenidos[position]

            holder.tvTitulo.text = tarea.titulo
            holder.tvDescripcion.text = tarea.descripcion

            holder.ibtnBorrar.setOnClickListener{
                onBorrarClic(tarea.id)
            }

            holder.cvTarea.setOnClickListener{
                onActualizarClick(tarea)
            }

        }
}