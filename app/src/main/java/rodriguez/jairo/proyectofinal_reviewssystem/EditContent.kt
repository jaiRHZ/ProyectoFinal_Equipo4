package rodriguez.jairo.proyectofinal_reviewssystem

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class EditContent : AppCompatActivity() {

    private lateinit var switchOption1: SwitchCompat
    private lateinit var switchOption2: SwitchCompat
    private lateinit var switchOption3: SwitchCompat
    private lateinit var textViewISBN: TextView
    private lateinit var etISBN: EditText
    private lateinit var etCategory: EditText
    private lateinit var chipGroupTags: ChipGroup
    private val selectedTags = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_content)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        switchOption1 = findViewById(R.id.switchOption1)
        switchOption2 = findViewById(R.id.switchOption2)
        switchOption3 = findViewById(R.id.switchOption3)
        textViewISBN = findViewById(R.id.textViewISBN)
        etISBN = findViewById(R.id.etISBN)

        setupExclusiveSwitches()

        etCategory = findViewById(R.id.etCategory)
        setupCategorySelector()

        chipGroupTags = findViewById(R.id.chipGroupTags)
        setupTagsListener()


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

    private fun setupExclusiveSwitches() {
        // Switch 1
        switchOption1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Desactivar los otros switches
                switchOption2.isChecked = false
                switchOption3.isChecked = false

                // Ocultar campos adicionales
                hideAdditionalFields()

            }
        }

        // Switch 2
        switchOption2.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Desactivar los otros switches
                switchOption1.isChecked = false
                switchOption3.isChecked = false

                // Ocultar campos adicionales
                hideAdditionalFields()

            }
        }

        // Switch 3
        switchOption3.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Desactivar los otros switches
                switchOption1.isChecked = false
                switchOption2.isChecked = false

                // Mostrar campos adicionales
                showAdditionalFields()

            } else {
                // Si se desactiva la opción 3, ocultar campos adicionales
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

        // Limpiar el texto del EditText cuando se oculta
        etISBN.setText("")
    }


    private fun setupCategorySelector() {
        etCategory.setOnClickListener {
            showCategorySelector()
        }
    }

    private fun showCategorySelector() {
        val categoryOptions = arrayOf(
            "Acción",
            "Aventura",
            "Comedia",
            "Drama",
            "Terror",
            "Thriller",
            "Romance",
            "Ciencia Ficción",
            "Fantasía",
            "Animación",
            "Documental",
            "Musical",
            "Biografía",
            "Historia",
            "Misterio",
            "Crimen",
            "Familia",
            "Guerra",
            "Deportes",
            "Suspenso",
            "Western",
            "Noir"
        )

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Seleccionar categoría")
        builder.setItems(categoryOptions) { _, which ->
            etCategory.setText(categoryOptions[which])
        }
        builder.show()
    }
}