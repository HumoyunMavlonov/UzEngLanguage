package uz.gita.uzenglanguage.ui.main

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.opengl.Visibility
import android.os.Bundle
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SearchView
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import uz.gita.uzenglanguage.R
import uz.gita.uzenglanguage.adapters.DictionaryAdapter
import uz.gita.uzenglanguage.databinding.ActivityMainBinding
import uz.gita.uzenglanguage.db.SharedPref
import uz.gita.uzenglanguage.db_rrom.DictionaryDatabase
import uz.gita.uzenglanguage.db_rrom.WordsDao
import uz.gita.uzenglanguage.model.WordData
import uz.gita.uzenglanguage.ui.favourite.FavouriteActivity
import uz.gita.uzenglanguage.ui.info.InfoActivity
import uz.gita.uzenglanguage.utils.spannable
import java.util.Locale

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var wordDao: WordsDao
    private lateinit var adapter: DictionaryAdapter
    private lateinit var handle: Handler
    private var searchedText=""

    private var tts: TextToSpeech? = null

    private lateinit var binding: ActivityMainBinding

    private val sharedPref by lazy { SharedPref.getInstance(applicationContext) }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        DictionaryDatabase.init(this)
        wordDao = DictionaryDatabase.getInstance().dictionaryDao()

        tts = TextToSpeech(this, this)
        adapter = DictionaryAdapter(wordDao.getAllWords(), "")

        handle = Handler()

        binding.apply {
            rvDictionary.adapter = adapter
            rvDictionary.layoutManager = LinearLayoutManager(this@MainActivity)

            binding.inputSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    handle.removeCallbacksAndMessages(null)
                    searchedText=query.toString()
                    query?.let {
                        adapter.cursor = wordDao.getWordByQuery(it.trim())
                        adapter.setNewQuery(it.trim())
                        adapter.query = it.trim()
                        adapter.notifyDataSetChanged()
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    handle.removeCallbacksAndMessages(null)
                    searchedText=newText.toString()

                    handle.postDelayed({

                        newText?.let {
                            binding.rvDictionary.visibility=View.VISIBLE
                            adapter.cursor = wordDao.getWordByQuery(it.trim())
                            if (adapter.cursor.count > 0){
                            adapter.setNewQuery(it.trim())
                            adapter.query = it.trim()
                            adapter.notifyDataSetChanged()
                            } else{
                                binding.rvDictionary.visibility=View.INVISIBLE
                                binding.notFound.visibility=View.VISIBLE
                            }
                        }
                    }, 0)
                    return true
                }
            })

            btnFavourite.setOnClickListener {
                val intent = Intent(this@MainActivity, FavouriteActivity::class.java)
                intent.putExtra("lang", sharedPref.language)
                startActivity(intent)
            }
            btnInfo.setOnClickListener {
                val intent = Intent(this@MainActivity, InfoActivity::class.java)
                startActivity(intent)
            }
        }

        adapter.setOnFavClick { id, like ,position->
            wordDao.updateFav(id, like)
            adapter.submitCursor(wordDao.getWordByQuery(searchedText))
            adapter.notifyDataSetChanged()
        }

        adapter.setDataListener {
            showItemDialog(it)
        }
    }

    private fun showItemDialog(item: WordData) {
        val dialog = Dialog(this@MainActivity)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        val window = dialog.window
        window!!.attributes = lp

        dialog.setContentView(R.layout.custom_word_dialog)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val word: AppCompatTextView = dialog.findViewById(R.id.txtWord)
        val type: AppCompatTextView = dialog.findViewById(R.id.txtType)
        val transcript: AppCompatTextView = dialog.findViewById(R.id.txtTranscript)
        val translate: AppCompatTextView = dialog.findViewById(R.id.txtTrans)

        val audio: AppCompatImageView = dialog.findViewById(R.id.btnVolume)
//        val btnClose: AppCompatButton = dialog.findViewById(R.id.btnClose)

        item.apply {
            word.text = this.word
            type.text = this.type
            transcript.text = this.transcript
            translate.text = this.translate

            if (sharedPref.language == "english") {
                audio.visibility = View.VISIBLE
                audio.isClickable = true
            } else {
                audio.visibility = View.INVISIBLE
                audio.isClickable = false
            }
        }

        audio.setOnClickListener {
            tts!!.speak(item.word, TextToSpeech.QUEUE_FLUSH, null, "")
        }

//        btnClose.setOnClickListener {
//            dialog.dismiss()
//        }
        dialog.create()
        dialog.show()
        dialog.setCancelable(true)
    }

    override fun onResume() {
        super.onResume()
        updateAdapterData()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "The Language is not supported!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateAdapterData() {
        adapter.cursor = wordDao.getAllWords()
        adapter.notifyDataSetChanged()
    }
}
