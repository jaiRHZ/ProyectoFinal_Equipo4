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
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import rodriguez.jairo.proyectofinal_reviewssystem.databinding.ActivityAddContentBinding
import rodriguez.jairo.proyectofinal_reviewssystem.databinding.ActivityHomeBinding
import rodriguez.jairo.proyectofinal_reviewssystem.entities.Content
import java.util.Locale


class Home : AppCompatActivity() {
    //adapter
    //private lateinit var adapter: PeliculaAdapter
    private var peliculas = ArrayList<Film>()
    private lateinit var searchView: SearchView
    private lateinit var originalPeliculas: ArrayList<Film>
    private lateinit var filteredPeliculas: ArrayList<Film>
    private lateinit var gridPelis: GridView

    private lateinit var adapter: ContentAdapter
    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewModel: ContentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[ContentViewModel::class.java]

        viewModel.listaContenidos.observe(this) { contenidos ->
            setupRecyclerView(contenidos)
        }

        initViews()
        setupData()
        setupSearchView()
        setupClickListeners()
    }

    fun setupRecyclerView(listaContenidos: List<Content>){
        adapter = ContentAdapter(listaContenidos, ::borrarContenido, ::actualizarContenido)
        binding.moviesCatalog.adapter = adapter
    }

    fun borrarContenido(id: String){
        viewModel.borrarContenidos(id)
    }

    fun actualizarContenido(contenido: Content){


    }

    private fun initViews() {
        gridPelis = findViewById(R.id.movies_catalog)
        searchView = findViewById(R.id.searchView)

    }

    private fun setupData() {
        //cargarPeliculas()
        originalPeliculas = ArrayList(peliculas)
        filteredPeliculas = ArrayList(peliculas)

        // Configurar adapter
        //adapter = PeliculaAdapter(this, filteredPeliculas)

        gridPelis.adapter = adapter
    }



    private fun setupSearchView() {
        // Configurar colores del SearchView
        val searchText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchText?.apply {
            setTextColor(ContextCompat.getColor(this@Home, R.color.black))
            setHintTextColor(ContextCompat.getColor(this@Home, R.color.subtituloGris))
        }


        // Configurar listener para búsqueda
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Opcional: cerrar el teclado al enviar
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterMovies(newText.orEmpty())
                return true
            }
        })

        // Configurar para que no se iconifique automáticamente
        searchView.isIconifiedByDefault = false

        // Opcional: configurar el ícono de búsqueda
        searchView.setIconifiedByDefault(false)
    }

    private fun setupClickListeners() {
        val home: ImageView = findViewById(R.id.profile_home)
        val filterHome: ImageView = findViewById(R.id.filter_home)
        val btnAddContent: Button = findViewById(R.id.btnAddContent_home)

        home.setOnClickListener {
            startActivity(Intent(this, Profile::class.java))
        }

        filterHome.setOnClickListener {
            startActivity(Intent(this, FilterReview::class.java))
        }

        btnAddContent.setOnClickListener {
            startActivity(Intent(this, AddContent::class.java))
        }
    }

    private fun filterMovies(query: String) {
        filteredPeliculas.clear()

        if (query.isEmpty()) {
            // Si no hay query, mostrar todas las películas
            filteredPeliculas.addAll(originalPeliculas)
        } else {
            val searchQuery = query.lowercase(Locale.getDefault())

            //Filtro por título de la pelicula
            originalPeliculas.forEach { pelicula ->
                val titleMatch = pelicula.title.lowercase(Locale.getDefault()).contains(searchQuery)

                if (titleMatch) {
                    filteredPeliculas.add(pelicula)
                }
            }
        }

        // Notificar cambios al adapter
        adapter.notifyDataSetChanged()

    }



    // Función para limpiar la búsqueda (opcional)
    fun clearSearch() {
        searchView.setQuery("", false)
        searchView.clearFocus()
        filterMovies("")
    }

    //Sustituir por adapter del recycler
    inner class ContentAdapter(){
        var listaContenidos: List<Content>,
        var onBorrarClic: (String) -> Unit,
        var onActualizarClick: (Content) -> Unit
        ): RecyclerView.Adapter<ContentAdapter.ViewHolder>() {
            class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
                val cvContenido: CardView = itemView.findViewById(R.id.cvContenido)
                val tvTitulo: TextView = itemView.findViewById(R.id.tvTitulo)
                val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
                val ibtnBorrar: ImageButton = itemView.findViewById(R.id.ibtnButton)
            }
    }


