package com.starwars.exercise.ui.theme

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import androidx.core.content.edit

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val application: Application
) : ViewModel() {

    private val prefs = application.getSharedPreferences("settings", Context.MODE_PRIVATE)

    // null = follow OS, true = dark, false = light
    private val _isDarkTheme = MutableStateFlow<Boolean?>(
        when (prefs.getString("theme", "system")) {
            "dark" -> true
            "light" -> false
            else -> null
        }
    )
    val isDarkTheme: StateFlow<Boolean?> = _isDarkTheme

    fun setTheme(mode: ThemeMode) {
        _isDarkTheme.value = when (mode) {
            ThemeMode.DARK -> true
            ThemeMode.LIGHT -> false
            ThemeMode.SYSTEM -> null
        }
        prefs.edit { putString("theme", mode.name.lowercase()) }
    }
}

enum class ThemeMode { LIGHT, DARK, SYSTEM }