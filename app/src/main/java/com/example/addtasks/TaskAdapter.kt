package com.example.addtasks

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(private var tasks: MutableList<MutableList<Any>>) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>(), SwipeToDeleteCallback.OnSwipeListener {

    // Список для хранения порядковых номеров элементов
    private var itemNumbers: MutableList<Int> = mutableListOf()
    // Объект для обработки свайпа элемента
    private lateinit var swipeToDeleteCallback: SwipeToDeleteCallback

    // Внутренний класс ViewHolder для отображения элементов списка
    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val numberTextView: TextView = itemView.findViewById(R.id.numberTextView)
        private val textTextView: TextView = itemView.findViewById(R.id.textTextView)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.imageButtonDeleteView)

        // Цвета для текста
        private val grayColor = ContextCompat.getColor(itemView.context, R.color.text_done_gray)
        private val attrs = intArrayOf(R.attr.textColorPrimary)
        private val typedArray = itemView.context.obtainStyledAttributes(attrs)
        private val textColorPrimary = typedArray.getColor(0, Color.BLACK)

        // Метод для привязки данных к ViewHolder
        fun bind(task: MutableList<Any>, itemNumber: Int) {
            // Устанавливаем текстовые поля
            setUpTextFields(task, itemNumber)
            // Устанавливаем обработчик для кнопки удаления
            setUpDeleteButton(task)
            // Устанавливаем обработчик для долгого нажатия
            setLongClickListener()
        }

        // Метод для установки текстовых полей
        private fun setUpTextFields(task: MutableList<Any>, itemNumber: Int) {
            val taskText = task[1].toString() // Текст задачи
            val isDone = task[2] as Int == 1 // Флаг выполнения задачи

            numberTextView.text = itemNumber.toString() // Установка порядкового номера
            textTextView.text = taskText // Установка текста задачи

            // Устанавливаем стили в зависимости от выполнения задачи
            if (isDone) {
                setDoneStyle()
            } else {
                setUndoneStyle()
            }
        }

        // Метод для установки стилей выполненной задачи
        private fun setDoneStyle() {
            numberTextView.paintFlags = textTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            textTextView.paintFlags = textTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            numberTextView.setTextColor(grayColor)
            textTextView.setTextColor(grayColor)
            deleteButton.visibility = View.VISIBLE
        }

        // Метод для установки стилей невыполненной задачи
        private fun setUndoneStyle() {
            numberTextView.paintFlags = textTextView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            textTextView.paintFlags = textTextView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            numberTextView.setTextColor(textColorPrimary)
            textTextView.setTextColor(textColorPrimary)
            deleteButton.visibility = View.GONE
        }

        // Метод для установки обработчика для кнопки удаления
        private fun setUpDeleteButton(task: MutableList<Any>) {
            deleteButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val taskId = task[0] as Int // Получаем идентификатор задачи
                    listener?.onDeleteButtonClick(taskId) // Вызываем метод удаления задачи у слушателя
                }
            }
        }

        // Метод для установки обработчика для долгого нажатия
        private fun setLongClickListener() {
            itemView.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener?.onLongTaskClick(position) // Вызываем метод долгого нажатия у слушателя
                }
                true // Возвращаем true, чтобы указать, что событие обработано
            }
        }
    }

    // Метод вызывается при создании ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        // Создаем новый ViewHolder из макета item_task
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)


    }

    // Метод вызывается для отображения данных в элементе списка
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.bind(task, itemNumbers[position])

        // Обработка клика по элементу
        holder.itemView.setOnClickListener {
            listener?.onTaskClick(position) // Вызываем метод клика у слушателя
        }
    }

    // Метод возвращает количество элементов в списке
    override fun getItemCount(): Int {
        return tasks.size
    }

    // Метод для обновления списка задач
    fun updateTasks(newTasks: MutableList<MutableList<Any>>) {
        tasks.clear()
        tasks.addAll(newTasks)
        updateItemNumbers()
        notifyDataSetChanged() // Обновляем список
    }

    // Метод для обновления порядковых номеров элементов
    private fun updateItemNumbers() {
        itemNumbers.clear()
        for (i in tasks.indices) {
            itemNumbers.add(i + 1)
        }
    }

    // Интерфейс для обработки событий нажатий
    private var listener: OnTaskClickListener? = null

    interface OnTaskClickListener {
        fun onTaskClick(position: Int)
        fun onDeleteButtonClick(taskId: Int)
        fun onLongTaskClick(position: Int)
    }

    // Метод для установки слушателя нажатий
    fun setOnTaskClickListener(listener: OnTaskClickListener) {
        this.listener = listener
    }

    // Метод для настройки обратного вызова для свайпа
    fun setSwipeToDeleteCallback(recyclerView: RecyclerView) {
        swipeToDeleteCallback = SwipeToDeleteCallback(recyclerView.context, this)
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    // Метод, вызываемый при свайпе элемента
    override fun onItemSwiped(position: Int) {
        val task = tasks[position]
        val taskId = task[0] as Int
        listener?.onDeleteButtonClick(taskId)
    }
}