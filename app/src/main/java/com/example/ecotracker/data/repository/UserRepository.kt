package com.example.ecotracker.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.ecotracker.data.local.dao.UserDao
import com.example.ecotracker.data.local.entity.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import com.example.ecotracker.data.datastore.UserPreferences
import com.example.ecotracker.data.datastore.UserSession
import kotlinx.coroutines.flow.firstOrNull

sealed class UserError(message: String) : Exception(message) {
    object DatabaseError : UserError("Erro no banco de Dados")
    object UserNotFound : UserError("Usuário não encontrado")
}

class UserRepository(
    private val userDao: UserDao,
    private val userPreferences: UserPreferences
) {
    // Crud
    suspend fun registerUser(user: User): Result<Unit> {
        return try {
            userDao.insertUser(user)
            Result.success(Unit)
        }
        catch (e: Exception){
            Result.failure(UserError.DatabaseError)
        }
    }
    suspend fun updateUser(user: User): Result<Unit> {
        return try {
            val row = userDao.updateUser(user)
            // Valor diferente de 0 sucesso na operação, caso contrário erro
            if(row == 0) {
                Result.failure(UserError.UserNotFound)
            }
            else Result.success(Unit)
        }
        catch (e: Exception){
            Result.failure(UserError.DatabaseError)
        }
    }

    suspend fun deleteUser(userId: Long): Result<Unit> {
        return try {
            val rows = userDao.deleteUserByID(userId)

            if (rows == 0) {
                Result.failure(UserError.UserNotFound)
            } else {
                // Se o usuário deletado for o que está logado, limpa a sessão
                val loggedUser = userPreferences.userFlow.firstOrNull()

                if (loggedUser?.id == userId) {
                    userPreferences.clearUser()
                }

                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(UserError.DatabaseError)
        }
    }

    // Retorna o usuário logado (ou null)
    fun getLoggedUserPreference(): Flow<UserSession?> = userPreferences.userFlow

    suspend fun saveLoggedUser(user: UserSession){
        userPreferences.saveUser(user.id, user.name, user.email)
    }

    suspend fun logout(): Result<Unit> {
        return try {
            userPreferences.clearUser()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(UserError.DatabaseError)
        }
    }

    // Querys

    fun getUserByName(username: String): Flow<PagingData<User>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = {
                userDao.getUserByName(username)
            }
        ).flow
    }
    fun getUserById(userId: Long): Flow<User?> {
        return userDao.getUserById(userId)
    }

    fun getUserByEmail(email: String): Flow<User?> {
        return userDao.getUserByEmail(email)
            .catch { exception ->
                emit(null)
            }

    }

}