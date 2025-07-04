package rodriguez.jairo.proyectofinal_reviewssystem

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import rodriguez.jairo.proyectofinal_reviewssystem.entities.Content
import java.util.Locale


class Home : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var contentAdapter: ContentAdapter
    private lateinit var contentViewModel: ContentViewModel

    private var listaContenido = ArrayList<Content>()
    private lateinit var originalContenido: ArrayList<Content>
    private lateinit var filteredContenido: ArrayList<Content>

    // Chips
    private lateinit var chipGroupContentType: ChipGroup
    private lateinit var chipMovie: Chip
    private lateinit var chipSerie: Chip
    private lateinit var chipBook: Chip

    private lateinit var searchView: SearchView
    private val selectedContentTypes = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initViews()
        setupRecyclerView()
        setupViewModel()
        setupSearchView()
        setupChipsListener()
        setupClickListeners()
        loadSavedChipFilters()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.rvContent)
        searchView = findViewById(R.id.searchView)

        chipGroupContentType = findViewById(R.id.chipGroupContentType)
        chipMovie = findViewById(R.id.chipMovie)
        chipSerie = findViewById(R.id.chipSerie)
        chipBook = findViewById(R.id.chipBook)
    }

    private fun setupRecyclerView() {
        filteredContenido = ArrayList()
        contentAdapter = ContentAdapter(filteredContenido) { content ->
            val intent = Intent(this, Detail::class.java).apply {
                putExtra("Title", content.titulo)
                putExtra("ImageUrl", content.urlImagen)   // Cloudinary
                putExtra("ImageLocal", content.imagen)    // fallback
                putExtra("Rate", content.estrellas)
            }
            startActivity(intent)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = contentAdapter
    }

    private fun setupViewModel() {
        contentViewModel = ViewModelProvider(this)[ContentViewModel::class.java]
        contentViewModel.listaContenidos.observe(this) { contenidos ->
            listaContenido.clear()
            listaContenido.addAll(contenidos)
            originalContenido = ArrayList(listaContenido)
            filteredContenido = ArrayList(listaContenido)
            applyContentTypeFilters()
        }
    }

    private fun setupSearchView() {
        val searchText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchText?.apply {
            setTextColor(ContextCompat.getColor(this@Home, R.color.black))
            setHintTextColor(ContextCompat.getColor(this@Home, R.color.subtituloGris))
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                applyContentTypeFilters()
                return true
            }
        })

        searchView.isIconifiedByDefault = false
        searchView.setIconifiedByDefault(false)
    }

    private fun setupChipsListener() {
        chipMovie.setOnCheckedChangeListener { _, isChecked ->
            val type = "movies"
            if (isChecked) selectedContentTypes.add(type) else selectedContentTypes.remove(type)
            applyContentTypeFilters()
        }

        chipSerie.setOnCheckedChangeListener { _, isChecked ->
            val type = "series"
            if (isChecked) selectedContentTypes.add(type) else selectedContentTypes.remove(type)
            applyContentTypeFilters()
        }

        chipBook.setOnCheckedChangeListener { _, isChecked ->
            val type = "books"
            if (isChecked) selectedContentTypes.add(type) else selectedContentTypes.remove(type)
            applyContentTypeFilters()
        }
    }

    private fun applyContentTypeFilters() {
        saveChipFilters()
        filteredContenido.clear()

        val baseList = if (selectedContentTypes.isEmpty()) {
            originalContenido
        } else {
            originalContenido.filter { selectedContentTypes.contains(it.type) }
        }

        val currentQuery = searchView.query.toString().lowercase()
        if (currentQuery.isNotEmpty()) {
            filteredContenido.addAll(baseList.filter {
                it.titulo.lowercase().contains(currentQuery)
            })
        } else {
            filteredContenido.addAll(baseList)
        }

        contentAdapter.actualizarLista(filteredContenido)
    }

    private fun saveChipFilters() {
        val sharedPrefs = getSharedPreferences("ContentTypeFilters", Context.MODE_PRIVATE)
        with(sharedPrefs.edit()) {
            putStringSet("selected_content_types", selectedContentTypes.toSet())
            putBoolean("movie_selected", chipMovie.isChecked)
            putBoolean("serie_selected", chipSerie.isChecked)
            putBoolean("book_selected", chipBook.isChecked)
            apply()
        }
    }

    private fun loadSavedChipFilters() {
        val sharedPrefs = getSharedPreferences("ContentTypeFilters", Context.MODE_PRIVATE)
        val savedContentTypes = sharedPrefs.getStringSet("selected_content_types", emptySet()) ?: emptySet()
        selectedContentTypes.clear()
        selectedContentTypes.addAll(savedContentTypes)

        chipMovie.isChecked = sharedPrefs.getBoolean("movie_selected", false)
        chipSerie.isChecked = sharedPrefs.getBoolean("serie_selected", false)
        chipBook.isChecked = sharedPrefs.getBoolean("book_selected", false)

        if (selectedContentTypes.isNotEmpty()) {
            applyContentTypeFilters()
        }
    }

    private fun setupClickListeners() {
        val profile: ImageView = findViewById(R.id.profile_home)
        val filter: ImageView = findViewById(R.id.filter_home)
        val btnAdd: Button = findViewById(R.id.btnAddContent_home)

        profile.setOnClickListener {
            startActivity(Intent(this, Profile::class.java))
        }

        filter.setOnClickListener {
            startActivity(Intent(this, FilterReview::class.java))
        }

        btnAdd.setOnClickListener {
            startActivity(Intent(this, AddContent::class.java))
        }
    }

    override fun onPause() {
        super.onPause()
        saveChipFilters()
    }

    fun clearAllFilters() {
        selectedContentTypes.clear()
        chipMovie.isChecked = false
        chipSerie.isChecked = false
        chipBook.isChecked = false

        searchView.setQuery("", false)
        searchView.clearFocus()

        filteredContenido.clear()
        filteredContenido.addAll(originalContenido)
        contentAdapter.actualizarLista(filteredContenido)

        val sharedPrefs = getSharedPreferences("ContentTypeFilters", Context.MODE_PRIVATE)
        sharedPrefs.edit().clear().apply()
    }

    fun clearSearch() {
        searchView.setQuery("", false)
        searchView.clearFocus()
        applyContentTypeFilters()
    }
}