package com.starwars.exercise

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.starwars.exercise.ui.theme.ThemeMode
import com.starwars.exercise.ui.theme.ThemeViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class ThemeViewModelTest {

    private lateinit var application: Application
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    @Before
    fun setUp() {
        application = mockk()
        sharedPreferences = mockk()
        editor = mockk(relaxed = true)

        every { application.getSharedPreferences("settings", Context.MODE_PRIVATE) } returns sharedPreferences
        every { sharedPreferences.edit() } returns editor
        every { editor.putString(any(), any()) } returns editor
    }

    // ── Test 1 ──────────────────────────────────────────────────────────────
    @Test
    fun `isDarkTheme is null when prefs returns system`() {
        // Given
        every { sharedPreferences.getString("theme", "system") } returns "system"

        // When
        val viewModel = ThemeViewModel(application)

        // Then – null means "follow OS"
        assertNull(viewModel.isDarkTheme.value)
    }

    // ── Test 2 ──────────────────────────────────────────────────────────────
    @Test
    fun `isDarkTheme is true when prefs returns dark`() {
        // Given
        every { sharedPreferences.getString("theme", "system") } returns "dark"

        // When
        val viewModel = ThemeViewModel(application)

        // Then
        assertEquals(true, viewModel.isDarkTheme.value)
    }

    // ── Test 3 ──────────────────────────────────────────────────────────────
    @Test
    fun `isDarkTheme is false when prefs returns light`() {
        // Given
        every { sharedPreferences.getString("theme", "system") } returns "light"

        // When
        val viewModel = ThemeViewModel(application)

        // Then
        assertEquals(false, viewModel.isDarkTheme.value)
    }

    // ── Test 4 ──────────────────────────────────────────────────────────────
    @Test
    fun `setTheme DARK updates isDarkTheme to true and persists to prefs`() {
        // Given
        every { sharedPreferences.getString("theme", "system") } returns "system"
        val viewModel = ThemeViewModel(application)

        // When
        viewModel.setTheme(ThemeMode.DARK)

        // Then – in-memory state
        assertEquals(true, viewModel.isDarkTheme.value)
        // Then – persisted value
        verify { editor.putString("theme", "dark") }
    }

    // ── Test 5 ──────────────────────────────────────────────────────────────
    @Test
    fun `setTheme SYSTEM resets isDarkTheme to null and persists system`() {
        // Given – start in dark mode
        every { sharedPreferences.getString("theme", "system") } returns "dark"
        val viewModel = ThemeViewModel(application)
        assertEquals(true, viewModel.isDarkTheme.value)

        // When
        viewModel.setTheme(ThemeMode.SYSTEM)

        // Then
        assertNull(viewModel.isDarkTheme.value)
        verify { editor.putString("theme", "system") }
    }
}