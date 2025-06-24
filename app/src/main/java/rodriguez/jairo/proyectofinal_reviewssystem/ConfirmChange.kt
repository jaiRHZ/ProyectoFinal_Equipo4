package rodriguez.jairo.proyectofinal_reviewssystem

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ConfirmChange : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_change)
        var back: ImageView = findViewById(R.id.backHome)
        back.setOnClickListener{
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        var confirm: Button = findViewById(R.id.confirm_button)
        confirm.setOnClickListener {
            val intent = Intent(this, ChangePassword::class.java)
            startActivity(intent)
        }
    }
}