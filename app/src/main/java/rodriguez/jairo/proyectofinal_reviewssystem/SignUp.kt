package rodriguez.jairo.proyectofinal_reviewssystem

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Calendar

class SignUp : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etBirthdate: EditText
    private lateinit var etGender: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeViews()
        setupClickListeners()
    }

    private fun initializeViews() {
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etBirthdate = findViewById(R.id.etBirthdate)
        etGender = findViewById(R.id.etGender)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
    }

    private fun setupClickListeners() {
        val btnRegister: Button = findViewById(R.id.btnRegister)
        val btnLogin: Button = findViewById(R.id.btnLogin)

        etBirthdate.setOnClickListener {
            showDatePicker()
        }

        etGender.setOnClickListener {
            showGenderSelector()
        }

        btnRegister.setOnClickListener {
            if (validateForm()) {
                // Si todas las validaciones pasan, proceder con el registro
                registerUser()
            }
        }

        btnLogin.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true

        // Validar nombre
        if (!validateName()) {
            isValid = false
        }

        // Validar email
        if (!validateEmail()) {
            isValid = false
        }

        // Validar fecha de nacimiento
        if (!validateBirthdate()) {
            isValid = false
        }

        // Validar género
        if (!validateGender()) {
            isValid = false
        }

        // Validar contraseña
        if (!validatePassword()) {
            isValid = false
        }

        // Validar confirmación de contraseña
        if (!validateConfirmPassword()) {
            isValid = false
        }

        return isValid
    }

    private fun validateName(): Boolean {
        val name = etName.text.toString().trim()

        return when {
            name.isEmpty() -> {
                etName.error = "El nombre es requerido"
                etName.requestFocus()
                false
            }
            name.length < 2 -> {
                etName.error = "El nombre debe tener al menos 2 caracteres"
                etName.requestFocus()
                false
            }
            !name.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) -> {
                etName.error = "El nombre solo puede contener letras y espacios"
                etName.requestFocus()
                false
            }
            else -> {
                etName.error = null
                true
            }
        }
    }

    private fun validateEmail(): Boolean {
        val email = etEmail.text.toString().trim()
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

        return when {
            email.isEmpty() -> {
                etEmail.error = "El email es requerido"
                etEmail.requestFocus()
                false
            }
            !email.matches(emailPattern.toRegex()) -> {
                etEmail.error = "Formato de email inválido"
                etEmail.requestFocus()
                false
            }
            else -> {
                etEmail.error = null
                true
            }
        }
    }

    private fun validateBirthdate(): Boolean {
        val birthdate = etBirthdate.text.toString().trim()

        return when {
            birthdate.isEmpty() -> {
                showToast("Selecciona tu fecha de nacimiento")
                false
            }
            !isValidAge(birthdate) -> {
                showToast("Debes ser mayor de 13 años para registrarte")
                false
            }
            else -> true
        }
    }

    private fun validateGender(): Boolean {
        val gender = etGender.text.toString().trim()

        return when {
            gender.isEmpty() -> {
                showToast("Selecciona tu género")
                false
            }
            else -> true
        }
    }

    private fun validatePassword(): Boolean {
        val password = etPassword.text.toString()

        return when {
            password.isEmpty() -> {
                etPassword.error = "La contraseña es requerida"
                etPassword.requestFocus()
                false
            }
            password.length < 6 -> {
                etPassword.error = "La contraseña debe tener al menos 6 caracteres"
                etPassword.requestFocus()
                false
            }
            !password.matches(Regex(".*[A-Z].*")) -> {
                etPassword.error = "La contraseña debe contener al menos una mayúscula"
                etPassword.requestFocus()
                false
            }
            !password.matches(Regex(".*[a-z].*")) -> {
                etPassword.error = "La contraseña debe contener al menos una minúscula"
                etPassword.requestFocus()
                false
            }
            !password.matches(Regex(".*\\d.*")) -> {
                etPassword.error = "La contraseña debe contener al menos un número"
                etPassword.requestFocus()
                false
            }
            else -> {
                etPassword.error = null
                true
            }
        }
    }

    private fun validateConfirmPassword(): Boolean {
        val password = etPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()

        return when {
            confirmPassword.isEmpty() -> {
                etConfirmPassword.error = "Confirma tu contraseña"
                etConfirmPassword.requestFocus()
                false
            }
            password != confirmPassword -> {
                etConfirmPassword.error = "Las contraseñas no coinciden"
                etConfirmPassword.requestFocus()
                false
            }
            else -> {
                etConfirmPassword.error = null
                true
            }
        }
    }

    private fun isValidAge(birthdate: String): Boolean {
        try {
            val parts = birthdate.split("/")
            if (parts.size != 3) return false

            val day = parts[0].toInt()
            val month = parts[1].toInt()
            val year = parts[2].toInt()

            val birthCalendar = Calendar.getInstance()
            birthCalendar.set(year, month - 1, day)

            val currentCalendar = Calendar.getInstance()
            val age = currentCalendar.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)

            // Ajustar si el cumpleaños no ha ocurrido este año
            if (currentCalendar.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
                return age - 1 >= 13
            }

            return age >= 13
        } catch (e: Exception) {
            return false
        }
    }

    private fun registerUser() {
        // Mostrar mensaje de éxito
        showToast("¡Registro exitoso!")

        // Proceder a la pantalla de login
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish() // Opcional: cerrar la actividad actual
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                etBirthdate.setText(selectedDate)
            },
            year,
            month,
            day
        )

        // Establecer fecha máxima para asegurar edad mínima
        val maxDate = Calendar.getInstance()
        maxDate.add(Calendar.YEAR, -13)
        datePickerDialog.datePicker.maxDate = maxDate.timeInMillis

        datePickerDialog.show()
    }

    private fun showGenderSelector() {
        val genderOptions = arrayOf("Masculino", "Femenino", "Otro", "Prefiero no decir")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Seleccionar género")
        builder.setItems(genderOptions) { _, which ->
            etGender.setText(genderOptions[which])
        }
        builder.show()
    }
}