package rodriguez.jairo.proyectofinal_reviewssystem

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.PatternsCompat

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginButton: Button = findViewById(R.id.login_button)
        val signupButton: Button = findViewById(R.id.login_button_signup)
        val forgotButton: Button = findViewById(R.id.login_button_forgot)
        val emailEditText: EditText = findViewById(R.id.login_email)
        val passEditText: EditText = findViewById(R.id.login_password)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passEditText.text.toString().trim()

            when {
                email.isEmpty() -> {
                    emailEditText.error = "Por favor ingresa tu email"
                    return@setOnClickListener
                }
                !isEmailValid(email) -> {
                    emailEditText.error = "Ingresa un email valido"
                    return@setOnClickListener
                }
                password.isEmpty() -> {
                    passEditText.error = "Por favor ingresa tu contraseña"
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
                else -> {
                    // Ambos campos son válidos
                    val intent = Intent(this, Home::class.java)
                    startActivity(intent)
                }
            }
        }

        signupButton.setOnClickListener {
            startActivity(Intent(this, SignUp::class.java))
        }

        forgotButton.setOnClickListener {
            startActivity(Intent(this, ConfirmChange::class.java))
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isPasswordValid(password: String): Boolean {
        if (password.length < 8) return false

        val hasLetter = password.any { it.isLetter() }
        val hasDigit = password.any { it.isDigit() }
        if (!hasLetter || !hasDigit) return false

        if (password.any { it.isWhitespace() }) return false

        return true
    }
}