import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "TasksDatabase.db"

        // Структура 1 таблицы
        private const val TABLE_NAME = "Tasks"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TEXT = "text"
        private const val COLUMN_DONE = "done"
    }

    init {
        this.writableDatabase
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Метод создания таблиц
        createFirstTable(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Метод который срабатывает при изменении версии БД
        dropTable(db, TABLE_NAME)
        onCreate(db)
    }

    private fun createFirstTable(db: SQLiteDatabase) {
        // Метод создания первой таблицы
        val createTableQuery =
            "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_TEXT TEXT, $COLUMN_DONE INT)"
        db.execSQL(createTableQuery)
    }

    private fun dropTable(db: SQLiteDatabase, tableName: String) {
        // Метод удаления таблицы
        db.execSQL("DROP TABLE IF EXISTS $tableName")
    }

    // Методы добавления в БД

    fun insertDataToTasks(text: String) {
        // Публичный метод добавления данных в первую таблицу
        insertDataToTableTasks(TABLE_NAME, COLUMN_TEXT, text, COLUMN_DONE)
    }

    private fun insertDataToTableTasks(tableName: String, columnName: String, text: String, columnDoneName: String) {
        // Метод добавления данных в первую таблицу
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put(columnName, text)
        contentValues.put(columnDoneName, 0)
        db.insert(tableName, null, contentValues)
    }


    // Получение всех записей

    fun getAllTasks(): MutableList<MutableList<Any>> {
        return getAllTasksFromTable()
    }

    private fun getAllTasksFromTable(): MutableList<MutableList<Any>> {
        val tasksList = mutableListOf<MutableList<Any>>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(query, null)
        cursor.use {
            while (it.moveToNext()) {
                val id = it.getInt(it.getColumnIndex(COLUMN_ID))
                val text = it.getString(it.getColumnIndex(COLUMN_TEXT))
                val done = it.getInt(it.getColumnIndex(COLUMN_DONE))
                val taskData = mutableListOf<Any>()
                taskData.add(id)
                taskData.add(text)
                taskData.add(done)
                tasksList.add(taskData)
            }
        }
        return tasksList
    }

    // Удаление записи
    fun deleteTaskById(id: Int) {
        deleteTaskById(TABLE_NAME, id)
    }

    private fun deleteTaskById(tableName: String, id: Int) {
        val db = writableDatabase
        db.delete(tableName, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    // Удаление всех записей в таблице
    fun deleteAllTasks() {
        val db = writableDatabase
        db.delete(TABLE_NAME, null, null)
    }


    // Изменяет значение поля done на противоположное
    fun toggleDoneStatus(text: String) {
        val db = writableDatabase
        val updateQuery = "UPDATE $TABLE_NAME SET $COLUMN_DONE = CASE WHEN $COLUMN_DONE = 0 THEN 1 ELSE 0 END WHERE $COLUMN_TEXT = ?"
        db.execSQL(updateQuery, arrayOf(text))
    }

}
