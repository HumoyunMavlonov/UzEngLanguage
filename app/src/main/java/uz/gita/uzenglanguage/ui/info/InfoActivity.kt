package uz.gita.uzenglanguage.ui.info

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import uz.gita.uzenglanguage.R
import uz.gita.uzenglanguage.databinding.AboutNotesBinding
import uz.gita.uzenglanguage.databinding.ActivityFavouriteBinding
import uz.gita.uzenglanguage.ui.main.MainActivity

class InfoActivity:AppCompatActivity() {
    private lateinit var binding: AboutNotesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AboutNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backToMain2.setOnClickListener{
            val intent3 = Intent(this@InfoActivity, MainActivity::class.java)
            startActivity(intent3)
            finish()

        }
    }
}