package com.example.employeedetails.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.employeedetails.databinding.ItemEmployeeBinding
import com.example.employeedetails.model.Employee
import kotlin.math.abs

/**
 * RecyclerView Adapter utilizing [ListAdapter] and [DiffUtil] for smooth animations,
 * zero flickering, and optimal UI performance.
 */
class EmployeeAdapter(
    private val onItemClick: (Employee) -> Unit
) : ListAdapter<Employee, EmployeeAdapter.EmployeeViewHolder>(EmployeeDiffCallback()) {

    // Palette for assigning distinct, consistent colors to employee avatars
    private val avatarColorPalette = intArrayOf(
        Color.parseColor("#3B82F6"), // Blue
        Color.parseColor("#8B5CF6"), // Purple
        Color.parseColor("#EC4899"), // Pink
        Color.parseColor("#10B981"), // Emerald
        Color.parseColor("#F59E0B"), // Amber
        Color.parseColor("#06B6D4"), // Cyan
        Color.parseColor("#6366F1")  // Indigo
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val binding = ItemEmployeeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EmployeeViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        val employee = getItem(position)
        val colorIndex = abs(employee.name.hashCode()) % avatarColorPalette.size
        val avatarColor = avatarColorPalette[colorIndex]
        holder.bind(employee, avatarColor)
    }

    class EmployeeViewHolder(
        private val binding: ItemEmployeeBinding,
        private val onItemClick: (Employee) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(employee: Employee, avatarColor: Int) {
            with(binding) {
                tvAvatar.text = employee.initial
                tvAvatar.background.setTint(avatarColor)

                tvEmployeeName.text = employee.name
                tvEmployeeAge.text = "Age: ${employee.age} yrs"
                tvEmployeeSalary.text = "${employee.formattedSalary} / mo"

                root.setOnClickListener {
                    onItemClick(employee)
                }
            }
        }
    }

    private class EmployeeDiffCallback : DiffUtil.ItemCallback<Employee>() {
        override fun areItemsTheSame(oldItem: Employee, newItem: Employee): Boolean {
            // Identifying uniqueness: name + age + salary
            return oldItem.name == newItem.name && oldItem.age == newItem.age
        }

        override fun areContentsTheSame(oldItem: Employee, newItem: Employee): Boolean {
            return oldItem == newItem
        }
    }
}
