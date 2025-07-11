package rodriguez.jairo.proyectofinal_reviewssystem

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
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
    private lateinit var baseFilteredContenido: ArrayList<Content> // contenido filtrado por tags/reviews

    // Chips
    private lateinit var chipGroupContentType: ChipGroup
    private lateinit var chipMovie: Chip
    private lateinit var chipSerie: Chip
    private lateinit var chipBook: Chip

    private lateinit var searchView: SearchView
    private val selectedContentTypes = mutableListOf<String>()

    // Flags para sincronizar datos
    private var isContentLoaded = false
    private var isUserLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initViews()
        setupRecyclerView()
        setupViewModel()
        setupUserViewModel()
        setupSearchView()
        loadSavedChipFilters()
        setupChipsListener()
        setupClickListeners()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)  // actualizar intent actual
        applyFiltersIfNeeded()
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
        baseFilteredContenido = ArrayList()
        contentAdapter = ContentAdapter(filteredContenido) { content ->

            val intent = Intent(this, Detail::class.java)
            intent.putExtra("contentId", content.id)
            intent.putExtra("title", content.titulo)
            intent.putExtra("imageUrl", content.urlImagen)
            intent.putExtra("rate", content.estrellas)
            intent.putExtra("category", content.categoria)
            intent.putExtra("type", content.type)
            intent.putExtra("synopsis", content.sinopsis)
            // Puedes agregar más extras si Detail los necesita
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
            isUserLoaded = true
            applyFiltersIfNeeded()
        }
        userViewModel.cargarUsuario(uid)
    }

    private fun setupViewModel() {
        contentViewModel = ViewModelProvider(this)[ContentViewModel::class.java]
        contentViewModel.listaContenidos.observe(this) { contenidos ->
            listaContenido.clear()
            listaContenido.addAll(contenidos)
            originalContenido = ArrayList(listaContenido)
            isContentLoaded = true
            applyFiltersIfNeeded()
        }
    }

    private fun applyFiltersIfNeeded() {
        if (!isContentLoaded || !isUserLoaded) return

        val (tags, myReviews, exploreReviews) = loadReviewFilters()
        val hasActiveFilters = tags.isNotEmpty() || myReviews || exploreReviews

        baseFilteredContenido = if (hasActiveFilters) {
            applyReviewFilters(tags, myReviews, exploreReviews, originalContenido, currentUserReviewIds)
        } else {
            ArrayList(originalContenido)
        }

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
                // Filtrar por tags (si hay tags seleccionados)
                val matchesTags = tags.isEmpty() || content.tagIds.any { tags.contains(it) }

                // Revisar si el contenido tiene reviews del usuario actual o de otros
                val hasMyReview = content.reviewIds.any { myReviewIds.contains(it) }
                val hasOtherReview = content.reviewIds.any { !myReviewIds.contains(it) }

                val matchesReviewSource = when {
                    myReviews && exploreReviews -> true  // mostrar todos con reviews
                    myReviews -> hasMyReview
                    exploreReviews -> hasOtherReview
                    else -> true  // sin filtro de review
                }

                matchesTags && matchesReviewSource
            }
        )
    }

    private fun setupSearchView() {
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
        // Desactivar temporalmente listeners para evitar llamados dobles
        chipMovie.setOnCheckedChangeListener(null)
        chipSerie.setOnCheckedChangeListener(null)
        chipBook.setOnCheckedChangeListener(null)

        // Cargar estados guardados en selectedContentTypes
        chipMovie.isChecked = selectedContentTypes.contains("movies")
        chipSerie.isChecked = selectedContentTypes.contains("series")
        chipBook.isChecked = selectedContentTypes.contains("books")

        // Configurar listeners
        chipMovie.setOnCheckedChangeListener { _, isChecked ->
            val type = "movies"
            if (isChecked) selectedContentTypes.add(type) else selectedContentTypes.remove(type)
            if (::originalContenido.isInitialized) applyContentTypeFilters()
        }

        chipSerie.setOnCheckedChangeListener { _, isChecked ->
            val type = "series"
            if (isChecked) selectedContentTypes.add(type) else selectedContentTypes.remove(type)
            if (::originalContenido.isInitialized) applyContentTypeFilters()
        }

        chipBook.setOnCheckedChangeListener { _, isChecked ->
            val type = "books"
            if (isChecked) selectedContentTypes.add(type) else selectedContentTypes.remove(type)
            if (::originalContenido.isInitialized) applyContentTypeFilters()
        }
    }

    private fun applyContentTypeFilters() {
        saveChipFilters()
        filteredContenido.clear()

        // Aplicar filtro por tipo de contenido sobre baseFilteredContenido
        val filteredByType = if (selectedContentTypes.isEmpty()) {
            baseFilteredContenido
        } else {
            baseFilteredContenido.filter { selectedContentTypes.contains(it.type) }
        }

        // Aplicar filtro por búsqueda
        val query = searchView.query.toString().lowercase()
        val filteredByQuery = if (query.isNotEmpty()) {
            filteredByType.filter { it.titulo.lowercase().contains(query) }
        } else {
            filteredByType
        }

        filteredContenido.addAll(filteredByQuery)
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
        selectedContentTypes.clear()
        sharedPrefs.getStringSet("selected_content_types", emptySet())?.let {
            selectedContentTypes.addAll(it)
        }

        chipMovie.isChecked = selectedContentTypes.contains("movies")
        chipSerie.isChecked = selectedContentTypes.contains("series")
        chipBook.isChecked = selectedContentTypes.contains("books")
    }

    private fun setupClickListeners() {
        val profile: ImageView = findViewById(R.id.profile_home)
        val filter: ImageView = findViewById(R.id.filter_home)
        val btnAdd: Button = findViewById(R.id.btnAddContent_home)

        profile.setOnClickListener {
            startActivity(Intent(this, Profile::class.java))  // ← Temporalmente comentado
            //Toast.makeText(this, "Perfil deshabilitado temporalmente", Toast.LENGTH_SHORT).show()
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
