package rodriguez.jairo.proyectofinal_reviewssystem

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.PatternsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth
        val errorTv: TextView = findViewById(R.id.tvError)
        errorTv.visibility = View.INVISIBLE

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
//                !isEmailValid(email) -> {
//                    emailEditText.error = "Ingresa un email valido"
//                    return@setOnClickListener
//                }
                password.isEmpty() -> {
                    passEditText.error = "Por favor ingresa tu contraseña"
                    passEditText.requestFocus()
                    return@setOnClickListener
                }
//                !isPasswordValid(password) -> {
//                    passEditText.error = "Contraseña incorrecta"
//                    passEditText.requestFocus()
//                    return@setOnClickListener
//                }
                else -> {
                    // Ambos campos son válidos
                    login(email, password)
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

    private fun goToHome(user: FirebaseUser) {
        val intent = Intent(this, Home::class.java)
        intent.putExtra("user", user.email)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    fun showError(text:String = "", visible: Boolean) {
        val errorTv: TextView = findViewById(R.id.tvError)

        errorTv.text = text

        errorTv.visibility = if (visible) View.VISIBLE else View.INVISIBLE

    }

    public override fun onStart() {
        super.onStart()
        //Validar si el usuario está registrado y actualizar UI acorde a ello
        val currentUser = auth.currentUser
        if (currentUser != null) {
            goToHome(currentUser)
        }
    }

    fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    showError(visible = false)
                    goToHome(user!!)
                } else {
                    showError("Usuario y/o contraseña incorrectos", true)
                }
            }
    }

//    private fun isEmailValid(email: String): Boolean {
//        return PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()
//    }
//
//    private fun isPasswordValid(password: String): Boolean {
//        if (password.length < 8) return false
//
//        val hasLetter = password.any { it.isLetter() }
//        val hasDigit = password.any { it.isDigit() }
//        if (!hasLetter || !hasDigit) return false
//
//        if (password.any { it.isWhitespace() }) return false
//
//        return true
//    }
}