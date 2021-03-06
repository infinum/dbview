package com.infinum.dbinspector.extensions

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import java.io.File

internal fun Context.dataStoreFile(fileName: String): File =
    File(filesDir, "datastore/$fileName")

internal val Context.databaseDir: String
    get() = "${filesDir.path.substring(0, filesDir.path.lastIndexOf("/"))}/databases"

internal fun Context.drawableFromAttribute(@AttrRes attrId: Int): Drawable {
    val typedValue = TypedValue()
    this.theme.resolveAttribute(attrId, typedValue, true)
    val imageResId = typedValue.resourceId
    return ContextCompat.getDrawable(this, imageResId)
        ?: throw IllegalArgumentException("Cannot load drawable $imageResId")
}

@ColorInt
internal fun Context.getColorFromAttribute(@AttrRes attrId: Int): Int {
    val typedArray: TypedArray = theme.obtainStyledAttributes(intArrayOf(attrId))
    val color = typedArray.getColor(0, 0)
    typedArray.recycle()
    return color
}
