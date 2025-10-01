package com.sky31.gongmultiplatform.util

sealed class AuthState {
    data object Unauthenticated: AuthState()
    data object Loading: AuthState()
    data object Authenticated: AuthState()

    data class Error(val message: String): AuthState()
}