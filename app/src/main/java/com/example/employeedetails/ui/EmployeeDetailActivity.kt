package com.example.employeedetails.ui

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.employeedetails.R
import com.example.employeedetails.databinding.ActivityEmployeeDetailBinding
import com.example.employeedetails.model.Employee
import kotlin.math.abs

/**
 * Detail Activity displaying comprehensive information for the selected employee.
 * Safely deserializes the [Employee] parcelable with backward compatibility across Android API versions.
 */
class EmployeeDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmployeeDetailBinding

    private val avatarColorPalette = intArrayOf(
        Color.parseColor("#3B82F6"),
        Color.parseColor("#8B5CF6"),
        Color.parseColor("#EC4899"),
        Color.parseColor("#10B981"),
        Color.parseColor("#F59E0B"),
        Color.parseColor("#06B6D4"),
        Color.parseColor("#6366F1")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployeeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        populateEmployeeDetails()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            // Respects Android back navigation stack
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun populateEmployeeDetails() {
        val employee: Employee? = extractEmployeeFromIntent()

        if (employee == null) {
            Toast.makeText(this, R.string.error_no_employee_data, Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        with(binding) {
            // Dynamic avatar tinting consistent with the list screen
            val colorIndex = abs(employee.name.hashCode()) % avatarColorPalette.size
            tvDetailAvatar.background.setTint(avatarColorPalette[colorIndex])
            tvDetailAvatar.text = employee.initial

            // Header summary
            tvDetailNameHeader.text = employee.name

            // Detailed structured attributes
            tvDetailFullName.text = employee.name
            tvDetailAge.text = getString(R.string.label_years, employee.age)
            tvDetailSalary.text = getString(R.string.label_salary_formatted, employee.salary)
            tvDetailAnnualSalary.text = getString(R.string.label_annual_salary_formatted, employee.salary * 12)
        }
    }

    /**
     * Backward-compatible Parcelable retrieval supporting Android 13+ (API 33, Tiramisu)
     * type-safe API as well as legacy Android API versions.
     */
    private fun extractEmployeeFromIntent(): Employee? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_EMPLOYEE, Employee::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_EMPLOYEE)
        }
    }

    companion object {
        const val EXTRA_EMPLOYEE = "com.example.employeedetails.EXTRA_EMPLOYEE"
    }
}
