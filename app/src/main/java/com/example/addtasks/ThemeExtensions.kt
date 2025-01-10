package com.example.addtasks

import android.content.Context
import android.util.TypedValue
import androidx.core.graphics.ColorUtils

fun Context.isDarkThemeEnabled(): Boolean {
    // Функция проверки какая тема используется в устройстве темная или светлая,
    // Чтобы вызвать context.isDarkThemeEnabled(),
    // в качестве context может быть this
    val typedValue = TypedValue()
    this.theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true)
    val color = typedValue.data

    // Проверяем, является ли основной цвет текста темным
    return ColorUtils.calculateLuminance(color) < 0.5
}
