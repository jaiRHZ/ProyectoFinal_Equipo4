package rodriguez.jairo.proyectofinal_reviewssystem

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.Firebase
import rodriguez.jairo.proyectofinal_reviewssystem.entities.User
import rodriguez.jairo.proyectofinal_reviewssystem.viewmodels.UserViewModel
import java.util.Calendar

class SignUp : AppCompatActivity() {
    private lateinit var userViewModel: UserViewModel

    private lateinit var auth: FirebaseAuth

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

        auth = Firebase.auth
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

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
                registerUser(etEmail.text.toString(), etPassword.text.toString())
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
                etName.error = "Name is required"
                etName.requestFocus()
                false
            }
            name.length < 2 -> {
                etName.error = "Name must have at least 2 characters"
                etName.requestFocus()
                false
            }
            !name.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) -> {
                etName.error = "Name can only contain letters and spaces"
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
                etEmail.error = "Email is required"
                etEmail.requestFocus()
                false
            }
            !email.matches(emailPattern.toRegex()) -> {
                etEmail.error = "Invalid email format"
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
                showStyledToast("Please select your birthdate")
                false
            }
            !isValidAge(birthdate) -> {
                showStyledToast("You must be at least 13 years old to register")
                false
            }
            else -> true
        }
    }

    private fun validateGender(): Boolean {
        val gender = etGender.text.toString().trim()

        return when {
            gender.isEmpty() -> {
                showStyledToast("Please select your gender")
                false
            }
            else -> true
        }
    }

    private fun validatePassword(): Boolean {
        val password = etPassword.text.toString()

        return when {
            password.isEmpty() -> {
                etPassword.error = "Password is required"
                etPassword.requestFocus()
                false
            }
            password.length < 8 -> {
                etPassword.error = "Password must have at least 8 characters"
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
                etConfirmPassword.error = "Please confirm your password"
                etConfirmPassword.requestFocus()
                false
            }
            password != confirmPassword -> {
                etConfirmPassword.error = "Passwords do not match"
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

    private fun registerUser(email: String, password: String) {
        Log.d("INFO", "email: ${email}, password${password}")
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    val uid = firebaseUser?.uid

                    if (uid != null) {
                        // Crear objeto User con los datos del formulario
                        val nuevoUsuario = User(
                            name = etName.text.toString().trim(),
                            gender = etGender.text.toString().trim(),
                            birthdate = etBirthdate.text.toString().trim(),
                            urlImagen = "" // podrías establecer una imagen por defecto
                        )

                        // Guardar en Firestore
                        userViewModel.guardarUsuario(uid, nuevoUsuario)

                        showStyledToast("Registration successful!")
                        startActivity(Intent(this, Login::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Error retrieving user UID", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
                // Mostrar mensaje de éxito
                //showStyledToast("¡Registro exitoso!")

                // Proceder a la pantalla de login
//        val intent = Intent(this, Login::class.java)
//        startActivity(intent)
//        finish() // Opcional: cerrar la actividad actual
            }
    }

    private fun showStyledToast(message: String) {
        val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        val view = toast.view

        // Personalizar el Toast para que coincida con la paleta de colores
        view?.apply {
            background = ContextCompat.getDrawable(this@SignUp, R.drawable.toast_background)
            val textView = findViewById<TextView>(android.R.id.message)
            textView?.setTextColor(ContextCompat.getColor(this@SignUp, R.color.white))
        }

        toast.show()
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Crear un contexto con tema personalizado para el DatePicker
        val themedContext = ContextThemeWrapper(this, R.style.CustomDatePickerTheme)

        val datePickerDialog = DatePickerDialog(
            themedContext,
            R.style.CustomDatePickerTheme,
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

        // Personalizar los botones y colores del DatePicker
        datePickerDialog.setOnShowListener {
            val positiveButton = datePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE)
            val negativeButton = datePickerDialog.getButton(DialogInterface.BUTTON_NEGATIVE)

            positiveButton?.setTextColor(ContextCompat.getColor(this, R.color.boton))
            negativeButton?.setTextColor(ContextCompat.getColor(this, R.color.subtituloGris))

            // Personalizar el fondo del diálogo
            datePickerDialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)

            // Forzar colores de texto en el DatePicker
            try {
                val datePicker = datePickerDialog.datePicker
                setDatePickerTextColors(datePicker)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        datePickerDialog.show()
    }

    private fun setDatePickerTextColors(datePicker: DatePicker) {
        try {
            // Cambiar colores de texto recursivamente
            setViewTextColors(datePicker)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setViewTextColors(view: View) {
        try {
            when (view) {
                is TextView -> {
                    view.setTextColor(ContextCompat.getColor(this, R.color.white))
                }
                is EditText -> {
                    view.setTextColor(ContextCompat.getColor(this, R.color.white))
                }
                is ViewGroup -> {
                    for (i in 0 until view.childCount) {
                        setViewTextColors(view.getChildAt(i))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showGenderSelector() {
        val genderOptions = arrayOf("Male", "Female", "Other", "Prefer not to say")

        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
        builder.setTitle("Select Gender")

        // Crear un adaptador personalizado para las opciones
        val adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, genderOptions) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)
                textView.setTextColor(ContextCompat.getColor(this@SignUp, R.color.white))
                textView.setBackgroundColor(ContextCompat.getColor(this@SignUp, android.R.color.transparent))
                textView.setPadding(32, 32, 32, 32)
                return view
            }
        }

        builder.setAdapter(adapter) { dialog, which ->
            etGender.setText(genderOptions[which])
            dialog.dismiss()
        }

        val alertDialog = builder.create()

        // Personalizar después de mostrar el diálogo
        alertDialog.setOnShowListener {
            // Personalizar el título
            val titleId = resources.getIdentifier("alertTitle", "id", "android")
            if (titleId > 0) {
                val titleView = alertDialog.findViewById<TextView>(titleId)
                titleView?.setTextColor(ContextCompat.getColor(this, R.color.white))
                titleView?.textSize = 18f
            }

            // Personalizar el fondo del diálogo
            alertDialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)

            // Personalizar la lista
            val listView = alertDialog.listView
            listView?.divider = ContextCompat.getDrawable(this, R.drawable.divider_line)
            listView?.dividerHeight = 1
        }

        alertDialog.show()
    }
}