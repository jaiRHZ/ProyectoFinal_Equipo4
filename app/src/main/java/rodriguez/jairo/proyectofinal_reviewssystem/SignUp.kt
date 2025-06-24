package rodriguez.jairo.proyectofinal_reviewssystem

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Calendar

class SignUp : AppCompatActivity() {

    private lateinit var etBirthdate: EditText
    private lateinit var etGender: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etBirthdate = findViewById(R.id.etBirthdate)
        etGender = findViewById(R.id.etGender)


        etBirthdate.setOnClickListener {
            showDatePicker()
        }

        etGender.setOnClickListener {
            showGenderSelector()
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
                etBirthdate.setText(selectedDate)
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
            etGender.setText(genderOptions[which])
        }
        builder.show()
    }

}