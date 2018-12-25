package ru.rian.dynamics.db

import android.arch.persistence.room.*
import io.reactivex.Flowable
import ru.rian.dynamics.retrofit.model.Feed

/**
 * Created by Amanjeet Singh on 14/11/17.
 */
@Dao
interface FeedDao {

    @Query("SELECT * FROM Feed WHERE sid LIKE :feedId")
    fun getFeedsById(feedId: String): List<Feed>

    @Query("SELECT * FROM Feed")
    fun getAllFeeds(): Flowable<List<Feed>>


    @Query("SELECT * FROM Feed WHERE type LIKE :type")
    fun getAllFeeds(type: String): Flowable<List<Feed>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(feeds: List<Feed>)

    //   @Insert
    //  fun insert(feeds: List<Feed>)

    @Insert
    fun insert(feed: Feed)

    @Delete
    fun delete(feed: Feed)


}