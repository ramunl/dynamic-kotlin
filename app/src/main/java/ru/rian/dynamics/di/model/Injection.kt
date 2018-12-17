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

import android.content.Context
import ru.rian.dynamics.db.DynamicsDataBase
import ru.rian.dynamics.db.FeedDao
import ru.rian.dynamics.db.ViewModelFactory


/**
 * Enables injection of data sources.
 */
object Injection {

    fun provideFeedDataSource(context: Context): FeedDao {
        val database = DynamicsDataBase.getInstance(context)
        return database.feedDao()
    }

    fun provideViewModelFactory(context: Context): ViewModelFactory {
        val dataSource = provideFeedDataSource(context)
        return ViewModelFactory(dataSource)
    }
}
