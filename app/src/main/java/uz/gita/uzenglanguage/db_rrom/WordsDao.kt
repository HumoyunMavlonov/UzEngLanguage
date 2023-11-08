package uz.gita.uzenglanguage.db_rrom

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Query
import uz.gita.uzenglanguage.db_rrom.DictionaryEntity
import uz.gita.uzenglanguage.db_rrom.DictionaryDao

@Dao
interface WordsDao : DictionaryDao<DictionaryEntity> {

    @Query("select * from dictionary")
    fun getAllWords(): Cursor

    @Query("UPDATE dictionary SET is_favourite = :fav WHERE id = :id ")
    fun updateFav(id: Int, fav: Int)

    @Query("SELECT * FROM dictionary WHERE english like '%'  ||:searchQuery||   '%'")
    fun getWordByQuery(searchQuery: String): Cursor

    @Query("SELECT * FROM dictionary WHERE is_favourite==1")
    fun getAllFavorit(): Cursor
}
