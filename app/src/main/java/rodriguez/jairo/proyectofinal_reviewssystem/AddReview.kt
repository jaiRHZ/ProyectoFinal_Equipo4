package rodriguez.jairo.proyectofinal_reviewssystem

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.firestore

class AddReview : AppCompatActivity() {
    private val stars = mutableListOf<ImageView>()
    private var selectedRating = 0

    lateinit var outContenTitle: TextView
    lateinit var inputShortDescription: EditText
    lateinit var inputFullReview: EditText
    lateinit var switchPublicShre: SwitchCompat
    lateinit var btnAddReview_review: Button
    lateinit var btnCancelReview_review: Button
    lateinit var btnBackReview: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_review)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initComponents()
        setupListeners()

        findViewById<ImageView>(R.id.btnBack_addreview).setOnClickListener {
            val intent = Intent(this, Detail::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnCancel_review).setOnClickListener {
            val intent = Intent(this, Detail::class.java)
            startActivity(intent)
        }

    }

    private fun initComponents(){
        outContenTitle = findViewById(R.id.outContentTitle_review)
        inputShortDescription = findViewById(R.id.inputShortDescription_addReview)
        inputFullReview = findViewById(R.id.inputfullreview_addreview)
        switchPublicShre = findViewById(R.id.switchPublic_addreview)

        btnAddReview_review = findViewById(R.id.btnAdd_review)
        btnBackReview = findViewById(R.id.btnBack_addreview)
        btnCancelReview_review = findViewById(R.id.btnCancel_review)

        // Inicializar estrellas
        stars.add(findViewById(R.id.star1))
        stars.add(findViewById(R.id.star2))
        stars.add(findViewById(R.id.star3))
        stars.add(findViewById(R.id.star4))
        stars.add(findViewById(R.id.star5))
        setupStarRating()
    }
    private fun setupStarRating() {
        stars.forEachIndexed { index, star ->
            star.setOnClickListener {
                selectedRating = index + 1
                updateStarDisplay()
            }
        }
    }

    private fun updateStarDisplay() {
        stars.forEachIndexed { index, star ->
            if (index < selectedRating) {
                star.setImageResource(android.R.drawable.btn_star_big_on)
            } else {
                star.setImageResource(android.R.drawable.btn_star_big_off)
            }
        }
    }

    private fun setupListeners() {
        btnAddReview_review.setOnClickListener {
            if (validateFields()) {
                saveReview()
                val intent = Intent(this, Detail::class.java)
                startActivity(intent)
            }
        }

        btnCancelReview_review.setOnClickListener {
            clearFields()
        }

        btnBackReview.setOnClickListener {
            finish() // Cierra la actividad actual
        }
    }

    private fun validateFields(): Boolean {
        var isValid = true

        // Validar descripción corta
        val shortDescription = inputShortDescription.text.toString().trim()
        if (shortDescription.isEmpty()) {
            inputShortDescription.error = "La descripción corta es obligatoria"
            inputShortDescription.requestFocus()
            isValid = false
        } else if (shortDescription.length < 10) {
            inputShortDescription.error = "La descripción debe tener al menos 10 caracteres"
            inputShortDescription.requestFocus()
            isValid = false
        } else if (shortDescription.length > 100) {
            inputShortDescription.error = "La descripción no puede exceder 100 caracteres"
            inputShortDescription.requestFocus()
            isValid = false
        } else {
            inputShortDescription.error = null
        }

        // Validar reseña completa
        val fullReview = inputFullReview.text.toString().trim()
        if (fullReview.isEmpty()) {
            inputFullReview.error = "La reseña completa es obligatoria"
            if (isValid) inputFullReview.requestFocus()
            isValid = false
        } else if (fullReview.length < 20) {
            inputFullReview.error = "La reseña debe tener al menos 20 caracteres"
            if (isValid) inputFullReview.requestFocus()
            isValid = false
        } else if (fullReview.length > 1000) {
            inputFullReview.error = "La reseña no puede exceder 1000 caracteres"
            if (isValid) inputFullReview.requestFocus()
            isValid = false
        } else {
            inputFullReview.error = null
        }

        // Validar rating
        if (selectedRating == 0) {
            Toast.makeText(this, "Selecciona una calificación de estrellas", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        // Mostrar mensaje general si hay errores
        if (!isValid) {
            Toast.makeText(this, "Por favor, corrige los errores antes de continuar", Toast.LENGTH_SHORT).show()
        }

        return isValid
    }

    private fun clearFields() {
        inputShortDescription.text.clear()
        inputFullReview.text.clear()
        switchPublicShre.isChecked = false

        // Limpiar errores
        inputShortDescription.error = null
        inputFullReview.error = null

        Toast.makeText(this, "Campos limpiados", Toast.LENGTH_SHORT).show()
    }

    private fun saveReview() {
        val shortDescription = inputShortDescription.text.toString().trim()
        val fullReview = inputFullReview.text.toString().trim()
        val isPublic = switchPublicShre.isChecked

        // Obtener el contentId y título del intent
        val contentId = intent.getStringExtra("contentId") ?: ""
        val contentTitle = intent.getStringExtra("contentTitle") ?: ""

        // Obtener el userId de FirebaseAuth
        val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
        if (contentId.isEmpty() || userId.isEmpty()) {
            Toast.makeText(this, "Error: Faltan datos para guardar la reseña", Toast.LENGTH_SHORT).show()
            return
        }

        // Crear la entidad Review (apegado a Review.kt y usando usuario en sesión)
        val review = rodriguez.jairo.proyectofinal_reviewssystem.entities.Review(
            id = java.util.UUID.randomUUID().toString(),
            userId = userId, // usuario en sesión
            contentId = contentId,
            rating = selectedRating,
            titulo = shortDescription,
            review = fullReview,
            compartir = isPublic
        )

        // Guardar en Firestore usando el método toMap() de la entidad Review
        val db = com.google.firebase.Firebase.firestore
        db.collection("reviews").document(review.id)
            .set(review.toMap())
            .addOnSuccessListener {
                Toast.makeText(this, "Reseña guardada exitosamente", Toast.LENGTH_SHORT).show()
                // Regresar a Detail con la info necesaria
                val intent = Intent(this, Detail::class.java)
                intent.putExtra("contentId", contentId)
                intent.putExtra("title", contentTitle)
                // Puedes pasar más extras si lo necesitas (imagen, etc)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al guardar la reseña", Toast.LENGTH_SHORT).show()
            }
    }
}