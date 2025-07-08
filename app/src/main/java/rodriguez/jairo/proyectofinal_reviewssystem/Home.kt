package rodriguez.jairo.proyectofinal_reviewssystem

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import rodriguez.jairo.proyectofinal_reviewssystem.adapters.ContentAdapter
import rodriguez.jairo.proyectofinal_reviewssystem.entities.Content
import rodriguez.jairo.proyectofinal_reviewssystem.viewmodels.ContentViewModel
import rodriguez.jairo.proyectofinal_reviewssystem.viewmodels.UserViewModel


class Home : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var contentAdapter: ContentAdapter
    private lateinit var contentViewModel: ContentViewModel

    private lateinit var userViewModel: UserViewModel
    private var currentUserReviewIds = emptySet<String>()

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
        setupUserViewModel()  // cargar usuario y reviews
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

    private fun setupUserViewModel() {
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        userViewModel.usuario.observe(this) { user ->
            currentUserReviewIds = user?.myReviewIds?.toSet() ?: emptySet()
            // Cuando ya tenemos reviews y contenidos, aplicar filtros si toca
            applyFiltersIfNeeded()
        }
        userViewModel.cargarUsuario(uid)
    }

    private fun applyFiltersIfNeeded() {
        val filtersApplied = intent.getBooleanExtra("filters_applied", false)
        if (!filtersApplied) {
            filteredContenido = ArrayList(originalContenido)
            applyContentTypeFilters()
            return
        }

        val (tags, myReviews, exploreReviews) = loadReviewFilters()
        filteredContenido = applyReviewFilters(tags, myReviews, exploreReviews, originalContenido, currentUserReviewIds)
        applyContentTypeFilters()
    }

    private fun loadReviewFilters(): Triple<Set<String>, Boolean, Boolean> {
        val prefs = getSharedPreferences("FilterPrefs", Context.MODE_PRIVATE)
        val tags = prefs.getStringSet("selected_tags", emptySet()) ?: emptySet()
        val myReviews = prefs.getBoolean("my_reviews_enabled", false)
        val exploreReviews = prefs.getBoolean("explore_reviews_enabled", false)
        return Triple(tags, myReviews, exploreReviews)
    }

    private fun applyReviewFilters(
        tags: Set<String>,
        myReviews: Boolean,
        exploreReviews: Boolean,
        contents: List<Content>,
        myReviewIds: Set<String>
    ): ArrayList<Content> {
        return ArrayList(
            contents.filter { content ->
                val matchesTags = tags.isEmpty() || content.tagIds.any { tags.contains(it) }

                val hasMyReview = content.reviewIds.any { myReviewIds.contains(it) }
                val hasOtherReview = content.reviewIds.any { !myReviewIds.contains(it) }

                val matchesReviewSource = when {
                    myReviews && exploreReviews -> true
                    myReviews -> hasMyReview
                    exploreReviews -> hasOtherReview
                    else -> true
                }

                matchesTags && matchesReviewSource
            }
        )
    }

    private fun setupViewModel() {
        contentViewModel = ViewModelProvider(this)[ContentViewModel::class.java]
        contentViewModel.listaContenidos.observe(this) { contenidos ->
            listaContenido.clear()
            listaContenido.addAll(contenidos)
            originalContenido = ArrayList(listaContenido)

            // Leer filtros de reviews
            val (tags, myReviews, exploreReviews) = loadReviewFilters()

            // Aplica los filtros si se solicitaron desde FilterReview
            val filtersApplied = intent.getBooleanExtra("filters_applied", false)

            filteredContenido = if (filtersApplied) {
                applyReviewFilters(tags, myReviews, exploreReviews, listaContenido, currentUserReviewIds)
            } else {
                ArrayList(listaContenido)
            }

            applyContentTypeFilters()
        }
    }

    private fun setupSearchView() {
//        val searchText = searchView.findViewById<EditText>(androidx.appcompat.R.id.searchView)
//        searchText?.apply {
//            setTextColor(ContextCompat.getColor(this@Home, R.color.black))
//            setHintTextColor(ContextCompat.getColor(this@Home, R.color.subtituloGris))
//        }

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