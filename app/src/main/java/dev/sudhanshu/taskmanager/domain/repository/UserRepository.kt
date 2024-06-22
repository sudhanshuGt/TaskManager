package dev.sudhanshu.taskmanager.domain.repository

import dev.sudhanshu.taskmanager.domain.model.User
import dev.sudhanshu.taskmanager.util.Resource


interface UserRepository {
    suspend fun getUser(): Resource<User>
    suspend fun saveUser(user: User): Resource<Unit>
    suspend fun singOut() : Resource<Unit>
}
