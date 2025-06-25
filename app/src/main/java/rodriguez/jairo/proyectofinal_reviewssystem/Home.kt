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
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import rodriguez.jairo.proyectofinal_reviewssystem.databinding.ActivityHomeBinding

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

        gridPelis.adapter = adapter

    }

    fun cargarPeliculas(){
        peliculas.add(Film("The Brutalist", R.drawable.stars,R.drawable.thebrutalist, "GOD2", "SI"))
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

            image.setImageResource(pelicula.image)
            title.setText(pelicula.title)


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