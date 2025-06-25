package rodriguez.jairo.proyectofinal_reviewssystem

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
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
    private lateinit var back: ImageView
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
        back = findViewById(R.id.backHome)
        btnSave_setting = findViewById(R.id.btnSave_Settings)
        btnChangePass_setting =findViewById(R.id.btnChangePassword_Settings)
        btnLogout_setting = findViewById(R.id.btnLogout_settings)


        back.setOnClickListener{
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
        }

        birthdayPicker.setOnClickListener{
            showDatePicker()
        }

        genderSelector.setOnClickListener{
            showGenderSelector()
        }

        btnSave_setting.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }
        btnChangePass_setting.setOnClickListener {
            val intent = Intent(this, ChangePassword::class.java)
            startActivity(intent)
        }

        btnLogout_setting.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
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
        }
        builder.show()
    }

}