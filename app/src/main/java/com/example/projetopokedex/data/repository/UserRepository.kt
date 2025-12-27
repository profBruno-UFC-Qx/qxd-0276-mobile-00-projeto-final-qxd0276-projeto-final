package com.example.projetopokedex.data.repository

import com.example.projetopokedex.data.auth.JwtGenerator
import com.example.projetopokedex.data.local.UserLocalDataSource
import com.example.projetopokedex.data.model.UserLocal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class UserRepository(
    private val localDataSource: UserLocalDataSource
) {

    val isLoggedIn: Flow<Boolean> =
        localDataSource.tokenFlow.map { !it.isNullOrBlank() }

    val userName: Flow<String?> = flow {
        emit(localDataSource.getUserName())
    }

    // CADASTRO
    suspend fun register(user: UserLocal): Result<Unit> {
        return try {
            val existingUser = localDataSource.getUserByEmail(user.email)
            if (existingUser != null) {
                return Result.failure(IllegalArgumentException("E-mail já cadastrado"))
            }

            localDataSource.saveUser(user)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // LOGIN
    suspend fun login(email: String, password: String): Result<String> {
        return try {
            val storedUser = localDataSource.getUserByEmail(email)
                ?: throw IllegalStateException("Usuário não encontrado")

            if (storedUser.password != password) {
                throw IllegalArgumentException("Senha inválida")
            }

            val token = JwtGenerator.generateToken(subject = storedUser.email)
            localDataSource.saveToken(token)
            Result.success(token)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // LOGOUT
    suspend fun logout() {
        localDataSource.clearToken()
    }
}
