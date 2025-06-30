package rodriguez.jairo.proyectofinal_reviewssystem

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import rodriguez.jairo.proyectofinal_reviewssystem.entities.User

class UserSQLHelper (context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){
    companion object{
        private const val DATABASE_NAME="usuarios.db"
        private const val DATABASE_VERSION=1
        private const val TABLE_NAME="usuarios"
        private const val COLUMN_ID="_id"
        private const val COLUMN_NAME="name"
        private const val COLUMN_EMAIL="email"
        private const val COLUMN_GENDER="gender"
        private const val COLUMN_PASSWORD="password"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableUsers = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY, " +
                "$COLUMN_NAME TEXT," +
                "$COLUMN_EMAIL TEXT," +
                "$COLUMN_GENDER TEXT," +
                "$COLUMN_PASSWORD TEXT" +
                ")"

        db?.execSQL(createTableUsers)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableUsers = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableUsers)
        onCreate(db)
    }

    fun insertUser(user: User) {
        val db = writableDatabase
        val values = ContentValues().apply {
            //Parametros de las columnas
            put(COLUMN_NAME, user.name)
            put(COLUMN_EMAIL, user.email)
            put(COLUMN_GENDER, user.gender)
            put(COLUMN_PASSWORD, user.password)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }
}