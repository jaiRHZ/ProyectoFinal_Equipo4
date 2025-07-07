package rodriguez.jairo.proyectofinal_reviewssystem

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import rodriguez.jairo.proyectofinal_reviewssystem.entities.Review

class ReviewAdapter(
    private var listaReviews: List<Review>,
    private val onClick: (Review) -> Unit
) : RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val stars = listOf(
            itemView.findViewById<ImageView>(R.id.star1),
            itemView.findViewById(R.id.star2),
            itemView.findViewById(R.id.star3),
            itemView.findViewById(R.id.star4),
            itemView.findViewById(R.id.star5)
        )
        val titleReview: TextView = itemView.findViewById(R.id.title_review)
        val reviewText: TextView = itemView.findViewById(R.id.review)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_review, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = listaReviews.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val review = listaReviews[position]

        holder.titleReview.text = review.titulo
        holder.reviewText.text = review.review

        val rating = review.rating.coerceIn(0, 5)
        holder.stars.forEachIndexed { index, starView ->
            if (index < rating) {
                starView.setImageResource(android.R.drawable.btn_star_big_on)
            } else {
                starView.setImageResource(android.R.drawable.btn_star_big_off)
            }
        }

        holder.itemView.setOnClickListener {
            onClick(review)
        }
    }

    fun actualizarLista(nuevaLista: List<Review>) {
        listaReviews = nuevaLista
        notifyDataSetChanged()
    }
}