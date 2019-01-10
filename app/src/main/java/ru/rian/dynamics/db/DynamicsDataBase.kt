package ru.rian.dynamics.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import ru.rian.dynamics.InitApp
import ru.rian.dynamics.retrofit.model.Feed

@Database(entities = [Feed::class], version = 1, exportSchema = false)
abstract class DynamicsDataBase : RoomDatabase() {

    abstract fun feedDao(): FeedDao

    companion object {
        @Volatile
        private var INSTANCE: DynamicsDataBase? = null

        fun getInstance(): DynamicsDataBase =
            INSTANCE ?: synchronized(this) { INSTANCE ?: buildDatabase().also { INSTANCE = it } }

        private fun buildDatabase() =
            Room.databaseBuilder(InitApp.appContext(), DynamicsDataBase::class.java, "Sample.db").build()
    }
}