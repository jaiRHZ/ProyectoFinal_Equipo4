package rodriguez.jairo.proyectofinal_reviewssystem

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton


class Home : AppCompatActivity() {
    

    var adapter: PeliculaAdapter? = null
    var seriesAdapter: PeliculaAdapter?= null
    var peliculas = ArrayList<Film>()
    var series = ArrayList<Film>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        cargarPeliculas()
        adapter = PeliculaAdapter(this, peliculas)
        seriesAdapter = PeliculaAdapter(this, series)

        var gridPelis: GridView = findViewById(R.id.movies_catalog)
        val home: ImageView = findViewById(R.id.profile_home)

        val filterHome: ImageView = findViewById(R.id.filter_home)
        val btnAddContent: FloatingActionButton = findViewById(R.id.btnAddContent_home)

        home.setOnClickListener{
            val intent: Intent = Intent(this, Profile::class.java)
            startActivity(intent)
        }

        filterHome.setOnClickListener{
            val intent: Intent = Intent(this, FilterReview::class.java)
            startActivity(intent)
        }

        btnAddContent.setOnClickListener {
            val intent: Intent = Intent(this, AddContent::class.java)
            startActivity(intent)
        }

        gridPelis.adapter = adapter
    }

    fun cargarPeliculas(){
        peliculas.add(Film("The Brutalist", R.drawable.stars,R.drawable.thebrutalist, "GOD2", "SI"))
        peliculas.add(Film(
            "Anora",
            R.drawable.stars,
            R.drawable.anora,
            "When a visionary architect and his wife flee post-war Europe in 1947 to rebuild their legacy and witness the birth of modern United States, their lives are changed forever by a mysterious, wealthy client.",
            "Welcome to America."
        ))
        peliculas.add(Film(
            "Conclave",
            R.drawable.stars,
            R.drawable.conclave,
            "After the unexpected death of the Pope, Cardinal Lawrence is tasked with managing the covert and ancient ritual of electing a new one. ",
            "What happens behind these walls will change everything."
        ))
        peliculas.add(Film(
            "Breaking Bad",
            R.drawable.stars,
            R.drawable.breaking,
            "In the wake of his dramatic escape from captivity, Jesse Pinkman must come to terms with his past in order to forge some kind of future. ",
            "Average chemistry class"
        ))
        peliculas.add(Film(
            "Malcolm",
            R.drawable.stars,
            R.drawable.malcolm,
            "He does his best to build and live his life in the best way possible. From time to time, he has to help each of his family members whenever they get into trouble. It always leads to unexpected adventures.",
            "A young male has trouble living with his strange and wild family"
        ))
        peliculas.add(Film(
            "Invincible",
            R.drawable.stars,
            R.drawable.invincible,
            "From the comics to the screen, Invincible follows Mark Grayson's journey of becoming Earth's next great defender after his father, Nolan Grayson: also known as Omni-Man.",
            "The son of Earth's most powerful superhero"
        ))
    }

    class PeliculaAdapter: BaseAdapter {
        var contexto: Context? = null
        var peliculas = ArrayList<Film>()

        constructor(contexto: Context, peliculas: ArrayList<Film>) {
            this.contexto = contexto
            this.peliculas = peliculas
        }

        override fun getCount(): Int {
            return peliculas.size
        }

        override fun getItem(position: Int): Any {
            return peliculas[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var pelicula = peliculas[position]
            var inflator = contexto!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var vista = inflator.inflate(R.layout.cell_movie, null)
            var image: ImageView = vista.findViewById(R.id.image_movie_cell)
            var title: TextView = vista.findViewById(R.id.movie_title_cell)
            var stars: ImageView = vista.findViewById(R.id.stars_home)

            image.setImageResource(pelicula.image)
            title.setText(pelicula.title)
            stars.setImageResource(pelicula.stars)

            image.setOnClickListener {
                var intento = Intent(contexto, Profile::class.java)
                intento.putExtra("Title", pelicula.title)
                intento.putExtra("Image", pelicula.image)
                intento.putExtra("Rate", pelicula.stars)
                contexto!!.startActivity(intento)
            }

            return vista
        }
    }
}