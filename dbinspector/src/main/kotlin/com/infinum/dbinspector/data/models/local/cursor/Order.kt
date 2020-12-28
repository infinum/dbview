package com.infinum.dbinspector.data.models.local.cursor

import androidx.annotation.DrawableRes
import com.infinum.dbinspector.R

enum class Order(val rawValue: String, @DrawableRes val icon: Int) {
    ASCENDING("ASC", R.drawable.dbinspector_ic_sort_arrows_desc),
    DESCENDING("DESC", R.drawable.dbinspector_ic_sort_arrows_asc);

    companion object {

        // TODO: Call sites should be only in mappers
        operator fun invoke(value: String) = values().firstOrNull { it.rawValue == value }
            ?: ASCENDING
    }
}
