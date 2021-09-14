package ru.netology.nmedia.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import ru.netology.nmedia.dto.Attachment

class Converter {
    @TypeConverter
    fun fromAttachment(attachment: Attachment?): String = Gson().toJson(attachment)

    @TypeConverter
    fun jsonToAttachment(attachmentJson: String): Attachment? =
        Gson().fromJson(attachmentJson, Attachment::class.java)
}