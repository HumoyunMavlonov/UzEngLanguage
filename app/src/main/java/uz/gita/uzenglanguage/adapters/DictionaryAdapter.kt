//package uz.gita.uzenglanguage.adapters
//
//import android.annotation.SuppressLint
//import android.database.Cursor
//import android.database.DataSetObserver
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.appcompat.view.menu.MenuView.ItemView
//import androidx.recyclerview.widget.RecyclerView
//import uz.gita.uzenglanguage.R
//import uz.gita.uzenglanguage.model.WordData
//import uz.gita.uzenglanguage.utils.spannable
//
//class DictionaryAdapter(var cursor: Cursor, var query: String):
//    RecyclerView.Adapter<DictionaryAdapter.ViewHolder>() {
//
//
//    private var isValid = false
//    private var lang = ""
//
//    private var clickListener: ((WordData) -> Unit)? = null
//    private var clickLikeListener: ((Int, Int) -> Unit)? = null
//
//    fun setClickListener(listener: (WordData) -> Unit) {
//        clickListener = listener
//    }
//
//    fun setClickLikeListener(like: (Int, Int) -> Unit) {
//        clickLikeListener = like
//    }
//
//    private val notifyingDataSetObserver = object : DataSetObserver() {
//        @SuppressLint("NotifyDataSetChanged")
//        override fun onChanged() {
//            isValid = true
//            notifyDataSetChanged()
//        }
//
//        @SuppressLint("NotifyDataSetChanged")
//        override fun onInvalidated() {
//            isValid = false
//            notifyDataSetChanged()
//        }
//    }
//
//    init {
//        cursor.registerDataSetObserver(notifyingDataSetObserver)
//        isValid = true
//    }
//
//    @SuppressLint("NotifyDataSetChanged")
//    fun updateCursor(newCursor: Cursor) {
//        isValid = false
//        cursor.unregisterDataSetObserver(notifyingDataSetObserver)
//        cursor.close()
//
//        newCursor.registerDataSetObserver(notifyingDataSetObserver)
//        cursor = newCursor
//        isValid = true
//        notifyDataSetChanged()
//    }
//
//    @SuppressLint("Range")
//    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        private val textWord: TextView = view.findViewById(R.id.text_word)
//        private val imageLike: ImageView = view.findViewById(R.id.imgLike)
//
//        init {
//            imageLike.setOnClickListener {
//                cursor.moveToPosition(adapterPosition)
//                val cursorFav = cursor.getInt(cursor.getColumnIndex("favourite"))
//                val cursorId = cursor.getInt(cursor.getColumnIndex("id"))
//                if (cursorFav == 1) {
//                    imageLike.setImageResource(R.drawable.like)
//                } else imageLike.setImageResource(R.drawable.no_like)
//                clickLikeListener?.invoke(cursorId, cursorFav)
//            }
//
//            textWord.setOnClickListener {
//                cursor.moveToPosition(adapterPosition)
//                val english = cursor.getString(cursor.getColumnIndex("english"))
//                val uzbek = cursor.getString(cursor.getColumnIndex("uzbek"))
//
//                val word: String
//                val translate: String
//
//                if (lang == "english") {
//                    word = english
//                    translate = uzbek
//                } else {
//                    word = uzbek
//                    translate = english
//                }
//
//                val id = cursor.getInt(cursor.getColumnIndex("id"))
//                val type = cursor.getString(cursor.getColumnIndex("type"))
//                val like = cursor.getInt(cursor.getColumnIndex("favourite"))
//                val transcript = cursor.getString(cursor.getColumnIndex("transcript"))
//
//                clickListener?.invoke(WordData(id, word, type, transcript, translate, like))
//            }
//        }
//
//        fun bind() {
//            cursor.moveToPosition(adapterPosition)
//            val isFavourite: Int = cursor.getInt(cursor.getColumnIndex("favourite"))
//            val word = cursor.getString(cursor.getColumnIndex(lang))
//
//            if (query.isEmpty()){
//                textWord.text = word
//            }else{
//              textWord.text = word.spannable(query,itemView.context)
//            }
//            if (isFavourite == 1) {
//                imageLike.setImageResource(R.drawable.like)
//            } else imageLike.setImageResource(R.drawable.no_like)
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val inflater = LayoutInflater.from(parent.context)
//        val view = inflater.inflate(R.layout.item_dictionary, parent, false)
//        return ViewHolder(view)
//    }
//
//    override fun getItemCount(): Int = if (isValid) cursor.count else 0
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.bind()
//    }
//
//    fun setLang(lang: String) {
//        this.lang = lang
//    }
//    fun setNewQuery(q: String) {
//        query = q
//    }
//
//    fun onDestroy() {
//        cursor.unregisterDataSetObserver(notifyingDataSetObserver)
//        cursor.close()
//    }
//}