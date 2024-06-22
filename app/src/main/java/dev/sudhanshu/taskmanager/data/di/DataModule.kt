package dev.sudhanshu.taskmanager.data.di

import android.content.Context
import android.content.SharedPreferences
import dev.sudhanshu.taskmanager.data.repository.UserRepositoryImpl
import dev.sudhanshu.taskmanager.data.source.remote.FirebaseAuthService
import dev.sudhanshu.taskmanager.data.source.remote.FirestoreService
import dev.sudhanshu.taskmanager.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.sudhanshu.taskmanager.data.repository.TaskRepositoryImpl
import dev.sudhanshu.taskmanager.domain.repository.TaskRepository
import dev.sudhanshu.taskmanager.domain.usecase.AddTaskUseCase
import dev.sudhanshu.taskmanager.domain.usecase.GetTasksForUserUseCase
import dev.sudhanshu.taskmanager.domain.usecase.SignOutUserUseCase
import dev.sudhanshu.taskmanager.domain.usecase.UpdateTaskUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {



    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseAuthService(auth: FirebaseAuth): FirebaseAuthService = FirebaseAuthService(auth)

    @Provides
    @Singleton
    fun provideFirestoreService(firestore: FirebaseFirestore): FirestoreService = FirestoreService(firestore)

    @Provides
    @Singleton
    fun provideUserRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): UserRepository = UserRepositoryImpl(auth, firestore)



    @Provides
    @Singleton
    fun provideTaskRepository(db: FirebaseFirestore): TaskRepository {
        return TaskRepositoryImpl(db)
    }

    @Provides
    fun provideAddTaskUseCase(taskRepository: TaskRepository): AddTaskUseCase {
        return AddTaskUseCase(taskRepository)
    }

    @Provides
    fun provideUpdateTaskUseCase(taskRepository: TaskRepository): UpdateTaskUseCase {
        return UpdateTaskUseCase(taskRepository)
    }

    @Provides
    fun provideGetTasksForUserUseCase(taskRepository: TaskRepository): GetTasksForUserUseCase {
        return GetTasksForUserUseCase(taskRepository)
    }




}
