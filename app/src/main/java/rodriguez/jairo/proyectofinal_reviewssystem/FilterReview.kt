package rodriguez.jairo.proyectofinal_reviewssystem

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class FilterReview : AppCompatActivity() {

    private lateinit var chipGroupTags: ChipGroup
    private val selectedTags = mutableListOf<String>()
    private lateinit var switchMyReviews: SwitchCompat
    private lateinit var switchExploreReviews: SwitchCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_filter_review)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        chipGroupTags = findViewById(R.id.chipGroupTags)
        switchMyReviews = findViewById(R.id.switchMyReviews)
        switchExploreReviews = findViewById(R.id.switchExploreReviews)

        setupTagsListener()
        setupSwitchesListener()

    }

    private fun setupTagsListener() {

        for (i in 0 until chipGroupTags.childCount) {
            val chip = chipGroupTags.getChildAt(i) as Chip
            chip.setOnCheckedChangeListener { _, isChecked ->
                val tagText = chip.text.toString()
                if (isChecked) {
                    selectedTags.add(tagText)
                } else {
                    selectedTags.remove(tagText)
                }

                Log.d("SelectedTags", "Tags seleccionados: $selectedTags")

            }
        }
    }

    private fun setupSwitchesListener() {
        switchMyReviews.setOnCheckedChangeListener { _, isChecked ->
            Log.d("FilterSwitch", "My Reviews: $isChecked")

        }

        switchExploreReviews.setOnCheckedChangeListener { _, isChecked ->
            Log.d("FilterSwitch", "Explore Reviews: $isChecked")
        }
    }

}