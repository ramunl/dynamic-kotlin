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
import ru.rian.dynamics.db.FeedDao
import ru.rian.dynamics.retrofit.model.Feed

/**
 * View Model for the [UserActivity]
 */
class FeedViewModel(private val dataSource: FeedDao) : ViewModel() {

    /*fun getFeeds(): Flowable<List<Feed>> {
        return dataSource.getAllFeeds().map { result -> result }
    }*/

    fun getFeedsByType(type: String): Flowable<List<Feed>> {
        return dataSource.getAllFeeds(type).map { result -> result }
    }

    fun insert(feeds: List<Feed>): Completable {
        return Completable.fromAction {
            dataSource.insert(feeds)
        }
    }
}
