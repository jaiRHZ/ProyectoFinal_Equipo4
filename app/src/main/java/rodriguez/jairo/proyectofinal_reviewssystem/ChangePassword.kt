package rodriguez.jairo.proyectofinal_reviewssystem

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ChangePassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        var back: ImageView = findViewById(R.id.backHome)
        val passEditText: EditText = findViewById(R.id.change_new_password)
        val passConfirmEditText: EditText = findViewById(R.id.change_confirm_password)

        back.setOnClickListener{
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        var confirm: Button = findViewById(R.id.confirm_apply)
        confirm.setOnClickListener {

            val password = passEditText.text.toString().trim()
            val passwordConfirm = passConfirmEditText.text.toString().trim()

            when {
                password.isEmpty() -> {
                    passEditText.error = "Por favor ingresa una contraseña"
                    passEditText.requestFocus()
                    return@setOnClickListener
                }
                !isPasswordValid(password) -> {
                    passEditText.error = """
                La contraseña debe contener:
                - Mínimo 8 caracteres
                - Letras y números
                - Sin espacios
                """.trimIndent()
                    passEditText.requestFocus()
                    return@setOnClickListener
                }
                !password.equals(passwordConfirm) -> {
                    passConfirmEditText.error = "Las contraseñas no coinciden"
                    passConfirmEditText.requestFocus()
                    return@setOnClickListener
                }
                else -> {
                    // Ambos campos son válidos
                    val intent = Intent(this, Login::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    fun isPasswordValid(password: String) : Boolean {
        if (password.length < 8) return false

        val hasLetter = password.any { it.isLetter() }
        val hasDigit = password.any { it.isDigit() }
        if (!hasLetter || !hasDigit) return false

        if (password.any { it.isWhitespace() }) return false

        return true
    }
}