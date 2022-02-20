package me.otavio.todolist.ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import me.otavio.todolist.databinding.ActivityMainBinding
import me.otavio.todolist.datasource.TaskDataSource

// TODO: Ja assisti a parte "Finalizando o app", falta entregar
// TODO: Melhorias, up no GitHub e entregar o projeto
// TODO: Tela de apresentação de tarefa
class MainActivity : AppCompatActivity() {

    companion object {
        private const val CREATE_TASK_CODE = 1000
    }

    private lateinit var binding: ActivityMainBinding
    private val adapter by lazy { TaskListAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvTaskListing.adapter = adapter
        updateTaskList()

        setupListeners()
    }

    private fun setupListeners() {
        binding.fabCreateTask.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            // TODO: Replace deprecated code
            startActivityForResult(intent, CREATE_TASK_CODE)
        }

        adapter.editItemListener = {
            val intent = Intent(this, AddTaskActivity::class.java)
            intent.putExtra(AddTaskActivity.TASK_ID, it.id)
            // TODO: Replace deprecated code
            startActivityForResult(intent, CREATE_TASK_CODE)
        }
        adapter.deleteItemListener = {
            TaskDataSource.deleteTask(it)
            updateTaskList()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // TODO: Replace deprecated code
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == CREATE_TASK_CODE) updateTaskList()
    }

    private fun updateTaskList() {
        val list = TaskDataSource.getList()
        if (list.isEmpty()) {
            binding.includeEmptyState.emptyState.visibility = View.VISIBLE
            binding.rvTaskListing.visibility = View.GONE
        } else {
            binding.includeEmptyState.emptyState.visibility = View.GONE
            binding.rvTaskListing.visibility = View.VISIBLE
        }

        adapter.submitList(list)
    }


}