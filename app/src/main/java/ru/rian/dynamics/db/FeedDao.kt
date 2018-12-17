package ru.rian.dynamics.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import io.reactivex.Observable
import ru.rian.dynamics.retrofit.model.Feed

/**
 * Created by Amanjeet Singh on 14/11/17.
 */
@Dao
interface FeedDao {

    @Query("SELECT * FROM Feed WHERE sid LIKE :feedId")
    fun getFeedsById(feedId: String): List<Feed>


    @Query("SELECT * FROM Feed")
    fun getAllFeeds(): Observable<List<Feed>>

    @Insert
    fun insert(feeds: List<Feed>)

    @Insert
    fun insert(feed: Feed)

    @Delete
    fun delete(feed: Feed)
}