package me.otavio.todolist.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import me.otavio.todolist.R
import me.otavio.todolist.databinding.ItemTaskBinding
import me.otavio.todolist.model.Task

class TaskListAdapter: ListAdapter<Task, TaskListAdapter.TaskViewHolder>(DiffCallback()) {

    var editItemListener: (Task) -> Unit = {}
    var deleteItemListener: (Task) -> Unit = {}

    inner class TaskViewHolder(
        private val binding: ItemTaskBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: Task) {
            binding.tvTitle.text = item.title
            binding.tvHourDate.text = "${item.date} ${item.hour}"
            binding.btnItemOptions.setOnClickListener { showOptionsPopup(item) }
        }

        private fun showOptionsPopup(item: Task) {
            val btnItemOptions = binding.btnItemOptions
            val popupMenu = PopupMenu(btnItemOptions.context, btnItemOptions)
            popupMenu.menuInflater.inflate(R.menu.item_popup_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.action_edit -> editItemListener(item)
                    R.id.action_delete -> deleteItemListener(item)
                }
                return@setOnMenuItemClickListener true
            }
            popupMenu.show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTaskBinding.inflate(inflater, parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class DiffCallback: DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task) = (oldItem == newItem)
    override fun areContentsTheSame(oldItem: Task, newItem: Task) = (
            oldItem.title == newItem.title
                    && oldItem.date == newItem.date
                    && oldItem.hour == newItem.hour
            )

}