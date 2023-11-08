package uz.gita.uzenglanguage.utils

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import uz.gita.uzenglanguage.R


fun String.spannable(query:String,context: Context):SpannableString{
    val span=SpannableString(this)
    val color=ForegroundColorSpan(ContextCompat.getColor(context, R.color.teal_700))
    val startIndex=this.indexOf(query,0,true)
    if (startIndex>-1){
        span.setSpan(color,startIndex,startIndex+query.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    return span
}