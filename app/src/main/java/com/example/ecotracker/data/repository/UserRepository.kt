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
    suspend fun logOutUser(): Result<Unit> {
        return try {
            val row = userDao.logOut()
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
    // Retorna o usuário logado (ou null)
    fun getLoggedUserPreference(): Flow<UserSession?> = userPreferences.userFlow
    suspend fun saveUser(user: UserSession) = userPreferences.saveUser(user.id, user.name, user.email)
    suspend fun clearUser() = userPreferences.clearUser()

    // Querys
    fun getLoggedUser(): Flow<User?> {
        return userDao.getLoggedUser()
            .catch { exception ->
                emit(null)
            }
    }

    fun getUserByName(username: String): Flow<PagingData<User>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = {
                userDao.getUserByName(username)
            }
        ).flow
    }

    fun getUserByEmail(email: String): Flow<User?> {
        return userDao.getUserByEmail(email)
            .catch { exception ->
                emit(null)
            }

    }

}