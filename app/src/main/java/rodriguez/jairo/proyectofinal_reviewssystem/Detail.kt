package rodriguez.jairo.proyectofinal_reviewssystem

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import rodriguez.jairo.proyectofinal_reviewssystem.adapters.ReviewAdapter
import rodriguez.jairo.proyectofinal_reviewssystem.viewmodels.ReviewViewModel
import rodriguez.jairo.proyectofinal_reviewssystem.viewmodels.UserViewModel

class Detail : AppCompatActivity() {
    private lateinit var tvTitle: TextView
    private lateinit var tvContentGenre: TextView
    private lateinit var tvContentCategory: TextView
    private lateinit var tvContentType: TextView
    private lateinit var tvContentSynopsis: TextView
    private lateinit var tvContenRatingAverage: TextView
    private lateinit var tvContentLearnMore: TextView

    private lateinit var recyclerView: RecyclerView
    private lateinit var reviewUserAdapter: ReviewAdapter
    private lateinit var reviewViewModel: ReviewViewModel
    private lateinit var userViewModel: UserViewModel

//    private lateinit var reviewsAdapter: ReviewsAdapter
//    private val films = ArrayList<Film>()
//
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
//
        initViews()
        setupRecyclerView()
        setUpContent()
        setupClickListeners()
//
//        val back: ImageView = findViewById(R.id.backHome_detail)
//        back.setOnClickListener {
//            val intent: Intent = Intent(this, Home::class.java)
//            startActivity(intent)
//        }
//
//        val buttonReview: Button = findViewById(R.id.btnAddReview)
//        buttonReview.setOnClickListener {
//            val intent: Intent = Intent(this, AddReview::class.java)
//            startActivity(intent)
//        }
//
//        //------------------------------------------------------------------------------------------
//        val viewDetailContent = intent.extras
//
//        if (viewDetailContent != null){
//            findViewById<TextView>(R.id.viewTitle_detail).text =viewDetailContent.getString("Title")
//            findViewById<TextView>(R.id.averageRating_detail).text = viewDetailContent.getString("stars")
//            findViewById<ImageView>(R.id.cover_detail).setImageResource(viewDetailContent.getInt("Image"))
//            findViewById<TextView>(R.id.viewContentSynopsis_detail).text = viewDetailContent.getString("description")
//            findViewById<TextView>(R.id.viewContentType_detail).text = viewDetailContent.getString("type")
//
//        }
//
//
//        //Componentes
//        setupRecyclerView()
//    }
//
//
//
//
//    private fun setupRecyclerView() {
//        val recyclerView: RecyclerView = findViewById(R.id.detail_reviews)
//        reviewsAdapter = ReviewsAdapter(films)
//        recyclerView.adapter = reviewsAdapter
//        recyclerView.layoutManager = LinearLayoutManager(this)
//    }
//
//    //----------------------------------------------------------------------------------------------
//
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
    }

    private fun initViews() {
        tvTitle = findViewById(R.id.viewTitle_detail)
        tvContentGenre = findViewById(R.id.viewContentGenre_detail)
        tvContentCategory = findViewById(R.id.viewContentCategory_detail)
        tvContentType = findViewById(R.id.viewContentType_detail)
        tvContentSynopsis = findViewById(R.id.viewContentSynopsis_detail)
        tvContenRatingAverage = findViewById(R.id.averageRating_detail)
        tvContentLearnMore = findViewById(R.id.viewContentLearnMore_detail)
        recyclerView = findViewById(R.id.detail_reviews)
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = reviewUserAdapter
    }


    private fun setUpContent() {
        intent.extras?.let { details ->
            tvTitle.text = details.getString("title", "")
            tvContentGenre.text = details.getString("genre", "")
            tvContentCategory.text = details.getString("category", "")
            tvContentType.text = details.getString("type", "")
            tvContentSynopsis.text = details.getString("synopsis", "")
            tvContenRatingAverage.text = details.getString("ratingAverage", "")

            // Hacer scrollable la sinopsis si es necesario
            //tvContentSynopsis.movementMethod = ScrollingMovementMethod()
        }
     }

    private fun setupClickListeners() {
        val back: ImageView = findViewById(R.id.backHome_detail)
        val buttonReview: Button = findViewById(R.id.btnAddReview)

        back.setOnClickListener {
            val intent: Intent = Intent(this, Home::class.java)
            startActivity(intent)
        }

        buttonReview.setOnClickListener {
            val intent: Intent = Intent(this, AddReview::class.java)
            startActivity(intent)
        }
    }
}