package rodriguez.jairo.proyectofinal_reviewssystem

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class FilterReview : AppCompatActivity() {

    private lateinit var chipGroupTags: ChipGroup
    private lateinit var switchMyReviews: SwitchCompat
    private lateinit var switchExploreReviews: SwitchCompat
    private lateinit var backButton: ImageView
    private lateinit var applyButton: Button

    private val selectedTags = mutableListOf<String>()
    private var isMyReviewsEnabled = false
    private var isExploreReviewsEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_filter_review)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeViews()
        setupListeners()
        loadSavedFilters()
    }

    private fun initializeViews() {
        chipGroupTags = findViewById(R.id.chipGroupTags)
        switchMyReviews = findViewById(R.id.switchMyReviews)
        switchExploreReviews = findViewById(R.id.switchExploreReviews)
        backButton = findViewById(R.id.backHome)
        applyButton = findViewById(R.id.applyFiltersButton)
    }

    private fun setupListeners() {
        setupTagsListener()
        setupSwitchesListener()
        setupNavigationListeners()
    }

    private fun setupTagsListener() {
        for (i in 0 until chipGroupTags.childCount) {
            val chip = chipGroupTags.getChildAt(i) as Chip
            chip.setOnCheckedChangeListener { _, isChecked ->
                val tagText = chip.text.toString()
                if (isChecked) {
                    if (!selectedTags.contains(tagText)) {
                        selectedTags.add(tagText)
                    }
                } else {
                    selectedTags.remove(tagText)
                }
                Log.d("SelectedTags", "Tags seleccionados: $selectedTags")
                validateFilters()
            }
        }
    }

    private fun setupSwitchesListener() {
        switchMyReviews.setOnCheckedChangeListener { _, isChecked ->
            isMyReviewsEnabled = isChecked
            Log.d("FilterSwitch", "My Reviews: $isChecked")
            validateFilters()
        }

        switchExploreReviews.setOnCheckedChangeListener { _, isChecked ->
            isExploreReviewsEnabled = isChecked
            Log.d("FilterSwitch", "Explore Reviews: $isChecked")
            validateFilters()
        }
    }

    private fun setupNavigationListeners() {
        // Botón de regreso - vuelve al home sin aplicar filtros
        backButton.setOnClickListener {
            showConfirmationDialog(
                title = "¿Descartar cambios?",
                message = "Los filtros no se aplicarán si regresas ahora",
                onConfirm = { navigateToHome(applyFilters = false) },
                onCancel = { /* No hacer nada */ }
            )
        }

        // Botón de aplicar filtros
        applyButton.setOnClickListener {
            if (validateAndApplyFilters()) {
                navigateToHome(applyFilters = true)
            }
        }
    }

    private fun validateFilters(): Boolean {
        // Los filtros son válidos incluso si no se selecciona nada
        // Esto permite mostrar todos los resultados
        return true
    }

    private fun validateAndApplyFilters(): Boolean {
        // Los filtros son válidos incluso si no se selecciona nada
        // Esto permite mayor flexibilidad al usuario

        // Si no hay nada seleccionado, mostrar todos los resultados
        if (selectedTags.isEmpty() && !isMyReviewsEnabled && !isExploreReviewsEnabled) {
            showToast("Se mostrarán todas las revisiones")
        } else if (selectedTags.isNotEmpty() && !isMyReviewsEnabled && !isExploreReviewsEnabled) {
            // Si solo hay tags seleccionados, aplicar a todas las revisiones
            showToast("Filtros de tags aplicados a todas las revisiones")
        } else if (selectedTags.isEmpty() && (isMyReviewsEnabled || isExploreReviewsEnabled)) {
            // Si solo hay switches activados sin tags
            val reviewType = when {
                isMyReviewsEnabled && isExploreReviewsEnabled -> "mis revisiones y revisiones de la comunidad"
                isMyReviewsEnabled -> "mis revisiones"
                else -> "revisiones de la comunidad"
            }
            showToast("Se mostrarán todas las $reviewType")
        }

        applyFilters()
        return true
    }

    private fun applyFilters() {
        // Guardar filtros en SharedPreferences o pasarlos como Intent extras
        val sharedPrefs = getSharedPreferences("FilterPrefs", Context.MODE_PRIVATE)
        with(sharedPrefs.edit()) {
            putStringSet("selected_tags", selectedTags.toSet())
            putBoolean("my_reviews_enabled", isMyReviewsEnabled)
            putBoolean("explore_reviews_enabled", isExploreReviewsEnabled)
            putBoolean("filters_applied", true)
            apply()
        }

        Log.d("FiltersApplied", "Tags: $selectedTags, MyReviews: $isMyReviewsEnabled, ExploreReviews: $isExploreReviewsEnabled")
        showToast("Filtros aplicados correctamente")
    }

    private fun loadSavedFilters() {
        val sharedPrefs = getSharedPreferences("FilterPrefs", Context.MODE_PRIVATE)

        // Cargar tags seleccionados
        val savedTags = sharedPrefs.getStringSet("selected_tags", emptySet()) ?: emptySet()
        selectedTags.clear()
        selectedTags.addAll(savedTags)

        // Aplicar estado a los chips
        for (i in 0 until chipGroupTags.childCount) {
            val chip = chipGroupTags.getChildAt(i) as Chip
            chip.isChecked = savedTags.contains(chip.text.toString())
        }

        // Cargar estado de los switches
        isMyReviewsEnabled = sharedPrefs.getBoolean("my_reviews_enabled", false)
        isExploreReviewsEnabled = sharedPrefs.getBoolean("explore_reviews_enabled", false)

        switchMyReviews.isChecked = isMyReviewsEnabled
        switchExploreReviews.isChecked = isExploreReviewsEnabled
    }

    private fun navigateToHome(applyFilters: Boolean) {
        val intent = Intent(this, Home::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)

        if (applyFilters) {
            intent.putExtra("filters_applied", true)
        }

        startActivity(intent)
        finish()
    }

    private fun showConfirmationDialog(
        title: String,
        message: String,
        onConfirm: () -> Unit,
        onCancel: () -> Unit
    ) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Sí") { _, _ -> onConfirm() }
            .setNegativeButton("No") { _, _ -> onCancel() }
            .setCancelable(false)
            .show()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun clearAllFilters() {
        // Limpiar tags
        selectedTags.clear()
        for (i in 0 until chipGroupTags.childCount) {
            val chip = chipGroupTags.getChildAt(i) as Chip
            chip.isChecked = false
        }

        // Limpiar switches
        switchMyReviews.isChecked = false
        switchExploreReviews.isChecked = false
        isMyReviewsEnabled = false
        isExploreReviewsEnabled = false

        showToast("Filtros limpiados")
    }

    override fun onBackPressed() {
        showConfirmationDialog(
            title = "¿Descartar cambios?",
            message = "Los filtros no se aplicarán si sales ahora",
            onConfirm = {
                super.onBackPressed()
                navigateToHome(applyFilters = false)
            },
            onCancel = { /* No hacer nada */ }
        )
    }

    // Metodo opcional para limpiar filtros desde el menú o un botón adicional
    fun addClearFiltersOption() {

    }
}