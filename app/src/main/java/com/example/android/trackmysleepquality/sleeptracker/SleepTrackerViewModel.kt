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

package com.example.android.trackmysleepquality.sleeptracker

import android.app.Application
import android.content.res.Resources
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatNights
import kotlinx.coroutines.*

/**
 * ViewModel for SleepTrackerFragment.
 * @param database: For access to SleepDatabase
 * @param application: Needed for resource access
 */
class SleepTrackerViewModel(val database: SleepDatabaseDao, application: Application)
    : AndroidViewModel(application) {
    private var viewModelJob = Job()

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    //Coroutines launched in uiScope will run on the main thread
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var tonight = MutableLiveData<SleepNight?>()
    private val nights = database.getAllNights()

    val nightsString = Transformations.map(nights) {nights ->
        formatNights(nights, application.resources)
    }

    init {
        initialiseTonight()
    }

    private fun initialiseTonight() {
        uiScope.launch { tonight.value = getTonightFromDatabase() }
    }

    /**
     * Needs to be suspended so that it can be called from within the coroutine without blocking
     * the UI thread.
     * @return night - The most recently saved SleepNight in the database
     */
    private suspend fun getTonightFromDatabase(): SleepNight? {
        return withContext(Dispatchers.IO) {
            var night = database.getTonight()
            if(night?.endTimeMilli != night?.startTimeMilli){ //Night has been completed
                night = null
            }
            night
        }
    }

    fun onStartTracking(){
        uiScope.launch {
            val newNight = SleepNight()
            insert(newNight)
            tonight.value = getTonightFromDatabase()
        }
    }

    fun onStopTracking(){
        uiScope.launch {
            //return from .launch(), not the lambda used in fragment_sleep_tracker.xml
            val oldNight = tonight.value ?: return@launch
            oldNight.endTimeMilli = System.currentTimeMillis()
            update(oldNight)
        }
    }

    fun onClear(){
        uiScope.launch {
            finalDeletion()
            tonight.value = null
        }
    }

    suspend fun finalDeletion() {
        withContext(Dispatchers.IO) {
            database.deleteAllNights()
        }
    }

    private suspend fun insert(night: SleepNight){
        withContext(Dispatchers.IO) {
            database.insertNight(night)
        }
    }
    private suspend fun update(night: SleepNight){
        withContext(Dispatchers.IO) {
            database.updateNight(night)
        }
    }
}

