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
    private lateinit var chipGroupContentType: ChipGroup
    private lateinit var chipMovie: Chip
    private lateinit var chipSerie: Chip
    private lateinit var chipBook: Chip
    private lateinit var textViewISBN: TextView
    private lateinit var etISBN: EditText
    private lateinit var etCategory: EditText
    private lateinit var etSynopsis: EditText
    private lateinit var etTitleReview: EditText
    private lateinit var etReview: EditText
    private lateinit var chipGroupTags: ChipGroup
    private lateinit var ivCoverImage: ImageView
    private lateinit var switchShareReviews: SwitchCompat
    private lateinit var btnAdd: Button
    private lateinit var btnCancel: Button
    private lateinit var btnAddCustomTag: Button

    private val selectedTags = mutableListOf<String>()
    private val stars = mutableListOf<ImageView>()
    private var selectedRating = 0
    private var selectedImageUri: Uri? = null

    //  Lista de tags predefinidos
    private val predefinedTags = listOf(
        "action hero", "alternate history", "anime", "based on book", "based on play", "based on comic",
        "based on comic book", "based on novel", "based on story", "based on manga", "experimental film",
        "independent film", "remake", "plot twist", "cult film", "bollywood", "post apocalypse"
    )

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
        setupPredefinedTags()
    }

    private fun initializeViews() {
        etTitle = findViewById(R.id.etTitle)
        chipGroupContentType = findViewById(R.id.chipGroupContentType)
        chipMovie = findViewById(R.id.chipMovie)
        chipSerie = findViewById(R.id.chipSerie)
        chipBook = findViewById(R.id.chipBook)
        textViewISBN = findViewById(R.id.textViewISBN)
        etISBN = findViewById(R.id.etISBN)
        etCategory = findViewById(R.id.etCategory)
        etSynopsis = findViewById(R.id.etSynopsis)
        etTitleReview = findViewById(R.id.etTitleReview)
        etReview = findViewById(R.id.etReview)
        chipGroupTags = findViewById(R.id.chipGroupTags)
        ivCoverImage = findViewById(R.id.ivCoverImage)
        switchShareReviews = findViewById(R.id.switchShareReviews)
        btnAdd = findViewById(R.id.btnAdd)
        btnCancel = findViewById(R.id.btnCancel)
        btnAddCustomTag = findViewById(R.id.btnAddCustomTag)

        // Inicializar estrellas
        stars.add(findViewById(R.id.star1))
        stars.add(findViewById(R.id.star2))
        stars.add(findViewById(R.id.star3))
        stars.add(findViewById(R.id.star4))
        stars.add(findViewById(R.id.star5))
    }

    //  Configurar tags predefinidos dinámicamente
    private fun setupPredefinedTags() {
        chipGroupTags.removeAllViews()

        predefinedTags.forEach { tagText ->
            addChipToGroup(tagText, false)
        }
    }

    //  Función para agregar chips al grupo
    private fun addChipToGroup(text: String, isCustom: Boolean = false) {
        val chip = Chip(this)
        chip.text = text
        chip.isCheckable = true
        chip.isClickable = true
        chip.isCloseIconVisible = isCustom // Solo los tags personalizados pueden eliminarse

        // Aplicar estilo personalizado
        chip.setChipBackgroundColorResource(R.color.chip_background_selector)
        chip.setTextColor(ContextCompat.getColorStateList(this, R.color.chip_text_color_selector))
        chip.chipStrokeColor = ContextCompat.getColorStateList(this, R.color.chip_stroke_selector)
        chip.chipStrokeWidth = 2.dpToPx()
        chip.textSize = 14f

        // Listener para selección/deselección
        chip.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (selectedTags.size >= 5) {
                    showCustomToast("Máximo 5 tags permitidos")
                    chip.isChecked = false
                    return@setOnCheckedChangeListener
                }
                selectedTags.add(text)
            } else {
                selectedTags.remove(text)
            }
            Log.d("SelectedTags", "Tags seleccionados: $selectedTags")
        }

        // Listener para eliminar tags personalizados
        if (isCustom) {
            chip.setOnCloseIconClickListener {
                if (chip.isChecked) {
                    selectedTags.remove(text)
                }
                chipGroupTags.removeView(chip)
                showCustomToast("Tag '$text' eliminado")
            }
        }

        chipGroupTags.addView(chip)
    }

    // Mostrar diálogo para agregar tag personalizado
    private fun showAddCustomTagDialog() {
        val editText = EditText(this)
        editText.hint = "Escribe tu tag personalizado"
        editText.maxLines = 1
        editText.setTextColor(ContextCompat.getColor(this, R.color.white))
        editText.setHintTextColor(ContextCompat.getColor(this, R.color.subtituloGris))

        // Configurar padding
        val padding = 16.dpToPx().toInt()
        editText.setPadding(padding, padding, padding, padding)

        AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
            .setTitle("Agregar Tag Personalizado")
            .setMessage("Ingresa un tag personalizado para tu contenido:")
            .setView(editText)
            .setPositiveButton("Agregar") { _, _ ->
                val customTag = editText.text.toString().trim()
                if (validateCustomTag(customTag)) {
                    addChipToGroup(customTag, true)
                    showCustomToast("Tag '$customTag' agregado")
                }
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    //  Validar tag personalizado
    private fun validateCustomTag(tag: String): Boolean {
        return when {
            tag.isEmpty() -> {
                showCustomToast("El tag no puede estar vacío")
                false
            }
            tag.length < 2 -> {
                showCustomToast("El tag debe tener al menos 2 caracteres")
                false
            }
            tag.length > 20 -> {
                showCustomToast("El tag no puede exceder 20 caracteres")
                false
            }
            tagAlreadyExists(tag) -> {
                showCustomToast("Este tag ya existe")
                false
            }
            else -> true
        }
    }

    // NUEVO: Verificar si el tag ya existe
    private fun tagAlreadyExists(tag: String): Boolean {
        for (i in 0 until chipGroupTags.childCount) {
            val chip = chipGroupTags.getChildAt(i) as Chip
            if (chip.text.toString().equals(tag, ignoreCase = true)) {
                return true
            }
        }
        return false
    }

    private fun setupClickListeners() {
        setupContentTypeChips()
        setupCategorySelector()
        setupStarRating()
        setupImagePicker()
        setupButtonListeners()
        setupCustomTagButton()
    }

    // Configurar ChipGroup para tipo de contenido
    private fun setupContentTypeChips() {
        // Configurar selección única para el tipo de contenido
        chipGroupContentType.isSingleSelection = true

        // Listeners para cada chip
        chipMovie.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                hideAdditionalFields()
                Log.d("ContentType", "Película seleccionada")
            }
        }

        chipSerie.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                hideAdditionalFields()
                Log.d("ContentType", "Serie seleccionada")
            }
        }

        chipBook.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showAdditionalFields()
                Log.d("ContentType", "Libro seleccionado")
            } else {
                hideAdditionalFields()
            }
        }
    }

    // NUEVO: Configurar botón de tag personalizado
    private fun setupCustomTagButton() {
        btnAddCustomTag.setOnClickListener {
            showAddCustomTagDialog()
        }
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
        if (chipBook.isChecked && !validateISBN()) {
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

        // Validar tags
        if (!validateTags()) {
            isValid = false
        }

        // Validar título de reseña
        if (!validateTitleReview()) {
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

        // Validar imagen
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

    // MODIFICADO: Validar tipo de contenido usando chips
    private fun validateContentType(): Boolean {
        val hasSelection = chipMovie.isChecked || chipSerie.isChecked || chipBook.isChecked

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

    private fun validateTitleReview(): Boolean {
        val titleReview = etTitleReview.text.toString().trim()

        return when {
            titleReview.isEmpty() -> {
                etTitleReview.error = "El título de la reseña es requerido"
                etTitleReview.requestFocus()
                false
            }
            titleReview.length < 3 -> {
                etTitleReview.error = "El título de la reseña debe tener al menos 3 caracteres"
                etTitleReview.requestFocus()
                false
            }
            titleReview.length > 80 -> {
                etTitleReview.error = "El título de la reseña no puede exceder 80 caracteres"
                etTitleReview.requestFocus()
                false
            }
            else -> {
                etTitleReview.error = null
                true
            }
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

    private fun validateImage(): Boolean {
        return if (selectedImageUri == null) {
            showCustomToast("Recomendamos agregar una imagen de portada")
            true
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
            chipMovie.isChecked -> "Película"
            chipSerie.isChecked -> "Serie"
            chipBook.isChecked -> "Libro"
            else -> ""
        }
        val isbn = if (chipBook.isChecked) etISBN.text.toString().trim() else ""
        val category = etCategory.text.toString().trim()
        val synopsis = etSynopsis.text.toString().trim()
        val titleReview = etTitleReview.text.toString().trim()
        val review = etReview.text.toString().trim()
        val shareReviews = switchShareReviews.isChecked

        AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
            .setTitle("Confirmar adición")
            .setMessage("¿Está seguro de que desea agregar este contenido?")
            .setPositiveButton("Sí") { _, _ ->
                Log.d("AddContent", """
                Contenido agregado:
                Título: $title
                Tipo: $contentType
                ISBN: $isbn
                Categoría: $category
                Sinopsis: $synopsis
                Tags: $selectedTags
                Título de reseña: $titleReview
                Reseña: $review
                Calificación: $selectedRating estrellas
                Compartir reseñas: $shareReviews
                Imagen: ${selectedImageUri?.toString() ?: "No seleccionada"}
            """.trimIndent())

                showCustomToast("¡Contenido agregado exitosamente!")

                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this@AddContent, Home::class.java)
                    startActivity(intent)
                    finish()
                }, 1000)
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showCancelConfirmation() {
        if (hasUnsavedChanges()) {
            AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
                .setTitle("Cambios sin guardar")
                .setMessage("Tiene información sin guardar. ¿Está seguro de que desea salir?")
                .setPositiveButton("Salir sin guardar") { _, _ ->
                    val intent = Intent(this, Home::class.java)
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("Continuar editando") { dialog, _ ->
                    dialog.dismiss()
                }
                .setNeutralButton("Guardar y salir") { _, _ ->
                    if (validateForm()) {
                        saveContentAndExit()
                    } else {
                        showCustomToast("Por favor corrige los errores antes de guardar")
                    }
                }
                .show()
        } else {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            finish()
        }
    }

    // MODIFICADO: Usar chips para determinar tipo de contenido
    private fun saveContentAndExit() {
        val title = etTitle.text.toString().trim()
        val contentType = when {
            chipMovie.isChecked -> "Película"
            chipSerie.isChecked -> "Serie"
            chipBook.isChecked -> "Libro"
            else -> ""
        }
        val isbn = if (chipBook.isChecked) etISBN.text.toString().trim() else ""
        val category = etCategory.text.toString().trim()
        val synopsis = etSynopsis.text.toString().trim()
        val titleReview = etTitleReview.text.toString().trim()
        val review = etReview.text.toString().trim()
        val shareReviews = switchShareReviews.isChecked

        Log.d("AddContent", """
        Contenido guardado y saliendo:
        Título: $title
        Tipo: $contentType
        ISBN: $isbn
        Categoría: $category
        Sinopsis: $synopsis
        Tags: $selectedTags
        Título de reseña: $titleReview
        Reseña: $review
        Calificación: $selectedRating estrellas
        Compartir reseñas: $shareReviews
        Imagen: ${selectedImageUri?.toString() ?: "No seleccionada"}
    """.trimIndent())

        showCustomToast("¡Contenido guardado exitosamente!")

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            finish()
        }, 1000)
    }

    // MODIFICADO: Verificar cambios usando chips
    private fun hasUnsavedChanges(): Boolean {
        return etTitle.text.toString().trim().isNotEmpty() ||
                etSynopsis.text.toString().trim().isNotEmpty() ||
                etTitleReview.text.toString().trim().isNotEmpty() ||
                etReview.text.toString().trim().isNotEmpty() ||
                etCategory.text.toString().trim().isNotEmpty() ||
                etISBN.text.toString().trim().isNotEmpty() ||
                selectedTags.isNotEmpty() ||
                chipMovie.isChecked ||
                chipSerie.isChecked ||
                chipBook.isChecked ||
                selectedImageUri != null ||
                selectedRating > 0
    }

    private fun showCustomToast(message: String) {
        val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        val view = toast.view
        view?.let {
            it.background = ContextCompat.getDrawable(this, R.drawable.toast_background)
            val textView = it.findViewById<TextView>(android.R.id.message)
            textView?.setTextColor(ContextCompat.getColor(this, R.color.white))
        }
        toast.show()
    }



    private fun Int.dpToPx(): Float {
        return this * resources.displayMetrics.density
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

    private fun showCategorySelector() {
        val categoryOptions = arrayOf(
            "Action", "Adventure", "Comedy", "Drama", "Horror", "Thriller",
            "Romance", "Science Fiction", "Fantasy", "Animation", "Documentary",
            "Musical", "Biography", "History", "Mystery", "Crime", "Family",
            "War", "Sports", "Suspense", "Western", "Noir"
        )

        val dialog = AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
            .setTitle("Seleccionar categoría")
            .setItems(categoryOptions) { _, which ->
                etCategory.setText(categoryOptions[which])
            }
            .create()

        dialog.show()

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