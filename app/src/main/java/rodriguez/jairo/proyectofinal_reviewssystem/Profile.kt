package rodriguez.jairo.proyectofinal_reviewssystem

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Profile : AppCompatActivity() {
    private lateinit var reviewsAdapter: ReviewsAdapter
    private val films = ArrayList<Film>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        //Falta conectar a pantalla Home
        findViewById<ImageView>(R.id.backHome).setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }

        //Falta conectar a pantalla settings
        findViewById<ImageView>(R.id.profile_settings).setOnClickListener {
            val intent = Intent(this, UserConfiguration::class.java)
            startActivity(intent)
        }

        cargarPeliculas()
        setupRecyclerView()
    }

    private fun cargarPeliculas() {
        films.add(Film(
            "The Brutalist",
            R.drawable.stars,
            R.drawable.thebrutalist,
            "The work creates a character whose life is an amalgam of many people's true experiences during that period of time; so it is very...", // descripción larga
            "I can see why it got 3 Oscars" // review corta
        ))

        films.add(Film(
            "The Brutalist",
            R.drawable.stars,
            R.drawable.thebrutalist,
            "At its core is brutal class commentary, painting a very well presented contrast between two very different lifestyles.", // descripción larga
            "I liked it for the same reason people hated it"
        ))
    }

    private fun setupRecyclerView() {
        val recyclerView: RecyclerView = findViewById(R.id.profile_reviews)
        reviewsAdapter = ReviewsAdapter(films)
        recyclerView.adapter = reviewsAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    inner class ReviewsAdapter(private val films: List<Film>) : RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder>() {

        inner class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val titleTextView: TextView = itemView.findViewById(R.id.title_review)
            val starsImageView: ImageView = itemView.findViewById(R.id.stars_review)
            val movieImageView: ImageView = itemView.findViewById(R.id.image_review)
            val reviewTextView: TextView = itemView.findViewById(R.id.review_text)
            val descriptionTextView: TextView = itemView.findViewById(R.id.description_text)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_review, parent, false)
            return ReviewViewHolder(view)
        }

        override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
            val film = films[position]
            holder.titleTextView.text = film.title
            holder.starsImageView.setImageResource(film.stars)
            holder.movieImageView.setImageResource(film.image)

            holder.reviewTextView.text = film.review
            holder.descriptionTextView.text = film.description
        }

        override fun getItemCount(): Int = films.size
    }
}