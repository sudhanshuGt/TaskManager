package dev.sudhanshu.taskmanager.domain.usecase

import dev.sudhanshu.taskmanager.util.Resource
import dev.sudhanshu.taskmanager.domain.model.User
import dev.sudhanshu.taskmanager.domain.repository.UserRepository
import javax.inject.Inject

class SignOutUserUseCase @Inject constructor(private val userRepository: UserRepository) {

    suspend operator fun invoke(): Resource<Unit> {
        return userRepository.singOut()
    }
}
