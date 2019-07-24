/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.sleepquality

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import kotlinx.coroutines.*

/**
 * @param sleepNightKey: Provided by the Navigation
 * @param database: The database provided by the Factory
 */
class SleepQualityViewModel(private val sleepNightKey: Long = 0L,
                            val database: SleepDatabaseDao)
    : ViewModel() {
    private val viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val inNavToSleepTracker = MutableLiveData<Boolean?>()
    val navigateToSleepTracker: LiveData<Boolean?>
        get() = inNavToSleepTracker
    fun doneNavigating() {
        inNavToSleepTracker.value = null
    }

    fun onSetSleepQuality(quality: Int){
        uiScope.launch {
            //Switch to the I/O Dispatcher for the database operation
            withContext(Dispatchers.IO) {
                val tonight = database.getNightFromLong(sleepNightKey) ?: return@withContext
                tonight.sleepQuality = quality
                database.updateNight(tonight)
            }
            inNavToSleepTracker.value = true
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}