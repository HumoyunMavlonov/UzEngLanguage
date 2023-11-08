package uz.gita.uzenglanguage.model

data class WordData(
    val id: Int,
    val word: String,
    val type: String,
    val transcript: String,
    val translate: String,
    val favourite: Int
): java.io.Serializable