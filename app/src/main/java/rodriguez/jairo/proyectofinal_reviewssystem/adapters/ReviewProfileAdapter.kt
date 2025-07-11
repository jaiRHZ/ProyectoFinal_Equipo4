package rodriguez.jairo.proyectofinal_reviewssystem.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import androidx.recyclerview.widget.RecyclerView
import rodriguez.jairo.proyectofinal_reviewssystem.R
import rodriguez.jairo.proyectofinal_reviewssystem.entities.ReviewContent

class ReviewProfileAdapter(
    private var listaReviewsWithContent: List<ReviewContent>,
    private val onClick: (ReviewContent) -> Unit
) : RecyclerView.Adapter<ReviewProfileAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contentImage: ImageView = itemView.findViewById(R.id.image_review)
        val contentTitle: TextView = itemView.findViewById(R.id.title_movie)
        val stars = listOf(
            itemView.findViewById<ImageView>(R.id.star1),
            itemView.findViewById<ImageView>(R.id.star2),
            itemView.findViewById<ImageView>(R.id.star3),
            itemView.findViewById<ImageView>(R.id.star4),
            itemView.findViewById<ImageView>(R.id.star5)
        )
        val titleReview: TextView = itemView.findViewById(R.id.title_review)
        val reviewText: TextView = itemView.findViewById(R.id.review)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_review, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = listaReviewsWithContent.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reviewWithContent = listaReviewsWithContent[position]
        val review = reviewWithContent.review
        val content = reviewWithContent.content

        // Configurar información de la review
        holder.titleReview.text = review.titulo
        holder.reviewText.text = review.review

        // Configurar estrellas
        val rating = review.rating.coerceIn(0, 5)
        holder.stars.forEachIndexed { index, starView ->
            if (index < rating) {
                starView.setImageResource(android.R.drawable.btn_star_big_on)
            } else {
                starView.setImageResource(android.R.drawable.btn_star_big_off)
            }
        }

        // Configurar información del contenido
        if (content != null) {
            holder.contentTitle.text = content.titulo

            // Cargar imagen con Glide, mostrando placeholder si no hay URL o falla carga
            Glide.with(holder.itemView.context)
                .load(content.urlImagen.takeIf { !it.isNullOrBlank() })
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(holder.contentImage)

            holder.contentImage.visibility = View.VISIBLE
        } else {
            holder.contentTitle.text = "Sin título"
            holder.contentImage.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            onClick(reviewWithContent)
        }
    }


    fun actualizarLista(nuevaLista: List<ReviewContent>) {
        listaReviewsWithContent = nuevaLista
        notifyDataSetChanged()
    }
}