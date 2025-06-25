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
        loadExistingData()
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
        // Implementar carga real
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
        if (switchOption3.isChecked && !validateISBN()) isValid = false
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
                etTitle.error = "El título es requerido"
                etTitle.requestFocus(); false
            }
            title.length < 2 -> {
                etTitle.error = "Debe tener al menos 2 caracteres"
                etTitle.requestFocus(); false
            }
            title.length > 100 -> {
                etTitle.error = "No puede exceder 100 caracteres"
                etTitle.requestFocus(); false
            }
            else -> true
        }
    }

    private fun validateContentType(): Boolean {
        return if (!(switchOption1.isChecked || switchOption2.isChecked || switchOption3.isChecked)) {
            showToast("Selecciona un tipo de contenido")
            false
        } else true
    }

    private fun validateISBN(): Boolean {
        val isbn = etISBN.text.toString().trim()
        return when {
            isbn.isEmpty() -> {
                etISBN.error = "El ISBN es requerido"
                etISBN.requestFocus(); false
            }
            !isValidISBN(isbn) -> {
                etISBN.error = "ISBN inválido (ISBN-10 o ISBN-13)"
                etISBN.requestFocus(); false
            }
            else -> true
        }
    }

    private fun validateCategory(): Boolean {
        val category = etCategory.text.toString().trim()
        return if (category.isEmpty()) {
            showToast("Selecciona una categoría")
            etCategory.requestFocus(); false
        } else true
    }

    private fun validateSynopsis(): Boolean {
        val synopsis = etSynopsis.text.toString().trim()
        return when {
            synopsis.isEmpty() -> {
                etSynopsis.error = "La sinopsis es requerida"
                etSynopsis.requestFocus(); false
            }
            synopsis.length < 10 -> {
                etSynopsis.error = "Mínimo 10 caracteres"
                etSynopsis.requestFocus(); false
            }
            synopsis.length > 500 -> {
                etSynopsis.error = "Máximo 500 caracteres"
                etSynopsis.requestFocus(); false
            }
            else -> true
        }
    }

    private fun validateTags(): Boolean {
        return when {
            selectedTags.isEmpty() -> {
                showToast("Selecciona al menos un tag")
                false
            }
            selectedTags.size > 5 -> {
                showToast("Máximo 5 tags permitidos")
                false
            }
            else -> true
        }
    }

    private fun validateImage(): Boolean {
        return if (selectedImageUri == null) {
            showToast("Recomendamos mantener o actualizar la imagen de portada")
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
            switchOption1.isChecked -> "Película"
            switchOption2.isChecked -> "Serie"
            switchOption3.isChecked -> "Libro"
            else -> ""
        }
        val isbn = if (switchOption3.isChecked) etISBN.text.toString().trim() else ""
        val category = etCategory.text.toString().trim()
        val synopsis = etSynopsis.text.toString().trim()

        AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
            .setTitle("Confirmar cambios")
            .setMessage("¿Está seguro de aplicar los cambios?")
            .setPositiveButton("Sí") { _, _ ->
                Log.d("EditContent", """
                    Título: $title
                    Tipo: $contentType
                    ISBN: $isbn
                    Categoría: $category
                    Sinopsis: $synopsis
                    Tags: $selectedTags
                    Imagen: ${selectedImageUri ?: "No modificada"}
                """.trimIndent())
                showToast("¡Cambios aplicados exitosamente!")

                Handler(Looper.getMainLooper()).postDelayed({
                    startActivity(Intent(this, Home::class.java))
                    finish()
                }, 1000)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showCancelConfirmation() {
        if (hasUnsavedChanges()) {
            AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
                .setTitle("Cambios sin guardar")
                .setMessage("Tiene cambios sin guardar. ¿Salir?")
                .setPositiveButton("Salir sin guardar") { _, _ ->
                    startActivity(Intent(this, Home::class.java))
                    finish()
                }
                .setNegativeButton("Seguir editando", null)
                .setNeutralButton("Guardar y salir") { _, _ ->
                    if (validateForm()) saveChangesAndExit()
                    else showToast("Corrige los errores antes de guardar")
                }
                .show()
        } else {
            startActivity(Intent(this, Home::class.java))
            finish()
        }
    }

    private fun saveChangesAndExit() {
        showToast("¡Cambios guardados exitosamente!")
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, Home::class.java))
            finish()
        }, 1000)
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
        chipGroupTags.setOnCheckedStateChangeListener { group, checkedIds ->
            selectedTags.clear()
            if (checkedIds.size > 5) {
                val lastId = checkedIds.last()
                group.check(lastId) // Se puede optimizar
                showToast("Máximo 5 tags")
                return@setOnCheckedStateChangeListener
            }
            checkedIds.forEach { id ->
                val chip = group.findViewById<Chip>(id)
                selectedTags.add(chip.text.toString())
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
        etCategory.setOnClickListener { showCategorySelector() }
    }

    private fun showCategorySelector() {
        val options = arrayOf("Acción", "Aventura", "Comedia", "Drama", "Terror", "Thriller",
            "Romance", "Ciencia Ficción", "Fantasía", "Animación", "Documental", "Musical",
            "Biografía", "Historia", "Misterio", "Crimen", "Familia", "Guerra", "Deportes",
            "Suspenso", "Western", "Noir")

        val dialog = AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
            .setTitle("Seleccionar categoría")
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