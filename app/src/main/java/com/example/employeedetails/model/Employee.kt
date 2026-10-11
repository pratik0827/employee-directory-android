package com.example.employeedetails.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.text.NumberFormat
import java.util.Locale

/**
 * Represents an individual employee record.
 * Implements [Parcelable] via @Parcelize for high-performance Intent passing between Activities.
 */
@Parcelize
data class Employee(
    @SerializedName("name")
    val name: String = "",

    @SerializedName("age")
    val age: Int = 0,

    @SerializedName("salary")
    val salary: Int = 0
) : Parcelable {

    /**
     * Returns formatted monthly salary, e.g., "₹17,000"
     */
    val formattedSalary: String
        get() {
            val formatter = NumberFormat.getNumberInstance(Locale.getDefault())
            return "₹${formatter.format(salary)}"
        }

    /**
     * Returns computed and formatted annual salary package, e.g., "₹204,000 / year"
     */
    val formattedAnnualSalary: String
        get() {
            val annual = salary.toLong() * 12L
            val formatter = NumberFormat.getNumberInstance(Locale.getDefault())
            return "₹${formatter.format(annual)}"
        }

    /**
     * Returns the first character of the employee's name for display in avatar badges.
     */
    val initial: String
        get() = name.trim().take(1).uppercase(Locale.getDefault()).ifEmpty { "?" }
}

/**
 * Wrapper data class for the root object schema: {"employees": [...]}
 */
data class EmployeeListResponse(
    @SerializedName("employees")
    val employees: List<Employee>? = null,

    @SerializedName("data")
    val data: List<Employee>? = null
)
