package rodriguez.jairo.proyectofinal_reviewssystem

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class AddContent : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var switchOption1: SwitchCompat
    private lateinit var switchOption2: SwitchCompat
    private lateinit var switchOption3: SwitchCompat
    private lateinit var textViewISBN: TextView
    private lateinit var etISBN: EditText
    private lateinit var etCategory: EditText
    private lateinit var etSynopsis: EditText
    private lateinit var etReview: EditText
    private lateinit var chipGroupTags: ChipGroup
    private lateinit var ivCoverImage: ImageView
    private lateinit var switchShareReviews: SwitchCompat
    private lateinit var btnAdd: Button
    private lateinit var btnCancel: Button

    private val selectedTags = mutableListOf<String>()
    private val stars = mutableListOf<ImageView>()
    private var selectedRating = 0
    private var selectedImageUri: Uri? = null

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            ivCoverImage.setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_content)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeViews()
        setupClickListeners()
    }

    private fun initializeViews() {
        etTitle = findViewById(R.id.etTitle)
        switchOption1 = findViewById(R.id.switchOption1)
        switchOption2 = findViewById(R.id.switchOption2)
        switchOption3 = findViewById(R.id.switchOption3)
        textViewISBN = findViewById(R.id.textViewISBN)
        etISBN = findViewById(R.id.etISBN)
        etCategory = findViewById(R.id.etCategory)
        etSynopsis = findViewById(R.id.etSynopsis)
        etReview = findViewById(R.id.etReview)
        chipGroupTags = findViewById(R.id.chipGroupTags)
        ivCoverImage = findViewById(R.id.ivCoverImage)
        switchShareReviews = findViewById(R.id.switchShareReviews)
        btnAdd = findViewById(R.id.btnAdd)
        btnCancel = findViewById(R.id.btnCancel)

        // Inicializar estrellas
        stars.add(findViewById(R.id.star1))
        stars.add(findViewById(R.id.star2))
        stars.add(findViewById(R.id.star3))
        stars.add(findViewById(R.id.star4))
        stars.add(findViewById(R.id.star5))
    }

    private fun setupClickListeners() {
        setupExclusiveSwitches()
        setupCategorySelector()
        setupTagsListener()
        setupStarRating()
        setupImagePicker()
        setupButtonListeners()
        setupSwitchListener()
    }

    private fun setupButtonListeners() {
        btnAdd.setOnClickListener {
            if (validateForm()) {
                addContent()
            }
        }

        btnCancel.setOnClickListener {
            showCancelConfirmation()
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true

        // Validar título
        if (!validateTitle()) {
            isValid = false
        }

        // Validar tipo de contenido
        if (!validateContentType()) {
            isValid = false
        }

        // Validar ISBN si es libro
        if (switchOption3.isChecked && !validateISBN()) {
            isValid = false
        }

        // Validar categoría
        if (!validateCategory()) {
            isValid = false
        }

        // Validar sinopsis
        if (!validateSynopsis()) {
            isValid = false
        }

        // Validar tags (mejorado con límite máximo)
        if (!validateTags()) {
            isValid = false
        }

        // Validar reseña
        if (!validateReview()) {
            isValid = false
        }

        // Validar calificación
        if (!validateRating()) {
            isValid = false
        }

        // Validar imagen (opcional para AddContent, pero con aviso)
        validateImage()

        return isValid
    }

    private fun validateTitle(): Boolean {
        val title = etTitle.text.toString().trim()

        return when {
            title.isEmpty() -> {
                etTitle.error = "El título es requerido"
                etTitle.requestFocus()
                false
            }
            title.length < 2 -> {
                etTitle.error = "El título debe tener al menos 2 caracteres"
                etTitle.requestFocus()
                false
            }
            title.length > 100 -> {
                etTitle.error = "El título no puede exceder 100 caracteres"
                etTitle.requestFocus()
                false
            }
            else -> {
                etTitle.error = null
                true
            }
        }
    }

    private fun validateContentType(): Boolean {
        val hasSelection = switchOption1.isChecked || switchOption2.isChecked || switchOption3.isChecked

        return if (!hasSelection) {
            showCustomToast("Selecciona un tipo de contenido (Película, Serie o Libro)")
            false
        } else {
            true
        }
    }

    private fun validateISBN(): Boolean {
        val isbn = etISBN.text.toString().trim()

        return when {
            isbn.isEmpty() -> {
                etISBN.error = "El ISBN es requerido para libros"
                etISBN.requestFocus()
                false
            }
            !isValidISBN(isbn) -> {
                etISBN.error = "Formato de ISBN inválido (debe ser ISBN-10 o ISBN-13)"
                etISBN.requestFocus()
                false
            }
            else -> {
                etISBN.error = null
                true
            }
        }
    }

    private fun validateCategory(): Boolean {
        val category = etCategory.text.toString().trim()

        return when {
            category.isEmpty() -> {
                showCustomToast("Selecciona una categoría")
                etCategory.requestFocus()
                false
            }
            else -> true
        }
    }

    private fun validateSynopsis(): Boolean {
        val synopsis = etSynopsis.text.toString().trim()

        return when {
            synopsis.isEmpty() -> {
                etSynopsis.error = "La sinopsis es requerida"
                etSynopsis.requestFocus()
                false
            }
            synopsis.length < 10 -> {
                etSynopsis.error = "La sinopsis debe tener al menos 10 caracteres"
                etSynopsis.requestFocus()
                false
            }
            synopsis.length > 500 -> {
                etSynopsis.error = "La sinopsis no puede exceder 500 caracteres"
                etSynopsis.requestFocus()
                false
            }
            else -> {
                etSynopsis.error = null
                true
            }
        }
    }

    private fun validateTags(): Boolean {
        return when {
            selectedTags.isEmpty() -> {
                showCustomToast("Selecciona al menos un tag")
                false
            }
            selectedTags.size > 5 -> {
                showCustomToast("No puedes seleccionar más de 5 tags")
                false
            }
            else -> true
        }
    }

    private fun validateReview(): Boolean {
        val review = etReview.text.toString().trim()

        return when {
            review.isEmpty() -> {
                etReview.error = "La reseña es requerida"
                etReview.requestFocus()
                false
            }
            review.length < 10 -> {
                etReview.error = "La reseña debe tener al menos 10 caracteres"
                etReview.requestFocus()
                false
            }
            review.length > 1000 -> {
                etReview.error = "La reseña no puede exceder 1000 caracteres"
                etReview.requestFocus()
                false
            }
            else -> {
                etReview.error = null
                true
            }
        }
    }

    private fun validateRating(): Boolean {
        return if (selectedRating == 0) {
            showCustomToast("Selecciona una calificación (1-5 estrellas)")
            false
        } else {
            true
        }
    }

    // NUEVO: Validación opcional de imagen con aviso amigable
    private fun validateImage(): Boolean {
        return if (selectedImageUri == null) {
            showCustomToast("Recomendamos agregar una imagen de portada")
            true // No bloquea el guardado, solo avisa
        } else {
            true
        }
    }

    private fun isValidISBN(isbn: String): Boolean {
        val cleanISBN = isbn.replace("-", "").replace(" ", "")

        return when (cleanISBN.length) {
            10 -> isValidISBN10(cleanISBN)
            13 -> isValidISBN13(cleanISBN)
            else -> false
        }
    }

    private fun isValidISBN10(isbn: String): Boolean {
        if (!isbn.matches(Regex("^[0-9]{9}[0-9X]$"))) return false

        var sum = 0
        for (i in 0..8) {
            sum += (isbn[i].toString().toInt()) * (10 - i)
        }

        val checkDigit = if (isbn[9] == 'X') 10 else isbn[9].toString().toInt()
        return (sum + checkDigit) % 11 == 0
    }

    private fun isValidISBN13(isbn: String): Boolean {
        if (!isbn.matches(Regex("^[0-9]{13}$"))) return false

        var sum = 0
        for (i in isbn.indices) {
            val digit = isbn[i].toString().toInt()
            sum += if (i % 2 == 0) digit else digit * 3
        }

        return sum % 10 == 0
    }

    private fun addContent() {
        val title = etTitle.text.toString().trim()
        val contentType = when {
            switchOption1.isChecked -> "Película"
            switchOption2.isChecked -> "Serie"
            switchOption3.isChecked -> "Libro"
            else -> ""
        }
        val isbn = if (switchOption3.isChecked) etISBN.text.toString().trim() else ""
        val category = etCategory.text.toString().trim()
        val synopsis = etSynopsis.text.toString().trim()
        val review = etReview.text.toString().trim()
        val shareReviews = switchShareReviews.isChecked

        // Crear diálogo con tema personalizado
        AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
            .setTitle("Confirmar adición")
            .setMessage("¿Está seguro de que desea agregar este contenido?")
            .setPositiveButton("Sí") { _, _ ->
                // Lógica para guardar el contenido
                Log.d("AddContent", """
                Contenido agregado:
                Título: $title
                Tipo: $contentType
                ISBN: $isbn
                Categoría: $category
                Sinopsis: $synopsis
                Tags: $selectedTags
                Reseña: $review
                Calificación: $selectedRating estrellas
                Compartir reseñas: $shareReviews
                Imagen: ${selectedImageUri?.toString() ?: "No seleccionada"}
            """.trimIndent())

                showCustomToast("¡Contenido agregado exitosamente!")

                // Navegar al Home solo después de confirmar
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this@AddContent, Home::class.java)
                    startActivity(intent)
                    finish()
                }, 1000)
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
                // No navega - se queda en la pantalla actual
            }
            .show()
    }

    private fun showCancelConfirmation() {
        if (hasUnsavedChanges()) {
            AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
                .setTitle("Cambios sin guardar")
                .setMessage("Tiene información sin guardar. ¿Está seguro de que desea salir?")
                .setPositiveButton("Salir sin guardar") { _, _ ->
                    // Solo aquí navega al Home
                    val intent = Intent(this, Home::class.java)
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("Continuar editando") { dialog, _ ->
                    dialog.dismiss()
                    // No navega - se queda en la pantalla actual
                }
                .setNeutralButton("Guardar y salir") { _, _ ->
                    if (validateForm()) {
                        // Guardar sin mostrar diálogo de confirmación adicional
                        saveContentAndExit()
                    } else {
                        // Si la validación falla, no navega
                        showCustomToast("Por favor corrige los errores antes de guardar")
                    }
                }
                .show()
        } else {
            // Si no hay cambios, navega directamente al Home
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun saveContentAndExit() {
        val title = etTitle.text.toString().trim()
        val contentType = when {
            switchOption1.isChecked -> "Película"
            switchOption2.isChecked -> "Serie"
            switchOption3.isChecked -> "Libro"
            else -> ""
        }
        val isbn = if (switchOption3.isChecked) etISBN.text.toString().trim() else ""
        val category = etCategory.text.toString().trim()
        val synopsis = etSynopsis.text.toString().trim()
        val review = etReview.text.toString().trim()
        val shareReviews = switchShareReviews.isChecked

        // Guardar directamente sin diálogo de confirmación
        Log.d("AddContent", """
        Contenido guardado y saliendo:
        Título: $title
        Tipo: $contentType
        ISBN: $isbn
        Categoría: $category
        Sinopsis: $synopsis
        Tags: $selectedTags
        Reseña: $review
        Calificación: $selectedRating estrellas
        Compartir reseñas: $shareReviews
        Imagen: ${selectedImageUri?.toString() ?: "No seleccionada"}
    """.trimIndent())

        showCustomToast("¡Contenido guardado exitosamente!")

        // Navegar al Home después de guardar
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            finish()
        }, 1000)
    }

    private fun hasUnsavedChanges(): Boolean {
        return etTitle.text.toString().trim().isNotEmpty() ||
                etSynopsis.text.toString().trim().isNotEmpty() ||
                etReview.text.toString().trim().isNotEmpty() ||
                etCategory.text.toString().trim().isNotEmpty() ||
                etISBN.text.toString().trim().isNotEmpty() ||
                selectedTags.isNotEmpty() ||
                switchOption1.isChecked ||
                switchOption2.isChecked ||
                switchOption3.isChecked ||
                selectedImageUri != null ||
                selectedRating > 0
    }

    // NUEVO: Toast personalizado con colores del tema
    private fun showCustomToast(message: String) {
        val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        val view = toast.view
        view?.let {
            // Aplicar colores personalizados al toast
            it.background = ContextCompat.getDrawable(this, R.drawable.toast_background)
            val textView = it.findViewById<TextView>(android.R.id.message)
            textView?.setTextColor(ContextCompat.getColor(this, R.color.white))
        }
        toast.show()
    }


    private fun setupTagsListener() {
        chipGroupTags.setOnCheckedStateChangeListener { group, checkedIds ->
            // Limpiar la lista actual
            selectedTags.clear()

            // Verificar límite máximo
            if (checkedIds.size > 5) {
                showCustomToast("Máximo 5 tags permitidos")
                // Desmarcar el último chip seleccionado
                val lastChipId = checkedIds.last()
                group.check(lastChipId)
                return@setOnCheckedStateChangeListener
            }

            // Agregar tags seleccionados
            checkedIds.forEach { chipId ->
                val chip = findViewById<Chip>(chipId)
                selectedTags.add(chip.text.toString())
            }

            Log.d("SelectedTags", "Tags seleccionados: $selectedTags")
        }
    }



    // Función auxiliar para convertir dp a px
    private fun Int.dpToPx(): Float {
        return this * resources.displayMetrics.density
    }

    private fun setupExclusiveSwitches() {
        switchOption1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                switchOption2.isChecked = false
                switchOption3.isChecked = false
                hideAdditionalFields()
            }
        }

        switchOption2.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                switchOption1.isChecked = false
                switchOption3.isChecked = false
                hideAdditionalFields()
            }
        }

        switchOption3.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                switchOption1.isChecked = false
                switchOption2.isChecked = false
                showAdditionalFields()
            } else {
                hideAdditionalFields()
            }
        }
    }

    private fun showAdditionalFields() {
        textViewISBN.visibility = View.VISIBLE
        etISBN.visibility = View.VISIBLE
    }

    private fun hideAdditionalFields() {
        textViewISBN.visibility = View.GONE
        etISBN.visibility = View.GONE
        etISBN.setText("")
        etISBN.error = null
    }

    private fun setupCategorySelector() {
        etCategory.setOnClickListener {
            showCategorySelector()
        }
    }

    // MEJORADO: Category selector con tema personalizado
    private fun showCategorySelector() {
        val categoryOptions = arrayOf(
            "Acción", "Aventura", "Comedia", "Drama", "Terror", "Thriller",
            "Romance", "Ciencia Ficción", "Fantasía", "Animación", "Documental",
            "Musical", "Biografía", "Historia", "Misterio", "Crimen", "Familia",
            "Guerra", "Deportes", "Suspenso", "Western", "Noir"
        )

        val dialog = AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
            .setTitle("Seleccionar categoría")
            .setItems(categoryOptions) { _, which ->
                etCategory.setText(categoryOptions[which])
            }
            .create()

        dialog.show()

        // Forzar color blanco en el ListView
        dialog.listView?.let { listView ->
            listView.setBackgroundColor(ContextCompat.getColor(this, R.color.fondoNegro))
            for (i in 0 until listView.count) {
                listView.getChildAt(i)?.let { child ->
                    (child as? TextView)?.setTextColor(ContextCompat.getColor(this, R.color.white))
                }
            }
        }
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

    private fun setupImagePicker() {
        ivCoverImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }
    }

    private fun setupSwitchListener() {
        switchShareReviews.setOnCheckedChangeListener { _, isChecked ->
            Log.d("ShareSwitch", "Share reviews checked: $isChecked")
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        showCancelConfirmation()
    }
}