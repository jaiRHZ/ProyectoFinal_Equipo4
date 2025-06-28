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

class AddReview : AppCompatActivity() {

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

        findViewById<Button>(R.id.btnAdd_review).setOnClickListener {
            val intent = Intent(this, Home::class.java)
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
        switchPublicShre = findViewById(R.id.switchShareReviews)

        btnAddReview_review = findViewById(R.id.btnAdd_review)
        btnBackReview = findViewById(R.id.btnBack_addreview)
        btnCancelReview_review = findViewById(R.id.btnCancel_review)
    }

    private fun setupListeners() {
        btnAddReview_review.setOnClickListener {
            if (validateFields()) {
                // Aquí puedes proceder a guardar la reseña
                saveReview()
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

        // Aquí implementarías la lógica para guardar la reseña
        // Por ejemplo, guardar en base de datos, enviar a servidor, etc.

        Toast.makeText(this, "Reseña guardada exitosamente", Toast.LENGTH_SHORT).show()

        // Opcional: cerrar la actividad después de guardar
        finish()
    }
}