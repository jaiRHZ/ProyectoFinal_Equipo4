package rodriguez.jairo.proyectofinal_reviewssystem

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import rodriguez.jairo.proyectofinal_reviewssystem.adapters.ReviewProfileAdapter
import rodriguez.jairo.proyectofinal_reviewssystem.entities.Content
import rodriguez.jairo.proyectofinal_reviewssystem.entities.Review
import rodriguez.jairo.proyectofinal_reviewssystem.entities.ReviewContent
import rodriguez.jairo.proyectofinal_reviewssystem.viewmodels.ContentViewModel
import rodriguez.jairo.proyectofinal_reviewssystem.viewmodels.ReviewViewModel
import rodriguez.jairo.proyectofinal_reviewssystem.viewmodels.UserViewModel

class Profile : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var reviewContentAdapter: ReviewProfileAdapter
    private lateinit var reviewViewModel: ReviewViewModel
    private lateinit var contentViewModel: ContentViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var etUsername: TextView
    private lateinit var profilePic: ImageView

    private var userReviews: List<Review> = emptyList()
    private var allContents: List<Content> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        Log.d("Profile", "UID del usuario actual: $uid")

        initViews()
        setupRecyclerView()
        setupUserViewModel()
        setupContentViewModel()
        setupReviewViewModel()
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
                etUsername.text = it.name
                //Falta imagen de perfil
            }
        }
    }

    private fun setupRecyclerView() {
        reviewContentAdapter = ReviewProfileAdapter(emptyList()) { reviewContent ->
            Log.d("Profile", "Click en review: ${reviewContent.review.titulo}")
            navigateToContentDetail(reviewContent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = reviewContentAdapter
    }

    private fun setupContentViewModel() {
        contentViewModel = ViewModelProvider(this)[ContentViewModel::class.java]

        contentViewModel.listaContenidos.observe(this) { contenidos ->
            allContents = contenidos
            Log.d("Profile", "Contenidos cargados: ${contenidos.size}")
            combineReviewsWithContent()
        }
    }

    private fun setupReviewViewModel() {
        reviewViewModel = ViewModelProvider(this)[ReviewViewModel::class.java]

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            reviewViewModel.obtenerReviewsDelUsuario(uid)
        }

        reviewViewModel.reviewsUsuario.observe(this) { reviews ->
            userReviews = reviews
            Log.d("Profile", "Reviews del usuario: ${reviews.size}")
            combineReviewsWithContent()
        }
    }

    private fun combineReviewsWithContent() {
        if (userReviews.isEmpty() || allContents.isEmpty()) {
            Log.d("Profile", "Esperando datos: reviews=${userReviews.size}, contents=${allContents.size}")
            return
        }

        val reviewsWithContent = userReviews.map { review ->
            val content = allContents.find { it.id == review.contentId }
            ReviewContent(review, content)
        }

        Log.d("Profile", "Combinando ${reviewsWithContent.size} reviews con contenido")
        reviewContentAdapter.actualizarLista(reviewsWithContent)
    }

    private fun navigateToContentDetail(reviewWithContent: ReviewContent) {
        val content = reviewWithContent.content
        if (content != null) {
            val intent = Intent(this, Detail::class.java)
            intent.putExtra("contentId", content.id)
            intent.putExtra("title", content.titulo)
            intent.putExtra("imageUrl", content.urlImagen)
            intent.putExtra("imageLocal", content.imagen)
            intent.putExtra("rate", content.estrellas)
            intent.putExtra("category", content.categoria)
            intent.putExtra("type", content.type)
            intent.putExtra("synopsis", content.sinopsis)
            // Puedes agregar más extras si Detail los necesita
            startActivity(intent)
        } else {
            Log.w("Profile", "No se puede navegar: contenido no disponible")
        }
    }
}