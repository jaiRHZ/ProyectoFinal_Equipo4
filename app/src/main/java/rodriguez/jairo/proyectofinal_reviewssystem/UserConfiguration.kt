package rodriguez.jairo.proyectofinal_reviewssystem

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import java.util.Calendar

import com.google.firebase.auth.auth
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import rodriguez.jairo.proyectofinal_reviewssystem.viewmodels.UserViewModel

class UserConfiguration : AppCompatActivity() {
    private lateinit var userViewModel: UserViewModel

    private lateinit var usernameSettings: EditText
    private lateinit var birthdayPicker: EditText
    private lateinit var genderSelector: EditText
    private lateinit var back: ImageView
    private lateinit var btnSave_setting: Button
    private lateinit var btnChangePass_setting: Button
    private lateinit var btnLogout_setting: Button
    private lateinit var btnProfileImageSelector: ImageView

    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_configuration)

        initComponents()
        setupUserViewModel()
    }

    private fun initComponents() {

        usernameSettings = findViewById(R.id.input_settings_name)
        birthdayPicker = findViewById(R.id.birthdatePicker_settings)
        genderSelector = findViewById(R.id.genderSelector_settings)
        back = findViewById(R.id.backHome)
        btnSave_setting = findViewById(R.id.btnSave_Settings)
        btnChangePass_setting = findViewById(R.id.btnChangePassword_Settings)
        btnLogout_setting = findViewById(R.id.btnLogout_settings)
        btnProfileImageSelector = findViewById(R.id.imageSelector_settings)

        back.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
        }

        btnProfileImageSelector.setOnClickListener {
            setupImagePicker()
        }

        birthdayPicker.setOnClickListener {
            showDatePicker()
        }

        genderSelector.setOnClickListener {
            showGenderSelector()
        }

        btnSave_setting.setOnClickListener {
            if (validateFields()) {
                // Todos los campos son válidos, proceder con el guardado
                saveUserData()
            } else {
                // Hay errores en los campos
                Toast.makeText(this, "Please Complete all the spaces", Toast.LENGTH_SHORT).show()
            }
        }

        btnChangePass_setting.setOnClickListener {
            val intent = Intent(this, ChangePassword::class.java)
            startActivity(intent)
        }

        btnLogout_setting.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(this, Login::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun setupUserViewModel() {
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            userViewModel.cargarUsuario(uid)
        }

        userViewModel.usuario.observe(this) { user ->
            user?.let {
                usernameSettings.setText(it.name)
                //Falta imagen de perfil
                birthdayPicker.setText(it.birthdate)
                genderSelector.setText(it.gender)
            }
        }

    }

    // Método para validar los campos
    private fun validateFields(): Boolean {
        var isValid = true

        // Validar campo de nombre de usuario
        val username = usernameSettings.text.toString().trim()
        if (username.isEmpty()) {
            usernameSettings.error = "Username is necesary"
            isValid = false
        } else {
            usernameSettings.error = null // Limpiar error si está válido
        }

        // Validar fecha de nacimiento
        val birthday = birthdayPicker.text.toString().trim()
        if (birthday.isEmpty()) {
            birthdayPicker.error = "Birthdate necesary"
            isValid = false
        } else {
            birthdayPicker.error = null // Limpiar error si está válido
        }

        // Validar selector de género
        val gender = genderSelector.text.toString().trim()
        if (gender.isEmpty()) {
            genderSelector.error = "Gender is necesary"
            isValid = false
        } else {
            genderSelector.error = null // Limpiar error si está válido
        }

        return isValid
    }

    // Método para guardar datos del usuario
    // Método para guardar datos del usuario
    private fun saveUserData() {
        val username = usernameSettings.text.toString().trim()
        val birthday = birthdayPicker.text.toString().trim()
        val gender = genderSelector.text.toString().trim()

        // Obtener el UID del usuario actual
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid != null) {
            // Crear mapa con los campos a actualizar
            val camposActualizados = mapOf(
                "name" to username,
                "birthdate" to birthday,
                "gender" to gender
            )

            // Actualizar los campos del usuario usando el ViewModel
            userViewModel.actualizarCamposUsuario(uid, camposActualizados)

            Toast.makeText(this, "Saved Successfully", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Error: User not authenticated", Toast.LENGTH_SHORT).show()
        }
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
                birthdayPicker.setText(selectedDate)
                // Limpiar error cuando se selecciona una fecha
                birthdayPicker.error = null
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    private fun showGenderSelector() {
        val genderOptions = arrayOf("Male", "Female", "Other", "Do Not Specify")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Gender")
        builder.setItems(genderOptions) { _, which ->
            genderSelector.setText(genderOptions[which])
            // Limpiar error cuando se selecciona un género
            genderSelector.error = null
        }
        builder.show()
    }

    //----------------------------------------------------------------------------------------------
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            btnProfileImageSelector.setImageURI(it)
        }
    }

    private fun setupImagePicker() {
        btnProfileImageSelector.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }
    }
    //----------------------------------------------------------------------------------------------
}