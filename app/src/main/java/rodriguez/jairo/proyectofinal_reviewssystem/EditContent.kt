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
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import rodriguez.jairo.proyectofinal_reviewssystem.entities.Content
import rodriguez.jairo.proyectofinal_reviewssystem.entities.Tag
import rodriguez.jairo.proyectofinal_reviewssystem.viewmodels.ContentViewModel
import rodriguez.jairo.proyectofinal_reviewssystem.viewmodels.TagViewModel
import rodriguez.jairo.proyectofinal_reviewssystem.viewmodels.UserViewModel
import java.util.UUID

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

    // Firebase ViewModel
    private lateinit var contentViewModel: ContentViewModel
    private lateinit var tagViewModel: TagViewModel
    private lateinit var userViewModel: UserViewModel

    private val selectedTags = mutableListOf<String>()
    private val stars = mutableListOf<ImageView>()
    private var selectedRating = 0
    private var selectedImageUri: Uri? = null
    private var currentContent: Content? = null
    private var contentId: String? = null
    private var originalImageUrl: String = ""

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

        // Obtener ID del contenido a editar
        contentId = intent.getStringExtra("CONTENT_ID")
        if (contentId.isNullOrEmpty()) {
            showCustomToast("Error: No content ID provided")
            finish()
            return
        }

        // Inicializar ViewModel
        contentViewModel = ViewModelProvider(this)[ContentViewModel::class.java]
        tagViewModel = ViewModelProvider(this)[TagViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        initializeViews()
        setupClickListeners()

        initCloudinary()
        loadExistingData()
    }

    private fun initCloudinary() {
        val config: MutableMap<String, String> = HashMap()
        config["cloud_name"] = "dob719uzm"
        MediaManager.init(this, config)
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

        // Inicializar estrellas
        stars.add(findViewById(R.id.star1))
        stars.add(findViewById(R.id.star2))
        stars.add(findViewById(R.id.star3))
        stars.add(findViewById(R.id.star4))
        stars.add(findViewById(R.id.star5))
    }

    private fun setupTagsFromFirebase() {
        tagViewModel.listaTags.observe(this) { tags ->
            if (tags.isNotEmpty()) {
                setupTagsWithExistingData(tags)
            }
        }
    }

    private fun setupTagsWithExistingData(allTags: List<Tag>) {
        chipGroupTags.removeAllViews()

        allTags.forEach { tag ->
            val isSelected = currentContent?.tagIds?.contains(tag.id) == true
            addChipToGroup(tag.nombre, false, isSelected)
        }
    }

    private fun loadExistingData() {
        contentViewModel.listaContenidos.observe(this) { contents ->
            currentContent = contents.find { it.id == contentId }
            currentContent?.let { content ->
                populateFields(content)
                setupTagsFromFirebase()
            }
        }
    }

    private fun populateFields(content: Content) {
        // Cargar datos básicos
        etTitle.setText(content.titulo)
        etSynopsis.setText(content.sinopsis)
        etCategory.setText(content.categoria)

        // Cargar tipo de contenido
        when (content.type) {
            "movies" -> chipMovie.isChecked = true
            "series" -> chipSerie.isChecked = true
            "books" -> {
                chipBook.isChecked = true
                showAdditionalFields()
                content.isbn?.let { etISBN.setText(it) }
            }
        }

        // Cargar rating
        selectedRating = content.estrellas
        updateStarDisplay()



        // Los tags se cargarán cuando se carguen desde Firebase
    }

    private fun addChipToGroup(text: String, isCustom: Boolean = false, isSelected: Boolean = false) {
        val chip = Chip(this)
        chip.text = text
        chip.isCheckable = true
        chip.isClickable = true
        chip.isCloseIconVisible = isCustom
        chip.isChecked = isSelected

        // Aplicar estilo
        chip.setChipBackgroundColorResource(R.color.chip_background_selector)
        chip.setTextColor(ContextCompat.getColorStateList(this, R.color.chip_text_color_selector))
        chip.chipStrokeColor = ContextCompat.getColorStateList(this, R.color.chip_stroke_selector)
        chip.chipStrokeWidth = 2.dpToPx()
        chip.textSize = 14f

        // Si está seleccionado inicialmente, agregarlo a la lista
        if (isSelected) {
            selectedTags.add(text)
        }

        chip.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (selectedTags.size >= 5) {
                    showCustomToast("Maximum 5 tags allowed")
                    chip.isChecked = false
                    return@setOnCheckedChangeListener
                }
                selectedTags.add(text)
            } else {
                selectedTags.remove(text)
            }
        }

        if (isCustom) {
            chip.setOnCloseIconClickListener {
                if (chip.isChecked) {
                    selectedTags.remove(text)
                }
                chipGroupTags.removeView(chip)
                showCustomToast("Tag '$text' removed")
            }
        }

        chipGroupTags.addView(chip)
    }

    private fun setupClickListeners() {
        setupContentTypeChips()
        setupCategorySelector()
        setupStarRating()
        setupImagePicker()
        setupButtonListeners()
        setupCustomTagButton()
    }

    private fun setupContentTypeChips() {
        chipGroupContentType.isSingleSelection = true

        chipMovie.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                hideAdditionalFields()
            }
        }

        chipSerie.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                hideAdditionalFields()
            }
        }

        chipBook.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showAdditionalFields()
            } else {
                hideAdditionalFields()
            }
        }
    }

    private fun setupCustomTagButton() {
        btnAddCustomTag.setOnClickListener {
            showAddCustomTagDialog()
        }
    }

    private fun showAddCustomTagDialog() {
        val editText = EditText(this)
        editText.hint = "Write your custom tag"
        editText.maxLines = 1
        editText.setTextColor(ContextCompat.getColor(this, R.color.white))
        editText.setHintTextColor(ContextCompat.getColor(this, R.color.subtituloGris))

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
                    showCustomToast("Tag '$customTag' added")
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun validateCustomTag(tag: String): Boolean {
        return when {
            tag.isEmpty() -> {
                showCustomToast("Tag cannot be empty")
                false
            }
            tag.length < 2 -> {
                showCustomToast("Tag must have at least 2 characters")
                false
            }
            tag.length > 20 -> {
                showCustomToast("Tag cannot exceed 20 characters")
                false
            }
            tagAlreadyExists(tag) -> {
                showCustomToast("This tag already exists")
                false
            }
            else -> true
        }
    }

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
            if (validateForm()) {
                applyChanges()
            }
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
        if (!validateRating()) isValid = false

        validateImage()
        return isValid
    }

    private fun validateTitle(): Boolean {
        val title = etTitle.text.toString().trim()
        return when {
            title.isEmpty() -> {
                etTitle.error = "Title is required"
                etTitle.requestFocus()
                false
            }
            title.length < 2 -> {
                etTitle.error = "Title must have at least 2 characters"
                etTitle.requestFocus()
                false
            }
            title.length > 100 -> {
                etTitle.error = "Title cannot exceed 100 characters"
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
        val hasSelection = chipMovie.isChecked || chipSerie.isChecked || chipBook.isChecked
        return if (!hasSelection) {
            showCustomToast("Select a content type (Movie, Series or Book)")
            false
        } else {
            true
        }
    }

    private fun validateISBN(): Boolean {
        val isbn = etISBN.text.toString().trim()
        return when {
            isbn.isEmpty() -> {
                etISBN.error = "ISBN is required for books"
                etISBN.requestFocus()
                false
            }
            !isValidISBN(isbn) -> {
                etISBN.error = "Invalid ISBN format (must be ISBN-10 or ISBN-13)"
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
                showCustomToast("Select a category")
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
                etSynopsis.error = "Synopsis is required"
                etSynopsis.requestFocus()
                false
            }
            synopsis.length < 10 -> {
                etSynopsis.error = "Synopsis must have at least 10 characters"
                etSynopsis.requestFocus()
                false
            }
            synopsis.length > 500 -> {
                etSynopsis.error = "Synopsis cannot exceed 500 characters"
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
                showCustomToast("Select at least one tag")
                false
            }
            selectedTags.size > 5 -> {
                showCustomToast("You cannot select more than 5 tags")
                false
            }
            else -> true
        }
    }

    private fun validateRating(): Boolean {
        return if (selectedRating == 0) {
            showCustomToast("Select a rating (1-5 stars)")
            false
        } else {
            true
        }
    }

    private fun validateImage(): Boolean {
        return if (selectedImageUri == null && originalImageUrl.isEmpty()) {
            showCustomToast("Please select an image")
            false
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

    private fun applyChanges() {
        if (selectedImageUri != null) {
            // Subir nueva imagen
            MediaManager.get().upload(selectedImageUri)
                .unsigned("reviewsystem-upload")
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String?) {
                        showCustomToast("Uploading image...")
                    }

                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}

                    override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                        val imageUrl = resultData?.get("secure_url") as? String ?: ""
                        updateContentWithURL(imageUrl)
                    }

                    override fun onError(requestId: String?, error: ErrorInfo?) {
                        showCustomToast("Image upload failed")
                    }

                    override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
                }).dispatch()
        } else {
            // Si no hay imagen nueva, actualizar sin imagen o mostrar error
            updateContentWithURL(originalImageUrl)
        }
    }

    private fun updateContentWithURL(imageUrl: String) {
        val title = etTitle.text.toString().trim()
        val contentType = when {
            chipMovie.isChecked -> "movies"
            chipSerie.isChecked -> "series"
            chipBook.isChecked -> "books"
            else -> ""
        }
        val isbn = if (chipBook.isChecked) etISBN.text.toString().trim() else ""
        val category = etCategory.text.toString().trim()
        val synopsis = etSynopsis.text.toString().trim()

        // Procesar tags
        val tagIds = mutableListOf<String>()
        selectedTags.forEach { tagName ->
            val existingTag = tagViewModel.listaTags.value?.find { it.nombre.equals(tagName, true) }
            if (existingTag != null) {
                tagIds.add(existingTag.id)
            } else {
                val newTag = Tag(id = UUID.randomUUID().toString(), nombre = tagName)
                tagViewModel.agregarTags(newTag)
                tagIds.add(newTag.id)
            }
        }

        // Actualizar contenido existente
        currentContent?.let { content ->
            val updatedContent = content.copy(
                titulo = title,
                sinopsis = synopsis,
                estrellas = selectedRating,
                imagen = imageUrl.hashCode(),
                urlImagen = imageUrl,
                type = contentType,
                categoria = category,
                isbn = isbn.ifEmpty { null },
                tagIds = ArrayList(tagIds)
            )

            // Confirmar actualización
            AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
                .setTitle("Confirm changes")
                .setMessage("Are you sure you want to apply these changes?")
                .setPositiveButton("Yes") { _, _ ->
                    contentViewModel.actualizarContenidos(updatedContent)
                    showCustomToast("Content updated successfully!")

                    Handler(Looper.getMainLooper()).postDelayed({
                        val intent = Intent(this@EditContent, Home::class.java)
                        startActivity(intent)
                        finish()
                    }, 1000)
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun showCancelConfirmation() {
        if (hasUnsavedChanges()) {
            AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
                .setTitle("Unsaved changes")
                .setMessage("You have unsaved changes. Are you sure you want to exit?")
                .setPositiveButton("Exit without saving") { _, _ ->
                    val intent = Intent(this, Home::class.java)
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("Continue editing") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        } else {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun hasUnsavedChanges(): Boolean {
        val currentTitle = etTitle.text.toString().trim()
        val currentSynopsis = etSynopsis.text.toString().trim()
        val currentCategory = etCategory.text.toString().trim()
        val currentISBN = etISBN.text.toString().trim()

        return currentContent?.let { content ->
            currentTitle != content.titulo ||
                    currentSynopsis != content.sinopsis ||
                    currentCategory != content.categoria ||
                    (chipBook.isChecked && currentISBN != (content.isbn ?: "")) ||
                    selectedRating != content.estrellas ||
                    hasTagChanges(content)
        } ?: false
    }

    private fun hasTagChanges(content: Content): Boolean {
        // Comparar tags actuales con los originales
        val originalTagNames = tagViewModel.listaTags.value
            ?.filter { content.tagIds.contains(it.id) }
            ?.map { it.nombre }
            ?.toSet() ?: emptySet()

        return selectedTags.toSet() != originalTagNames
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
            .setTitle("Select category")
            .setItems(categoryOptions) { _, which ->
                etCategory.setText(categoryOptions[which])
            }
            .create()

        dialog.show()
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

    override fun onBackPressed() {
        super.onBackPressed()
        showCancelConfirmation()
    }
}