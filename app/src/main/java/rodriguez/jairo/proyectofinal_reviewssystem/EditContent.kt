package rodriguez.jairo.proyectofinal_reviewssystem

import android.content.Intent
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
import com.google.android.material.switchmaterial.SwitchMaterial

class EditContent : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var chipGroupContentType: ChipGroup
    private lateinit var chipMovie: Chip
    private lateinit var chipSerie: Chip
    private lateinit var chipBook: Chip
    private lateinit var textViewISBN: TextView
    private lateinit var etISBN: EditText
    private lateinit var etCategory: EditText
    private lateinit var etSynopsis: EditText
    private lateinit var chipGroupTags: ChipGroup
    private lateinit var ivCoverImage: ImageView
    private lateinit var btnApplyChanges: Button
    private lateinit var btnCancel: Button
    private lateinit var btnAddCustomTag: Button

    private val selectedTags = mutableListOf<String>()
    private var selectedImageUri: Uri? = null

    // Lista de tags predefinidos
    private val predefinedTags = listOf(
        "alternate history", "anime", "based on book", "based on play", "based on comic",
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
        setContentView(R.layout.activity_edit_content)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeViews()
        setupClickListeners()
        setupPredefinedTags()
        loadExistingData()
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
        chipGroupTags = findViewById(R.id.chipGroupTags)
        ivCoverImage = findViewById(R.id.ivCoverImage)
        btnApplyChanges = findViewById(R.id.btnApplyChanges)
        btnCancel = findViewById(R.id.btnCancel)
        btnAddCustomTag = findViewById(R.id.btnAddCustomTag)
    }

    // Configurar tags predefinidos dinámicamente
    private fun setupPredefinedTags() {
        chipGroupTags.removeAllViews()

        predefinedTags.forEach { tagText ->
            addChipToGroup(tagText, false)
        }
    }

    // Función para agregar chips al grupo
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
                    showToast("Maximum 5 tags allowed")
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
                showToast("Tag '$text' removed")
            }
        }

        chipGroupTags.addView(chip)
    }

    private fun loadExistingData() {
        // Aquí cargarías los datos existentes del contenido a editar
        // Por ejemplo:
        // etTitle.setText(existingContent.title)
        // if (existingContent.type == "Película") chipMovie.isChecked = true
        // etc.
    }

    private fun setupClickListeners() {
        setupContentTypeChips()
        setupCategorySelector()
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

    // Configurar botón de tag personalizado
    private fun setupCustomTagButton() {
        btnAddCustomTag.setOnClickListener {
            showAddCustomTagDialog()
        }
    }

    // Mostrar diálogo para agregar tag personalizado
    private fun showAddCustomTagDialog() {
        val editText = EditText(this)
        editText.hint = "Enter your custom tag"
        editText.maxLines = 1
        editText.setTextColor(ContextCompat.getColor(this, R.color.white))
        editText.setHintTextColor(ContextCompat.getColor(this, R.color.subtituloGris))

        // Configurar padding
        val padding = 16.dpToPx().toInt()
        editText.setPadding(padding, padding, padding, padding)

        AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
            .setTitle("Add Custom Tag")
            .setMessage("Enter a custom tag for your content:")
            .setView(editText)
            .setPositiveButton("Add") { _, _ ->
                val customTag = editText.text.toString().trim()
                if (validateCustomTag(customTag)) {
                    addChipToGroup(customTag, true)
                    showToast("Tag '$customTag' added")
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    // Validar tag personalizado
    private fun validateCustomTag(tag: String): Boolean {
        return when {
            tag.isEmpty() -> {
                showToast("Tag cannot be empty")
                false
            }
            tag.length < 2 -> {
                showToast("Tag must have at least 2 characters")
                false
            }
            tag.length > 20 -> {
                showToast("Tag cannot exceed 20 characters")
                false
            }
            tagAlreadyExists(tag) -> {
                showToast("This tag already exists")
                false
            }
            else -> true
        }
    }

    // Verificar si el tag ya existe
    private fun tagAlreadyExists(tag: String): Boolean {
        for (i in 0 until chipGroupTags.childCount) {
            val chip = chipGroupTags.getChildAt(i) as Chip
            if (chip.text.toString().equals(tag, ignoreCase = true)) {
                return true
            }
        }
        return false
    }

    private fun setupButtonListeners() {
        btnApplyChanges.setOnClickListener {
            if (validateForm()) applyChanges()
        }

        btnCancel.setOnClickListener {
            showCancelConfirmation()
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true

        if (!validateTitle()) isValid = false
        if (!validateContentType()) isValid = false
        if (chipBook.isChecked && !validateISBN()) isValid = false
        if (!validateCategory()) isValid = false
        if (!validateSynopsis()) isValid = false
        if (!validateTags()) isValid = false
        validateImage()

        return isValid
    }

    private fun validateTitle(): Boolean {
        val title = etTitle.text.toString().trim()
        return when {
            title.isEmpty() -> {
                etTitle.error = "Title is required"
                etTitle.requestFocus(); false
            }
            title.length < 2 -> {
                etTitle.error = "Must have at least 2 characters"
                etTitle.requestFocus(); false
            }
            title.length > 100 -> {
                etTitle.error = "Cannot exceed 100 characters"
                etTitle.requestFocus(); false
            }
            else -> {
                etTitle.error = null
                true
            }
        }
    }

    // Validar tipo de contenido usando chips
    private fun validateContentType(): Boolean {
        val hasSelection = chipMovie.isChecked || chipSerie.isChecked || chipBook.isChecked

        return if (!hasSelection) {
            showToast("Select a content type (Movie, Series or Book)")
            false
        } else {
            true
        }
    }

    private fun validateISBN(): Boolean {
        val isbn = etISBN.text.toString().trim()
        return when {
            isbn.isEmpty() -> {
                etISBN.error = "ISBN is required"
                etISBN.requestFocus(); false
            }
            !isValidISBN(isbn) -> {
                etISBN.error = "Invalid ISBN (ISBN-10 or ISBN-13)"
                etISBN.requestFocus(); false
            }
            else -> {
                etISBN.error = null
                true
            }
        }
    }

    private fun validateCategory(): Boolean {
        val category = etCategory.text.toString().trim()
        return if (category.isEmpty()) {
            showToast("Select a category")
            etCategory.requestFocus(); false
        } else true
    }

    private fun validateSynopsis(): Boolean {
        val synopsis = etSynopsis.text.toString().trim()
        return when {
            synopsis.isEmpty() -> {
                etSynopsis.error = "Synopsis is required"
                etSynopsis.requestFocus(); false
            }
            synopsis.length < 10 -> {
                etSynopsis.error = "Minimum 10 characters"
                etSynopsis.requestFocus(); false
            }
            synopsis.length > 500 -> {
                etSynopsis.error = "Maximum 500 characters"
                etSynopsis.requestFocus(); false
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
                showToast("Select at least one tag")
                false
            }
            selectedTags.size > 5 -> {
                showToast("Maximum 5 tags allowed")
                false
            }
            else -> true
        }
    }

    private fun validateImage(): Boolean {
        return if (selectedImageUri == null) {
            showToast("We recommend keeping or updating the cover image")
            true
        } else true
    }

    private fun isValidISBN(isbn: String): Boolean {
        val clean = isbn.replace("-", "").replace(" ", "")
        return when (clean.length) {
            10 -> isValidISBN10(clean)
            13 -> isValidISBN13(clean)
            else -> false
        }
    }

    private fun isValidISBN10(isbn: String): Boolean {
        if (!isbn.matches(Regex("^[0-9]{9}[0-9X]$"))) return false
        val sum = isbn.take(9).mapIndexed { i, c -> (10 - i) * c.digitToInt() }.sum() +
                if (isbn[9] == 'X') 10 else isbn[9].digitToInt()
        return sum % 11 == 0
    }

    private fun isValidISBN13(isbn: String): Boolean {
        if (!isbn.matches(Regex("^[0-9]{13}$"))) return false
        val sum = isbn.mapIndexed { i, c -> c.digitToInt() * if (i % 2 == 0) 1 else 3 }.sum()
        return sum % 10 == 0
    }

    private fun applyChanges() {
        val title = etTitle.text.toString().trim()
        val contentType = when {
            chipMovie.isChecked -> "Movie"
            chipSerie.isChecked -> "Series"
            chipBook.isChecked -> "Book"
            else -> ""
        }
        val isbn = if (chipBook.isChecked) etISBN.text.toString().trim() else ""
        val category = etCategory.text.toString().trim()
        val synopsis = etSynopsis.text.toString().trim()

        AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
            .setTitle("Confirm changes")
            .setMessage("Are you sure you want to apply the changes?")
            .setPositiveButton("Yes") { _, _ ->
                Log.d("EditContent", """
                    Título: $title
                    Tipo: $contentType
                    ISBN: $isbn
                    Categoría: $category
                    Sinopsis: $synopsis
                    Tags: $selectedTags
                    Imagen: ${selectedImageUri ?: "No modificada"}
                """.trimIndent())
                showToast("Changes applied successfully!")

                Handler(Looper.getMainLooper()).postDelayed({
                    startActivity(Intent(this, Home::class.java))
                    finish()
                }, 1000)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showCancelConfirmation() {
        if (hasUnsavedChanges()) {
            AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
                .setTitle("Unsaved changes")
                .setMessage("You have unsaved changes. Exit anyway?")
                .setPositiveButton("Exit without saving") { _, _ ->
                    startActivity(Intent(this, Home::class.java))
                    finish()
                }
                .setNegativeButton("Continue editing", null)
                .setNeutralButton("Save and exit") { _, _ ->
                    if (validateForm()) saveChangesAndExit()
                    else showToast("Fix errors before saving")
                }
                .show()
        } else {
            startActivity(Intent(this, Home::class.java))
            finish()
        }
    }

    private fun saveChangesAndExit() {
        showToast("Changes saved successfully!")
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, Home::class.java))
            finish()
        }, 1000)
    }

    // Verificar cambios usando chips
    private fun hasUnsavedChanges(): Boolean {
        return etTitle.text.toString().trim().isNotEmpty() ||
                etSynopsis.text.toString().trim().isNotEmpty() ||
                etCategory.text.toString().trim().isNotEmpty() ||
                etISBN.text.toString().trim().isNotEmpty() ||
                selectedTags.isNotEmpty() ||
                chipMovie.isChecked ||
                chipSerie.isChecked ||
                chipBook.isChecked ||
                selectedImageUri != null
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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
        etCategory.setOnClickListener { showCategorySelector() }
    }

    private fun showCategorySelector() {
        val options = arrayOf("Action", "Adventure", "Comedy", "Drama", "Horror", "Thriller",
            "Romance", "Science Fiction", "Fantasy", "Animation", "Documentary",
            "Musical", "Biography", "History", "Mystery", "Crime", "Family",
            "War", "Sports", "Suspense", "Western", "Noir")

        val dialog = AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
            .setTitle("Select category")
            .setItems(options) { _, which -> etCategory.setText(options[which]) }
            .create()

        dialog.show()
    }

    private fun setupImagePicker() {
        ivCoverImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        showCancelConfirmation()
    }
}