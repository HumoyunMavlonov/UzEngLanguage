package uz.gita.uzenglanguage.db_rrom

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy

interface DictionaryDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(t: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(t: List<T>)

    @Delete
    fun delete(t: T)
}
