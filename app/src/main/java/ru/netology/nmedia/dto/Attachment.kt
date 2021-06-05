package ru.netology.nmedia.dto

import ru.netology.nmedia.enums.AttachmentType

data class Attachment(
    val url: String,
    val description: String,
    val type: AttachmentType
)
