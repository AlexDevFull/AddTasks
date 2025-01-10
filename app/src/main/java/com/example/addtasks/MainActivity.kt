package com.example.addtasks

import DBHelper
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import android.content.ClipboardManager
import android.content.ClipData
import androidx.recyclerview.widget.ItemTouchHelper

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper

    private lateinit var tasksRecyclerView: RecyclerView
    private lateinit var etAddTask: EditText
    private lateinit var btnAddTask: Button
    private lateinit var adapter: TaskAdapter
    private lateinit var tvDeleteAll: TextView

    private var isKeyboardOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addtasks)

        // Создаём экземпляр DBHelper для работы с базой данных
        dbHelper = DBHelper(this)

        // Находим Views
        etAddTask = findViewById(R.id.etAddTask)
        btnAddTask = findViewById(R.id.btnAddTask)
        tasksRecyclerView = findViewById(R.id.tasksRecyclerView)
        tvDeleteAll = findViewById(R.id.tvDeleteAll)

        // Устанавливаем LayoutManager для RecyclerView
        tasksRecyclerView.layoutManager = LinearLayoutManager(this)

        // Инициализируем адаптер с пустым списком задач и устанавливаем его для RecyclerView
        adapter = TaskAdapter(mutableListOf())
        tasksRecyclerView.adapter = adapter

        // Обновляем список задач при запуске активности
        updateListTasks()

        // Настройка слушателя для кнопки добавления задачи
        btnAddTask.setOnClickListener {
            val taskText = etAddTask.text.toString().trim()
            if (taskText.isNotEmpty()) {
                // Добавляем задачу в базу данных
                dbHelper.insertDataToTasks(taskText)
                // Очищаем поле ввода после добавления задачи
                etAddTask.text.clear()
                // Обновляем список задач
                updateListTasks()
                // Показываем уведомление об успешном добавлении задачи
                Toast.makeText(this, "Задача успешно добавлена!", Toast.LENGTH_SHORT).show()
                // Сролим ленту в конец
                scrollRecyclerView()
            } else {
                // Показываем сообщение об ошибке, если поле ввода пустое
                Toast.makeText(this, "Пожалуйста, введите задачу в поле.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        // Настройка слушателя для клика на задачу и удаления задачи
        adapter.setOnTaskClickListener(object : TaskAdapter.OnTaskClickListener {

            // Настройка слушателя для короткого нажатия на элемент RecyclerView
            override fun onTaskClick(position: Int) {
                // Получаем задачу по позиции
                val tasksList = dbHelper.getAllTasks()
                val task = tasksList[position] // [2, taska2, 0]
                val get_text_task = task[1].toString()
                val isDone = task[2] == 1

                dbHelper.toggleDoneStatus(get_text_task)

                updateListTasks()
            }

            // Настройка слушателя по svg-иконке удаления элемента RecyclerView
            override fun onDeleteButtonClick(taskId: Int) {
                // Удаление задачи из базы данных по идентификатору
                dbHelper.deleteTaskById(taskId)
                // Обновление списка задач
                updateListTasks()
            }

            // Настройка слушателя для долгого нажатия на элемент RecyclerView
            override fun onLongTaskClick(position: Int) {
                // Выполняйте здесь действия при долгом нажатии на элемент RecyclerView
                val tasksList = dbHelper.getAllTasks()
                val task = tasksList[position]
                // Копирование в буфер обмена
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Task", task[1].toString())
                clipboard.setPrimaryClip(clip)
                // Например, вы можете отобразить контекстное меню, показать диалоговое окно или выполнить другие действия
                Toast.makeText(
                    this@MainActivity,
                    "Скопировано в буфер обмена: ${task[1]}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        // вызывается при свайпе
        adapter.setSwipeToDeleteCallback(tasksRecyclerView)

        etAddTask.setOnClickListener() {
            scrollRecyclerView()
        }

        // Очищаем список
        tvDeleteAll.setOnClickListener{
            dbHelper.deleteAllTasks()
            updateListTasks()
        }

    }


    // Своя функция проверка, которая при открытой клавиатуре прокручивает список задач в конец
    private fun scrollRecyclerView() {
        if (isKeyboardOpen(this)) {
            println("открыта")
            tasksRecyclerView.scrollToPosition(adapter.itemCount - 1)
        } else {
            tasksRecyclerView.scrollToPosition(0)
        }
    }

    // Функция которая проверяет открыта или нет клавиатура
    private fun isKeyboardOpen(activity: Activity): Boolean {
        val inputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        return inputMethodManager.isAcceptingText
    }

    // Функция которая скрывает клавиатуру
    // чтобы её вызвать hideKeyboard(etAddTask, this)
    fun hideKeyboard(view: View, context: Context) {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    // Функция для обновления списка задач из базы данных
    private fun updateListTasks() {
        // Получаем список задач из базы данных
        val tasksList = dbHelper.getAllTasks()
        // Обновляем список задач в адаптере
        adapter.updateTasks(tasksList)
    }
}
