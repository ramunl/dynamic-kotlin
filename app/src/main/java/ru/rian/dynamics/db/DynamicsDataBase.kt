package ru.rian.dynamics.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import ru.rian.dynamics.retrofit.model.Feed

/**
 * Created by Amanjeet Singh on 14/11/17.
 */
@Database(entities = [Feed::class], version = 1, exportSchema = false)
abstract class DynamicsDataBase : RoomDatabase() {

    abstract fun feedDao(): FeedDao

    companion object {
        @Volatile
        private var INSTANCE: DynamicsDataBase? = null

        fun getInstance(context: Context): DynamicsDataBase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                DynamicsDataBase::class.java, "Sample.db"
            )
                .build()
    }
}