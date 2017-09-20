package com.raywenderlich.marsrovers.models


enum class ROW_TYPE {
    PHOTO,
    HEADER
}
data class PhotoRow(var type: ROW_TYPE, var photo: Photo?, var header: String?)