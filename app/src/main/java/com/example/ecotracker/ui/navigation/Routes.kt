package com.example.ecotracker.ui.navigation

object Routes {
    const val LOGIN = "login"
    const val MAIN = "main"
    const val HOME = "home"
    const val HABITS = "habits"
    const val ADD_HABIT = "add_habit"
    const val EDIT_HABIT = "add_habit/{habitId}"
    const val IMPACT = "impact"
    const val PROFILE = "profile"
    const val EDIT_PROFILE = "edit_profile"
    const val REGISTER ="register"
    const val START ="start"

    fun editHabit(habitId: Long) = "add_habit/$habitId"
}