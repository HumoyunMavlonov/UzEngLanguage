package uz.gita.uzenglanguage.adapters

import android.annotation.SuppressLint
import android.database.Cursor
import android.database.DataSetObserver
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.gita.uzenglanguage.R
import uz.gita.uzenglanguage.databinding.ItemDictionaryBinding
import uz.gita.uzenglanguage.db_rrom.DictionaryEntity
import uz.gita.uzenglanguage.model.WordData
import uz.gita.uzenglanguage.utils.spannable

class DictionaryAdapter(var cursor: Cursor, var query: String) : RecyclerView.Adapter<DictionaryAdapter.DictionaryHolder>() {

    private var isValid = false
    private var onFavClick: ((Int, Int,Int) -> Unit)? = null
    private var datalistener: ((WordData) -> Unit)? = null

    fun setDataListener(block: (WordData) -> Unit) {
        datalistener = block
    }
    fun submitCursor(cursor: Cursor) {
        this.cursor = cursor
    }

    fun setOnFavClick(listener: (Int, Int,Int) -> Unit) {
        onFavClick = listener
    }


    @SuppressLint("Range")
    inner class DictionaryHolder(private val binding: ItemDictionaryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.imgLike.setOnClickListener {
                cursor.moveToPosition(adapterPosition)
                val count = cursor.getInt(cursor.getColumnIndex("is_favourite"))
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                if (count == 1) {
                    onFavClick?.invoke(id, 0,0)
                    binding.imgLike.setImageResource(R.drawable.like)
                } else {
                    binding.imgLike.setImageResource(R.drawable.no_like)
                    onFavClick?.invoke(id, 1,0)
                }
            }

            binding.root.setOnClickListener {
                cursor.moveToPosition(adapterPosition)
                val item = DictionaryEntity(
                    cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("english")),
                    cursor.getString(cursor.getColumnIndex("type")),
                    cursor.getString(cursor.getColumnIndex("transcript")),
                    cursor.getString(cursor.getColumnIndex("uzbek")),
                    cursor.getString(cursor.getColumnIndex("countable")),
                    cursor.getInt(cursor.getColumnIndex("is_favourite"))
                )
                datalistener?.invoke(
                    WordData(
                        cursor.getInt(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("english")),
                        cursor.getString(cursor.getColumnIndex("type")),
                        cursor.getString(cursor.getColumnIndex("transcript")),
                        cursor.getString(cursor.getColumnIndex("uzbek")),
                        cursor.getInt(cursor.getColumnIndex("is_favourite"))
                )
                )
            }
        }

        @SuppressLint("Range")
        fun bind() {
            val item = DictionaryEntity(
                cursor.getInt(cursor.getColumnIndex("id")),
                cursor.getString(cursor.getColumnIndex("english")),
                cursor.getString(cursor.getColumnIndex("type")),
                cursor.getString(cursor.getColumnIndex("transcript")),
                cursor.getString(cursor.getColumnIndex("uzbek")),
                cursor.getString(cursor.getColumnIndex("countable")),
                cursor.getInt(cursor.getColumnIndex("is_favourite"))
            )
            if (query.isEmpty())
                binding.textWord.text = item.english
            else
                binding.textWord.text = item.english?.spannable(query, itemView.context)


            binding.imgLike.setImageResource(
                if (item.isFavourite == 0) R.drawable.no_like
                else R.drawable.like
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DictionaryHolder {
        return DictionaryHolder(
            ItemDictionaryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    fun setNewQuery(q: String) {
        query = q
    }

    override fun getItemCount(): Int = cursor.count

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: DictionaryHolder, position: Int) {
        cursor.moveToPosition(position)
        holder.bind()
    }

}
