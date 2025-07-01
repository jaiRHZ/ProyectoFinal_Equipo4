package rodriguez.jairo.proyectofinal_reviewssystem

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
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
        backButton.setOnClickListener {
            showConfirmationDialog(
                title = "Discard changes?", // "¿Descartar cambios?"
                message = "Filters will not be applied if you go back now", // "Los filtros no se aplicarán si regresas ahora"
                onConfirm = { navigateToHome(applyFilters = false) },
                onCancel = { /* no-op */ }
            )
        }

        applyButton.setOnClickListener {
            if (validateAndApplyFilters()) {
                navigateToHome(applyFilters = true)
            }
        }
    }

    private fun validateFilters(): Boolean {
        return true // siempre válidos
    }

    private fun validateAndApplyFilters(): Boolean {
        if (selectedTags.isEmpty() && !isMyReviewsEnabled && !isExploreReviewsEnabled) {
            showCustomToast("All reviews will be shown") // "Se mostrarán todas las revisiones"
        } else if (selectedTags.isNotEmpty() && !isMyReviewsEnabled && !isExploreReviewsEnabled) {
            showCustomToast("Tag filters applied to all reviews") // "Filtros de tags aplicados a todas las revisiones"
        } else if (selectedTags.isEmpty() && (isMyReviewsEnabled || isExploreReviewsEnabled)) {
            val reviewType = when {
                isMyReviewsEnabled && isExploreReviewsEnabled -> "my reviews and community reviews" // "mis revisiones y revisiones de la comunidad"
                isMyReviewsEnabled -> "my reviews" // "mis revisiones"
                else -> "community reviews" // "revisiones de la comunidad"
            }
            showCustomToast("All $reviewType will be shown") // "Se mostrarán todas las $reviewType"
        }

        applyFilters()
        return true
    }

    private fun applyFilters() {
        val sharedPrefs = getSharedPreferences("FilterPrefs", Context.MODE_PRIVATE)
        with(sharedPrefs.edit()) {
            putStringSet("selected_tags", selectedTags.toSet())
            putBoolean("my_reviews_enabled", isMyReviewsEnabled)
            putBoolean("explore_reviews_enabled", isExploreReviewsEnabled)
            putBoolean("filters_applied", true)
            apply()
        }

        Log.d("FiltersApplied", "Tags: $selectedTags, MyReviews: $isMyReviewsEnabled, ExploreReviews: $isExploreReviewsEnabled")
        showCustomToast("Filters applied successfully") // "Filtros aplicados correctamente"
    }

    private fun loadSavedFilters() {
        val sharedPrefs = getSharedPreferences("FilterPrefs", Context.MODE_PRIVATE)

        val savedTags = sharedPrefs.getStringSet("selected_tags", emptySet()) ?: emptySet()
        selectedTags.clear()
        selectedTags.addAll(savedTags)

        for (i in 0 until chipGroupTags.childCount) {
            val chip = chipGroupTags.getChildAt(i) as Chip
            chip.isChecked = savedTags.contains(chip.text.toString())
        }

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
        AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Yes") { _, _ -> onConfirm() } // "Sí"
            .setNegativeButton("No") { _, _ -> onCancel() } // "No"
            .setCancelable(false)
            .show()
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

    private fun clearAllFilters() {
        selectedTags.clear()
        for (i in 0 until chipGroupTags.childCount) {
            val chip = chipGroupTags.getChildAt(i) as Chip
            chip.isChecked = false
        }

        switchMyReviews.isChecked = false
        switchExploreReviews.isChecked = false
        isMyReviewsEnabled = false
        isExploreReviewsEnabled = false

        showCustomToast("Filters cleared") // "Filtros limpiados"
    }

    override fun onBackPressed() {
        showConfirmationDialog(
            title = "Discard changes?", // "¿Descartar cambios?"
            message = "Filters will not be applied if you exit now", // "Los filtros no se aplicarán si sales ahora"
            onConfirm = {
                super.onBackPressed()
                navigateToHome(applyFilters = false)
            },
            onCancel = { /* No hacer nada */ }
        )
    }

    fun addClearFiltersOption() {
        // opcional
    }
}