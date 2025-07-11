package rodriguez.jairo.proyectofinal_reviewssystem.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import rodriguez.jairo.proyectofinal_reviewssystem.R
import rodriguez.jairo.proyectofinal_reviewssystem.entities.Review
import rodriguez.jairo.proyectofinal_reviewssystem.entities.ReviewUser

class ReviewAdapter(
    private var listaReviews: List<ReviewUser>,
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
        val usernameReview: TextView = itemView.findViewById(R.id.username_review)
        val titleReview: TextView = itemView.findViewById(R.id.title_review)
        val reviewText: TextView = itemView.findViewById(R.id.review)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_review_detail, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = listaReviews.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reviewWithUser = listaReviews[position]
        val review = reviewWithUser.review
        val user = reviewWithUser.user

        holder.titleReview.text = review.titulo
        holder.reviewText.text = review.review
        holder.usernameReview.text = user.name


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

    fun actualizarLista(nuevaLista: List<ReviewUser>) {
        listaReviews = nuevaLista
        notifyDataSetChanged()
    }
}