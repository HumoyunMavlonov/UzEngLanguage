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

@Suppress("UNREACHABLE_CODE")
class FavouriteAdapter : RecyclerView.Adapter<FavouriteAdapter.DictionaryHolder>() {

    private  var cursor: Cursor?=null
    private var isValid = false

    private var datalistener: ((WordData) -> Unit)? = null
    fun submitCursor(cursor: Cursor) {
        this.cursor = cursor
        notifyDataSetChanged()
    }

    fun setDataListener(block: (WordData) -> Unit) {
        datalistener = block
    }


    private var onFavClick: ((Int, Int) -> Unit)? = null

    fun setOnFavClick(listener: (Int, Int) -> Unit) {
        onFavClick = listener
    }
    private val notifyingDataSetObserver = object : DataSetObserver() {
        @SuppressLint("NotifyDataSetChanged")
        override fun onChanged() {
            isValid = true
            notifyDataSetChanged()
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun onInvalidated() {
            isValid = false
            notifyDataSetChanged()
        }
    }

    @SuppressLint("Range")
    inner class DictionaryHolder(private val binding: ItemDictionaryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            cursor?.let { cursor: Cursor ->
                if (cursor.moveToPosition(position)) {
                    binding.textWord.text = cursor.getString(cursor.getColumnIndex("english"))
                    binding.imgLike.setImageResource(if (cursor.getInt(cursor.getColumnIndex("is_favourite")) == 0) R.drawable.no_like else R.drawable.like)
                }
            }
        }

        init {
            binding.imgLike.setOnClickListener {
                cursor?.let { cursor ->
                    if (cursor.moveToPosition(adapterPosition)) {
                        val count = cursor.getInt(cursor.getColumnIndex("is_favourite"))
                        val id = cursor.getInt(cursor.getColumnIndex("id"))
                        onFavClick?.invoke(id, if (count == 1) 0 else 1)
                    }
                }
            }
            binding.root.setOnClickListener {
                cursor?.moveToPosition(adapterPosition)
                val item = DictionaryEntity(
                    cursor!!.getInt(cursor!!.getColumnIndex("id")),
                    cursor!!.getString(cursor!!.getColumnIndex("english")),
                    cursor!!.getString(cursor!!.getColumnIndex("type")),
                    cursor!!.getString(cursor!!.getColumnIndex("transcript")),
                    cursor!!.getString(cursor!!.getColumnIndex("uzbek")),
                    cursor!!.getString(cursor!!.getColumnIndex("countable")),
                    cursor!!.getInt(cursor!!.getColumnIndex("is_favourite"))
                )
                datalistener?.invoke(WordData(
                    cursor!!.getInt(cursor!!.getColumnIndex("id")),
                    cursor!!.getString(cursor!!.getColumnIndex("english")),
                    cursor!!.getString(cursor!!.getColumnIndex("type")),
                    cursor!!.getString(cursor!!.getColumnIndex("transcript")),
                    cursor!!.getString(cursor!!.getColumnIndex("uzbek")),
                    cursor!!.getInt(cursor!!.getColumnIndex("is_favourite"))
                    ))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DictionaryHolder {
        return DictionaryHolder(ItemDictionaryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return cursor?.count ?: 0

    }

    override fun onBindViewHolder(holder: DictionaryHolder, position: Int) {
        holder.bind(position)
    }

}
