package rodriguez.jairo.proyectofinal_reviewssystem

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Profile : AppCompatActivity() {
    var filmsAdapter: FilmAdapter?= null
    var films = ArrayList<Film>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        findViewById<ImageView>(R.id.backHome).setOnClickListener{
            val intent: Intent = Intent(this, Login::class.java)
            this.startActivity(intent)
        }

        cargarPeliculas()
        filmsAdapter = FilmAdapter(this, films)
        var gridPelis: GridView = findViewById(R.id.profile_reviews)

        gridPelis.adapter = filmsAdapter
    }
    fun cargarPeliculas() {
        films.add(Film("The Brutalist", R.drawable.stars, R.drawable.thebrutalist, "I can see why it got 3 Oscars", "The work creates a character whose life is an amalgam of many people's true experiences during that period of time; so it is very..."))
    }

    class FilmAdapter: BaseAdapter {
        var contexto: Context? = null
        var films = ArrayList<Film>()

        constructor(contexto: Context, films: ArrayList<Film>) {
            this.contexto = contexto
            this.films = films
        }

        override fun getCount(): Int = films.size
        override fun getItem(position: Int): Any = films[position]
        override fun getItemId(position: Int): Long = position.toLong()

        override fun getItemViewType(position: Int): Int {
            return if (position % 2 == 0) 0 else 1 // Posiciones pares: tipo 0, impares: tipo 1
        }

        override fun getViewTypeCount(): Int = 2 // Dos tipos de celdas

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val film = films[position]
            val inflater = LayoutInflater.from(contexto)

            return when (getItemViewType(position)) {
                0 -> { // Celda izquierda (película)
                    val view = convertView ?: inflater.inflate(R.layout.cell_content, parent, false)

                    view.findViewById<TextView>(R.id.title_cell_content).text = film.title
                    view.findViewById<ImageView>(R.id.stars_cell_content).setImageResource(film.stars)
                    view.findViewById<ImageView>(R.id.image_cell_content).setImageResource(film.image)

                    view
                }

                else -> { // Celda derecha (reseña)
                    val view = convertView ?: inflater.inflate(R.layout.cell_review, parent, false)

                    view.findViewById<TextView>(R.id.title_cell_review).text = film.review
                    view.findViewById<TextView>(R.id.description_cell_review).text = film.description

                    view
                }
            }
        }
    }
}