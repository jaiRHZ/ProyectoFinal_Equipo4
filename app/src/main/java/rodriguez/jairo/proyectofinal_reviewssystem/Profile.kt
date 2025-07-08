package rodriguez.jairo.proyectofinal_reviewssystem

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import rodriguez.jairo.proyectofinal_reviewssystem.adapters.ContentAdapter
import rodriguez.jairo.proyectofinal_reviewssystem.adapters.ReviewProfileAdapter
import rodriguez.jairo.proyectofinal_reviewssystem.entities.Content
import rodriguez.jairo.proyectofinal_reviewssystem.entities.ReviewContent
import rodriguez.jairo.proyectofinal_reviewssystem.viewmodels.ContentViewModel
import rodriguez.jairo.proyectofinal_reviewssystem.viewmodels.ReviewViewModel
import rodriguez.jairo.proyectofinal_reviewssystem.viewmodels.UserViewModel

class Profile : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView

    private lateinit var contentAdapter: ContentAdapter
    private lateinit var contentViewModel: ContentViewModel

    private lateinit var reviewsAdapter: ReviewProfileAdapter
    private lateinit var reviewsViewModel: ReviewViewModel
    //private val films = ArrayList<Film>()
    private lateinit var contenidoUsuario: ArrayList<Content>

    private lateinit var userViewModel: UserViewModel
    private lateinit var etUsername: EditText
    private lateinit var profilePic: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        initViews()
        //cargarPeliculas()
        setupRecyclerView()
        setupUserViewModel()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.profile_reviews)

        etUsername = findViewById(R.id.profile_username)
        profilePic = findViewById(R.id.profile_picture)

        findViewById<ImageView>(R.id.backHome).setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.profile_settings).setOnClickListener {
            val intent = Intent(this, UserConfiguration::class.java)
            startActivity(intent)
        }
    }

    private fun setupUserViewModel() {
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            userViewModel.cargarUsuario(uid)
        }

        userViewModel.usuario.observe(this) { user ->
            user?.let {
                etUsername.setText(it.name)
                //Falta imagen de perfil
            }
        }

    }

//    private fun cargarPeliculas() {
//        films.add(Film(
//            "The Brutalist",
//            R.drawable.stars,
//            R.drawable.thebrutalist,
//            "The work creates a character whose life is an amalgam of many people's true experiences during that period of time; so it is very...", // descripción larga
//            "I can see why it got 3 Oscars",
//            "movies"// review corta
//        ))
//
//        films.add(Film(
//            "Anora",
//            R.drawable.stars,
//            R.drawable.anora,
//            "At its core is brutal class commentary, painting a very well presented contrast between two very different lifestyles.", // descripción larga
//            "I liked it for the same reason people hated it",
//            "movies"
//        ))
//
//        films.add(Film(
//            "Conclave",
//            R.drawable.stars,
//            R.drawable.conclave,
//            "The Conclave is a great film that is sure to keep you on the edge of your seat and holding your breath. Edward Berger's vision is perfectly executed in just about every way possible.",
//            "Oscar Worthy Through and Through",
//            "movies"
//        ))
//    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        cargarReviewsDelUsuario()
//        reviewsAdapter = ReviewsAdapter(films)
//        recyclerView.adapter = reviewsAdapter
//        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun cargarReviewsDelUsuario() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()

        db.collection("reviews")
            .whereEqualTo("userId", uid)
            .get()
            .addOnSuccessListener { snapshot ->
                val reviews = snapshot.toObjects(rodriguez.jairo.proyectofinal_reviewssystem.entities.Review::class.java)
                val listaCombinada = mutableListOf<ReviewContent>()

                if (reviews.isEmpty()) return@addOnSuccessListener

                for (review in reviews) {
                    db.collection("content").document(review.contentId).get()
                        .addOnSuccessListener { doc ->
                            val content = doc.toObject(Content::class.java)
                            if (content != null) {
                                listaCombinada.add(ReviewContent(review, content))
                                if (listaCombinada.size == reviews.size) {
                                    mostrarReviews(listaCombinada)
                                }
                            }
                        }
                }
            }
    }

    private fun mostrarReviews(lista: List<ReviewContent>) {
        val adapter = ReviewProfileAdapter(lista) { content ->
            val intent = Intent(this, Detail::class.java).apply {
                putExtra("Title", content.titulo)
                putExtra("ImageUrl", content.urlImagen)
                putExtra("ImageLocal", content.imagen)
                putExtra("Rate", content.estrellas)
            }
            startActivity(intent)
        }
        recyclerView.adapter = adapter
    }

//    inner class ReviewsAdapter(private val films: List<Film>) : RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder>() {
//
//        inner class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//            val titleTextView: TextView = itemView.findViewById(R.id.title_review)
//            val starsImageView: ImageView = itemView.findViewById(R.id.stars_review)
//            val movieImageView: ImageView = itemView.findViewById(R.id.image_review)
//            val reviewTextView: TextView = itemView.findViewById(R.id.review_text)
//            val descriptionTextView: TextView = itemView.findViewById(R.id.description_text)
//        }
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
//            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_review, parent, false)
//            return ReviewViewHolder(view)
//        }
//
//        override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
//            val film = films[position]
//            holder.titleTextView.text = film.title
//            holder.starsImageView.setImageResource(film.stars)
//            holder.movieImageView.setImageResource(film.image)
//
//            holder.reviewTextView.text = film.review
//            holder.descriptionTextView.text = film.description
//        }
//
//        override fun getItemCount(): Int = films.size
//    }
}