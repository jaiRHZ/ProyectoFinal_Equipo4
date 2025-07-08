package rodriguez.jairo.proyectofinal_reviewssystem.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import androidx.recyclerview.widget.RecyclerView
import rodriguez.jairo.proyectofinal_reviewssystem.entities.Content
import rodriguez.jairo.proyectofinal_reviewssystem.entities.ReviewContent
import rodriguez.jairo.proyectofinal_reviewssystem.R

class ReviewProfileAdapter (
    private val items: List<ReviewContent>,
    private val onClick: (Content) -> Unit
) : RecyclerView.Adapter<ReviewProfileAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title_movie)
        val image: ImageView = view.findViewById(R.id.image_review)
        val stars = listOf(
            itemView.findViewById<ImageView>(R.id.star1),
            itemView.findViewById(R.id.star2),
            itemView.findViewById(R.id.star3),
            itemView.findViewById(R.id.star4),
            itemView.findViewById(R.id.star5)
        )
        val titleReview: TextView = view.findViewById(R.id.title_review)
        val review: TextView = view.findViewById(R.id.review)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_review, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val review = item.review
        val content = item.content

        holder.title.text = item.content.titulo
        holder.titleReview.text = item.review.titulo
        holder.review.text = item.review.review

        val rating = review.rating.coerceIn(0, 5)
        holder.stars.forEachIndexed { index, starView ->
            if (index < rating) {
                starView.setImageResource(android.R.drawable.btn_star_big_on)
            } else {
                starView.setImageResource(android.R.drawable.btn_star_big_off)
            }
        }

        if (!item.content.urlImagen.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(item.content.urlImagen)
                .placeholder(R.drawable.placeholder)
                .into(holder.image)
        } else {
            holder.image.setImageResource(item.content.imagen)
        }

        holder.itemView.setOnClickListener {
            onClick(item.content)
        }
    }

    override fun getItemCount(): Int = items.size
}