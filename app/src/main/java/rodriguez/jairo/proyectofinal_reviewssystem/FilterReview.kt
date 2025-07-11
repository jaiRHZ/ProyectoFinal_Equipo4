package rodriguez.jairo.proyectofinal_reviewssystem

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import rodriguez.jairo.proyectofinal_reviewssystem.viewmodels.TagViewModel


class FilterReview : AppCompatActivity() {

    private lateinit var chipGroupTags: ChipGroup
    private lateinit var switchMyReviews: SwitchCompat
    private lateinit var switchExploreReviews: SwitchCompat
    private lateinit var backButton: ImageView
    private lateinit var applyButton: Button

    private lateinit var tagViewModel: TagViewModel

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
        tagViewModel = ViewModelProvider(this)[TagViewModel::class.java]
        setupTagsFromFirebase()
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
        setupSwitchesListener()
        setupNavigationListeners()
    }

    private fun setupTagsFromFirebase() {
        tagViewModel.listaTags.observe(this) { tags ->
            chipGroupTags.removeAllViews()
            selectedTags.clear()

            tags.forEach { tag ->
                val chip = Chip(this)
                chip.text = tag.nombre
                chip.isCheckable = true
                chip.isClickable = true

                chip.setChipBackgroundColorResource(R.color.chip_background_selector)
                chip.setTextColor(ContextCompat.getColorStateList(this, R.color.chip_text_color_selector))
                chip.chipStrokeColor = ContextCompat.getColorStateList(this, R.color.chip_stroke_selector)
                chip.chipStrokeWidth = 2.dpToPx()
                chip.textSize = 14f

                chip.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        selectedTags.add(tag.id)
                    } else {
                        selectedTags.remove(tag.id)
                    }
                    // Aquí no necesitas validar filtros porque se aplican al dar click en aplicar
                }

                chipGroupTags.addView(chip)
            }

            // Marcar chips guardados
            val savedTagIds = getSharedPreferences("FilterPrefs", Context.MODE_PRIVATE)
                .getStringSet("selected_tags", emptySet()) ?: emptySet()

            for (i in 0 until chipGroupTags.childCount) {
                val chip = chipGroupTags.getChildAt(i) as Chip
                val tag = tags[i]
                chip.isChecked = savedTagIds.contains(tag.id)
            }
        }
    }

    private fun Int.dpToPx(): Float {
        return this * resources.displayMetrics.density
    }

    private fun setupSwitchesListener() {
        switchMyReviews.setOnCheckedChangeListener { _, isChecked ->
            isMyReviewsEnabled = isChecked
        }

        switchExploreReviews.setOnCheckedChangeListener { _, isChecked ->
            isExploreReviewsEnabled = isChecked
        }
    }

    private fun setupNavigationListeners() {
        backButton.setOnClickListener {
            showConfirmationDialog(
                title = "Discard changes?",
                message = "Filters will not be applied if you go back now",
                onConfirm = { navigateToHome(applyFilters = false) },
                onCancel = { }
            )
        }

        applyButton.setOnClickListener {
            applyFilters()
            showCustomToast("Filters applied successfully")
            navigateToHome(applyFilters = true)
        }
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
    }

    private fun loadSavedFilters() {
        val sharedPrefs = getSharedPreferences("FilterPrefs", Context.MODE_PRIVATE)

        val savedTags = sharedPrefs.getStringSet("selected_tags", emptySet()) ?: emptySet()
        selectedTags.clear()
        selectedTags.addAll(savedTags)

        isMyReviewsEnabled = sharedPrefs.getBoolean("my_reviews_enabled", false)
        isExploreReviewsEnabled = sharedPrefs.getBoolean("explore_reviews_enabled", false)

        switchMyReviews.isChecked = isMyReviewsEnabled
        switchExploreReviews.isChecked = isExploreReviewsEnabled
    }

    private fun navigateToHome(applyFilters: Boolean) {
        val intent = Intent(this, Home::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

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
            .setPositiveButton("Yes") { _, _ -> onConfirm() }
            .setNegativeButton("No") { _, _ -> onCancel() }
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

    override fun onBackPressed() {
        showConfirmationDialog(
            title = "Discard changes?",
            message = "Filters will not be applied if you exit now",
            onConfirm = {
                super.onBackPressed()
                navigateToHome(applyFilters = false)
            },
            onCancel = { }
        )
    }
}