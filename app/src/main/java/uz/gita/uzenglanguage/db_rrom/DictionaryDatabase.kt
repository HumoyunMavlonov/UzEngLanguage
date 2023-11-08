
package uz.gita.uzenglanguage.db_rrom

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DictionaryEntity::class], version = 1)
abstract class DictionaryDatabase : RoomDatabase() {

    abstract fun dictionaryDao(): WordsDao

    companion object {
        private var dictionaryDatabase: DictionaryDatabase? = null

        fun init(context: Context) {
            if (dictionaryDatabase == null)
                dictionaryDatabase = Room.databaseBuilder(
                    context.applicationContext,
                    DictionaryDatabase::class.java,
                    "Dictionary.db"
                )
                    .createFromAsset("dictionary.db")
                    .allowMainThreadQueries()
                    .build()
        }

        fun getInstance(): DictionaryDatabase = dictionaryDatabase!!
    }
}
