package dev.sudhanshu.taskmanager.domain.usecase

import dev.sudhanshu.taskmanager.domain.model.User
import dev.sudhanshu.taskmanager.domain.repository.UserRepository
import dev.sudhanshu.taskmanager.util.Resource
import javax.inject.Inject

class SaveUserUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(user: User): Resource<Unit> {
        return userRepository.saveUser(user)
    }
}
