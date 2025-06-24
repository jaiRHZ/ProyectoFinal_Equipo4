package rodriguez.jairo.proyectofinal_reviewssystem

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Calendar

class UserConfiguration : AppCompatActivity() {

    private lateinit var usernameSettings: EditText
    private lateinit var birthdayPicker: EditText
    private lateinit var genderSelector: EditText

    private lateinit var btnSave_setting: Button
    private lateinit var btnChangePass_setting: Button
    private lateinit var btnLogout_setting: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_configuration)

        initComponents();
    }

    private fun initComponents() {

        usernameSettings = findViewById(R.id.input_settings_name)
        birthdayPicker = findViewById(R.id.birthdatePicker_settings)
        genderSelector = findViewById(R.id.genderSelector_settings)

        btnSave_setting = findViewById(R.id.btnSave_Settings)
        btnChangePass_setting =findViewById(R.id.btnChangePassword_Settings)
        btnLogout_setting = findViewById(R.id.btnLogout_settings)


        birthdayPicker.setOnClickListener{
            showDatePicker()
        }

        genderSelector.setOnClickListener{
            showGenderSelector()
        }

        btnSave_setting.setOnClickListener {  }
        btnChangePass_setting.setOnClickListener {  }
        btnLogout_setting.setOnClickListener {  }

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
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    private fun showGenderSelector() {
        val genderOptions = arrayOf("Masculino", "Femenino", "Otro", "Prefiero no decir")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Seleccionar género")
        builder.setItems(genderOptions) { _, which ->
            genderSelector.setText(genderOptions[which])
        }
        builder.show()
    }

}