//    fun cargarPeliculas(){
//        peliculas.add(Film("The Brutalist", R.drawable.stars,R.drawable.thebrutalist, "GOD2", "SI", "movies"))
//        peliculas.add(Film(
//            "Anora",
//            R.drawable.stars,
//            R.drawable.anora,
//            "When a visionary architect and his wife flee post-war Europe in 1947 to rebuild their legacy and witness the birth of modern United States, their lives are changed forever by a mysterious, wealthy client.",
//            "Welcome to America.",
//            "movies"
//        ))
//        peliculas.add(Film(
//            "Conclave",
//            R.drawable.stars,
//            R.drawable.conclave,
//            "After the unexpected death of the Pope, Cardinal Lawrence is tasked with managing the covert and ancient ritual of electing a new one. ",
//            "What happens behind these walls will change everything.",
//            "movies"
//        ))
//        peliculas.add(Film(
//            "Breaking Bad",
//            R.drawable.stars,
//            R.drawable.breaking,
//            "In the wake of his dramatic escape from captivity, Jesse Pinkman must come to terms with his past in order to forge some kind of future. ",
//            "Average chemistry class",
//            "series"
//        ))
//        peliculas.add(Film(
//            "Malcolm",
//            R.drawable.stars,
//            R.drawable.malcolm,
//            "He does his best to build and live his life in the best way possible. From time to time, he has to help each of his family members whenever they get into trouble. It always leads to unexpected adventures.",
//            "A young male has trouble living with his strange and wild family",
//            "series"
//        ))
//        peliculas.add(Film(
//            "Invincible",
//            R.drawable.stars,
//            R.drawable.invincible,
//            "From the comics to the screen, Invincible follows Mark Grayson's journey of becoming Earth's next great defender after his father, Nolan Grayson: also known as Omni-Man.",
//            "The son of Earth's most powerful superhero",
//            "series"
//        ))
//    }
//
//    class PeliculaAdapter: BaseAdapter {
//        var contexto: Context? = null
//        var peliculas = ArrayList<Film>()
//
//        constructor(contexto: Context, peliculas: ArrayList<Film>) {
//            this.contexto = contexto
//            this.peliculas = peliculas
//        }
//
//        override fun getCount(): Int {
//            return peliculas.size
//        }
//
//        override fun getItem(position: Int): Any {
//            return peliculas[position]
//        }
//
//        override fun getItemId(position: Int): Long {
//            return position.toLong()
//        }
//
//        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
//            val pelicula = peliculas[position]
//            val inflator = contexto!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//            val vista = convertView ?: inflator.inflate(R.layout.cell_movie, parent, false)
//
//            val image: ImageView = vista.findViewById(R.id.image_movie_cell)
//            val title: TextView = vista.findViewById(R.id.movie_title_cell)
//            val stars: ImageView = vista.findViewById(R.id.stars_home)
//
//            image.setImageResource(pelicula.image)
//            title.text = pelicula.title
//            stars.setImageResource(pelicula.stars)
//
//            image.setOnClickListener {
//                val intento = Intent(contexto, Detail::class.java).apply {
//                    putExtra("Title", pelicula.title)
//                    putExtra("Image", pelicula.image)
//                    putExtra("Rate", pelicula.stars)
//                    putExtra("description", pelicula.description)
//                    putExtra("review", pelicula.review)
//                }
//                contexto!!.startActivity(intento)
//            }
//
//            return vista
//        }
//    }
}