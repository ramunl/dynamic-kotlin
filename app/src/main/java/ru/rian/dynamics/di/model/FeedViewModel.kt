/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.rian.dynamics.di.model

import android.arch.lifecycle.ViewModel
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import ru.rian.dynamics.db.FeedDao
import ru.rian.dynamics.retrofit.model.Feed

/**
 * View Model for the [UserActivity]
 */
class FeedViewModel(private val dataSource: FeedDao) : ViewModel() {

    /**
     * Get the user name of the user.
     * @return a [Flowable] that will emit every time the user name has been updated.
     */
    // for every emission of the user, get the user name
    fun allFeeds(): Observable<List<Feed>> {
        return dataSource.getAllFeeds().map { result -> result }
    }

    /**
     * Update the user name.
     * @param userName the new user name
     * *
     * @return a [Completable] that completes when the user name is updated
     */
    fun updateUserName(feeds: List<Feed>): Completable {
        return Completable.fromAction {
            dataSource.insert(feeds)
        }
    }

    /*
    *   @Provides
    fun insertFeeds(db: DynamicsDataBase, feeds: List<Feed>) {
        return db.feedDao().insert(feeds)
    }

    @Provides
    fun provideFeeds(db: DynamicsDataBase): List<Feed> {
        return db.feedDao().getAllFeeds()
    }

    @Provides
    fun provideFeedById(sid: String, db: DynamicsDataBase): Feed {
        return db.feedDao().getFeedsById(sid).first()
    }
    * */
}
