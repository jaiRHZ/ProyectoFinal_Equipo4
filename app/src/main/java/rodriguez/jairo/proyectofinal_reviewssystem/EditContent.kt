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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.switchmaterial.SwitchMaterial

class EditContent : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var switchOption1: SwitchMaterial
    private lateinit var switchOption2: SwitchMaterial
    private lateinit var switchOption3: SwitchMaterial
    private lateinit var textViewISBN: TextView
    private lateinit var etISBN: EditText
    private lateinit var etCategory: EditText
    private lateinit var etSynopsis: EditText
    private lateinit var chipGroupTags: ChipGroup
    private lateinit var ivCoverImage: ImageView
    private lateinit var btnApplyChanges: Button
    private lateinit var btnCancel: Button

    private val selectedTags = mutableListOf<String>()
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
        setContentView(R.layout.activity_edit_content)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeViews()
        setupClickListeners()
        loadExistingData() // Cargar datos existentes para edición
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
        chipGroupTags = findViewById(R.id.chipGroupTags)
        ivCoverImage = findViewById(R.id.ivCoverImage)
        btnApplyChanges = findViewById(R.id.btnApplyChanges)
        btnCancel = findViewById(R.id.btnCancel)
    }

    private fun loadExistingData() {
        // Aquí se cargarian los datos de existentes para edición

    }

    private fun setupClickListeners() {
        setupExclusiveSwitches()
        setupCategorySelector()
        setupTagsListener()
        setupImagePicker()
        setupButtonListeners()
    }

    private fun setupButtonListeners() {
        btnApplyChanges.setOnClickListener {
            if (validateForm()) {
                applyChanges()
                val intent = Intent(this, Home::class.java)
                startActivity(intent)
                finish()
            }
        }

        btnCancel.setOnClickListener {
            showCancelConfirmation()

            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            finish()
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

        // Validar tags
        if (!validateTags()) {
            isValid = false
        }

        // Validar imagen (opcional para EditContent con aviso amigable)
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
            showToast("Selecciona un tipo de contenido (Película, Serie o Libro)")
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
                showToast("Selecciona una categoría")
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
                showToast("Selecciona al menos un tag")
                false
            }
            selectedTags.size > 5 -> {
                showToast("No puedes seleccionar más de 5 tags")
                false
            }
            else -> true
        }
    }


    private fun validateImage(): Boolean {
        return if (selectedImageUri == null) {
            showToast("Recomendamos mantener o actualizar la imagen de portada")
            true // No bloquea el guardado en edición, solo avisa
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

        // Mostrar confirmación antes de guardar cambios
        AlertDialog.Builder(this)
            .setTitle("Confirmar cambios")
            .setMessage("¿Está seguro de que desea aplicar estos cambios?")
            .setPositiveButton("Sí") { _, _ ->
                // Lógica para guardar los cambios
                Log.d("EditContent", """
                    Cambios aplicados:
                    Título: $title
                    Tipo: $contentType
                    ISBN: $isbn
                    Categoría: $category
                    Sinopsis: $synopsis
                    Tags: $selectedTags
                    Imagen: ${selectedImageUri?.toString() ?: "No modificada"}
                """.trimIndent())

                showToast("¡Cambios aplicados exitosamente!")

                // Simular guardado y cerrar actividad después de un breve delay
                Handler(Looper.getMainLooper()).postDelayed({
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
            AlertDialog.Builder(this)
                .setTitle("Cambios sin guardar")
                .setMessage("Tiene cambios sin guardar. ¿Está seguro de que desea salir?")
                .setPositiveButton("Salir sin guardar") { _, _ ->
                    finish()
                }
                .setNegativeButton("Continuar editando") { dialog, _ ->
                    dialog.dismiss()
                }
                .setNeutralButton("Guardar y salir") { _, _ ->
                    if (validateForm()) {
                        applyChanges()
                    }
                }
                .show()
        } else {
            finish()
        }
    }

    private fun hasUnsavedChanges(): Boolean {

        return etTitle.text.toString().trim().isNotEmpty() ||
                etSynopsis.text.toString().trim().isNotEmpty() ||
                etCategory.text.toString().trim().isNotEmpty() ||
                etISBN.text.toString().trim().isNotEmpty() ||
                selectedTags.isNotEmpty() ||
                switchOption1.isChecked ||
                switchOption2.isChecked ||
                switchOption3.isChecked ||
                selectedImageUri != null
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    private fun setupTagsListener() {
        for (i in 0 until chipGroupTags.childCount) {
            val chip = chipGroupTags.getChildAt(i) as Chip
            chip.setOnCheckedChangeListener { _, isChecked ->
                val tagText = chip.text.toString()

                if (isChecked) {
                    if (selectedTags.size >= 5) {
                        chip.isChecked = false
                        showToast("Máximo 5 tags permitidos")
                        return@setOnCheckedChangeListener
                    }
                    selectedTags.add(tagText)
                } else {
                    selectedTags.remove(tagText)
                }

                Log.d("SelectedTags", "Tags seleccionados: $selectedTags")
            }
        }
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

    private fun showCategorySelector() {
        val categoryOptions = arrayOf(
            "Acción", "Aventura", "Comedia", "Drama", "Terror", "Thriller",
            "Romance", "Ciencia Ficción", "Fantasía", "Animación", "Documental",
            "Musical", "Biografía", "Historia", "Misterio", "Crimen", "Familia",
            "Guerra", "Deportes", "Suspenso", "Western", "Noir"
        )

        AlertDialog.Builder(this)
            .setTitle("Seleccionar categoría")
            .setItems(categoryOptions) { _, which ->
                etCategory.setText(categoryOptions[which])
            }
            .show()
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