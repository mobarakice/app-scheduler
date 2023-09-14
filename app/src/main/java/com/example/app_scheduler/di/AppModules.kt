package com.example.app_scheduler.di

import android.content.Context
import androidx.room.Room
import com.example.app_scheduler.data.db.AppDatabase
import com.example.app_scheduler.data.db.ScheduleRepository
import com.example.app_scheduler.data.db.ScheduleRepositoryImpl
import com.example.app_scheduler.data.db.dao.ScheduleDao
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindScheduleRepository(repository: ScheduleRepositoryImpl): ScheduleRepository


}


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "app-scheduler.db"
        ).build()

    }

    @Provides
    fun provideScheduleDao(db: AppDatabase): ScheduleDao = db.scheduleDao()
}
