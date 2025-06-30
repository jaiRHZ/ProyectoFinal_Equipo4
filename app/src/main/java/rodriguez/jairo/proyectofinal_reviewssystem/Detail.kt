package rodriguez.jairo.proyectofinal_reviewssystem

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Detail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val back: ImageView = findViewById(R.id.backHome_detail)
        back.setOnClickListener {
            val intent: Intent = Intent(this, Home::class.java)
            startActivity(intent)
        }

        val buttonReview: Button = findViewById(R.id.btnAddReview)
        buttonReview.setOnClickListener {
            val intent: Intent = Intent(this, AddReview::class.java)
            startActivity(intent)
        }
    }
}