package me.otavio.todolist.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import me.otavio.todolist.R
import me.otavio.todolist.databinding.ActivityAddTaskBinding
import me.otavio.todolist.datasource.TaskDataSource
import me.otavio.todolist.extensions.*
import me.otavio.todolist.model.Task
import java.util.*

class AddTaskActivity : AppCompatActivity() {

    companion object {
        private const val DATE_PICKER_TAG = "DATE PICK 1"
        private const val TIME_PICKER_TAG = "TIME PICK 1"
        const val TASK_ID = "task_id"
    }

    private lateinit var binding: ActivityAddTaskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fillEditData()
        setUpListeners()
    }

    private fun fillEditData() {
        // TODO: Checar por campos nulos
        if (intent.hasExtra(TASK_ID)) {
            val editId = intent.getIntExtra(TASK_ID, 0)
            TaskDataSource.findById(editId)?.let { task ->
                binding.tilTitle.text = task.title
                binding.tilDescription.text = task.description
                binding.tilDate.text = task.date
                binding.tilStartTime.text = task.hour
            }
        }
    }

    private fun setUpListeners() {
        setupDateField()
        setupHourField()
        setupCancelButton()
        setupSaveButton()
    }

    private fun setupSaveButton() {
        binding.btnSave.setOnClickListener {
            if (allInputsAreValid()) {
                val task = Task(
                    title = binding.tilTitle.text,
                    description = binding.tilDescription.text,
                    date = binding.tilDate.text,
                    hour = binding.tilStartTime.text,
                    id = intent.getIntExtra(TASK_ID, 0)
                )
                TaskDataSource.insertTask(task)
                setResult(RESULT_OK)
                finish()
            } else {
                setErrors()
                Toast.makeText(this, getString(R.string.err_incomplete_data), Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Returns false if any input field is empty
     */
    private fun allInputsAreValid(): Boolean {
        return (binding.tilTitle.hasNoText()
                || binding.tilDescription.hasNoText()
                || binding.tilDate.hasNoText()
                || binding.tilStartTime.hasNoText())
    }

    /**
     * Setup an error message for each empty input field
     */
    private fun setErrors() {
        binding.tilTitle.setErrorIfEmpty(this, R.string.err_te_title_missing)
        binding.tilDescription.setErrorIfEmpty(this, R.string.err_te_description_missing)
        binding.tilStartTime.setErrorIfEmpty(this, R.string.err_te_start_time_missing)
        binding.tilDate.setErrorIfEmpty(this, R.string.err_te_date_missing)
    }

    private fun setupCancelButton() = binding.btnCancelEdit.setOnClickListener { finish() }

    private fun setupHourField() {
        binding.tilStartTime.editText?.setOnClickListener {
            val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .build()
            timePicker.addOnPositiveButtonClickListener {
                val minutes: String = if (timePicker.minute < 10)
                    "0${timePicker.minute}"
                else "${timePicker.minute}"

                val hours: String = if (timePicker.hour < 10)
                    "0${timePicker.hour}"
                else "${timePicker.hour}"
                binding.tilStartTime.text = "${hours}:${minutes}"
            }
            timePicker.show(supportFragmentManager, TIME_PICKER_TAG)
        }
    }

    private fun setupDateField() {
        binding.tilDate.editText?.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker().build()
            datePicker.addOnPositiveButtonClickListener {
                val timeZone = TimeZone.getDefault()
                val offset = timeZone.getOffset(Date().time) * -1
                binding.tilDate.text = Date(it + offset).format()
            }
            datePicker.show(supportFragmentManager, DATE_PICKER_TAG)
        }
    }
}