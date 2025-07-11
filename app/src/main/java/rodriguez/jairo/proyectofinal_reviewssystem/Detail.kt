package rodriguez.jairo.proyectofinal_reviewssystem

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.firestore
import rodriguez.jairo.proyectofinal_reviewssystem.adapters.ReviewAdapter
import rodriguez.jairo.proyectofinal_reviewssystem.viewmodels.ReviewViewModel
import rodriguez.jairo.proyectofinal_reviewssystem.viewmodels.UserViewModel

class Detail : AppCompatActivity() {

    private lateinit var tvTitle: TextView
    private lateinit var tvContentCategory: TextView
    private lateinit var tvContentType: TextView
    private lateinit var tvContentSynopsis: TextView
    private lateinit var tvContentRating: TextView
    private lateinit var ivCoverImage: ImageView
    private lateinit var recyclerView: RecyclerView

    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var reviewViewModel: ReviewViewModel
    private lateinit var userViewModel: UserViewModel

    private var contentId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        initViews()
        setupViewModels()
        loadContentData()
        setupRecyclerView()
        setupClickListeners()
    }

    private fun initViews() {
        tvTitle = findViewById(R.id.viewTitle_detail)
        tvContentCategory = findViewById(R.id.viewContentCategory_detail)
        tvContentType = findViewById(R.id.viewContentType_detail)
        tvContentSynopsis = findViewById(R.id.viewContentSynopsis_detail)
        tvContentRating = findViewById(R.id.averageRating_detail)
        ivCoverImage = findViewById(R.id.cover_detail)
        recyclerView = findViewById(R.id.detail_reviews)
    }

    private fun setupViewModels() {
        reviewViewModel = ViewModelProvider(this)[ReviewViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        reviewViewModel.listaReviews.observe(this) { reviews ->
            val filteredReviews = reviews.filter { it.contentId == contentId && it.compartir }
            if (filteredReviews.isEmpty()) {
                reviewAdapter.actualizarLista(emptyList())
                return@observe
            }

            // Obtener los usuarios de los reviews
            val userIds = filteredReviews.map { it.userId }.distinct()
            val db = com.google.firebase.Firebase.firestore
            db.collection("users").whereIn("name", userIds).get()
                .addOnSuccessListener { result ->
                    val users = result.documents.mapNotNull { it.toObject(rodriguez.jairo.proyectofinal_reviewssystem.entities.User::class.java) }
                    val userMap = users.associateBy { it.name } // OJO: si el campo userId es el UID, deberías usar otro campo
                    val reviewUsers = filteredReviews.map { review ->
                        val user = userMap[review.userId] ?: rodriguez.jairo.proyectofinal_reviewssystem.entities.User(name = "Usuario")
                        rodriguez.jairo.proyectofinal_reviewssystem.entities.ReviewUser(user = user, review = review)
                    }
                    reviewAdapter.actualizarLista(reviewUsers)
                }
                .addOnFailureListener {
                    // Si falla, mostrar dummy
                    val reviewUsers = filteredReviews.map { review ->
                        rodriguez.jairo.proyectofinal_reviewssystem.entities.ReviewUser(
                            user = rodriguez.jairo.proyectofinal_reviewssystem.entities.User(name = "Usuario"),
                            review = review
                        )
                    }
                    reviewAdapter.actualizarLista(reviewUsers)
                }
        }
    }

    private fun loadContentData() {
        intent.extras?.let { extras ->
            try {
                // 1. Datos básicos (con defaults seguros)
                tvTitle.text = extras.getString("title", "Sin título")
                tvContentCategory.text = "Category: ${extras.getString("category", "Desconocida")}"
                tvContentType.text = "Type: ${extras.getString("type", "No especificado")}"
                tvContentSynopsis.text = extras.getString("synopsis", "Non synopsis")
                tvContentRating.text = "⭐ ${extras.getInt("rate", 0)}/5"

                // 2. Imagen (con Glide para URL de Firebase)
                try {
                    val imageUrl = extras.getString("imageUrl")
                    if (!imageUrl.isNullOrEmpty()) {
                        com.bumptech.glide.Glide.with(this)
                            .load(imageUrl)
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.placeholder)
                            .into(ivCoverImage)
                    } else {
                        extras.getInt("imageLocal", 0).takeIf { it != 0 }?.let { drawableId ->
                            ivCoverImage.setImageResource(drawableId)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("Detail", "Error cargando imagen: ${e.message}")
                    ivCoverImage.setImageResource(R.drawable.placeholder)
                }

                // 3. Reviews (con validación de contentId)
                contentId = extras.getString("contentId", "")
                if (contentId.isEmpty()) {
                    Log.e("Detail", "Error: contentId vacío")
                    return
                }
                reviewViewModel.obtenerReviews()
            } catch (e: Exception) {
                Log.e("Detail", "Error inesperado: ${e.message}")
                Toast.makeText(this, "Error al cargar el contenido", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(this, "Datos inválidos", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupRecyclerView() {
        reviewAdapter = ReviewAdapter(mutableListOf()) { }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = reviewAdapter
    }

    private fun setupClickListeners() {
        findViewById<ImageView>(R.id.backHome_detail).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btnAddReview).setOnClickListener {
            Intent(this, AddReview::class.java).apply {
                putExtra("contentId", contentId)
                putExtra("contentTitle", tvTitle.text.toString())
                startActivity(this)
            }
        }
    }
}