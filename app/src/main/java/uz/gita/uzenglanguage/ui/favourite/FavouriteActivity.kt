package uz.gita.uzenglanguage.ui.favourite

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import uz.gita.uzenglanguage.adapters.FavouriteAdapter
import uz.gita.uzenglanguage.R
import uz.gita.uzenglanguage.databinding.ActivityFavouriteBinding
import uz.gita.uzenglanguage.db.SharedPref
import uz.gita.uzenglanguage.db_rrom.DictionaryDatabase
import uz.gita.uzenglanguage.model.WordData
import uz.gita.uzenglanguage.ui.main.MainActivity
import java.util.*

class FavouriteActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivityFavouriteBinding

    private val database = DictionaryDatabase.getInstance().dictionaryDao()
    private val sharedPref by lazy { SharedPref.getInstance(applicationContext) }
    private val adapter by lazy { FavouriteAdapter() }

    private var tts: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFavouriteBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.recyclerFav.adapter=adapter

        adapter.submitCursor(database.getAllFavorit())


        binding.btnBack.setOnClickListener{
            Log.d("TTT", "onCreate: back")
            val intent2 = Intent(this@FavouriteActivity, MainActivity::class.java)
            intent2.putExtra("lang", sharedPref.language)
            startActivity(intent2)
        }

        tts = TextToSpeech(this, this)

        adapter.setOnFavClick  { id, like ->
            database.updateFav(id,like)
            adapter.submitCursor(database.getAllFavorit())
            adapter.notifyDataSetChanged()

            if (database.getAllFavorit().count == 0) {
                binding.noFav.visibility = View.VISIBLE
            } else binding.noFav.visibility = View.INVISIBLE
        }

        binding.apply {
            recyclerFav.adapter = adapter
            recyclerFav.layoutManager = LinearLayoutManager(this@FavouriteActivity)

            val cursor = database.getAllFavorit()
            if (cursor.count == 0) {
                noFav.visibility = View.VISIBLE
            } else noFav.visibility = View.INVISIBLE

            btnBack.setOnClickListener {
                finish()
            }


            adapter.setDataListener { item ->
                showItemDialog(item)
            }
        }
    }

    private fun showItemDialog(item: WordData) {
        val dialog = Dialog(this@FavouriteActivity)

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

    override fun onBackPressed() {
        finish()
    }



    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "The Language is not supported!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}