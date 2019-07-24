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

package com.example.android.trackmysleepquality.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SleepDatabaseDao {
    /**
     * @param newNight: The SleepNight to be inserted
     */
    @Insert
    fun insertNight(newNight: SleepNight) //Room will take care of the "Create" SQLite query

    /**
     * Updates the inputted SleepNight
     *
     * @param night: The SleepNight to be updated
     */
    @Update
    fun updateNight(night: SleepNight)

    //Clears the table
    @Query("DELETE FROM daily_sleep_quality_table")
    fun deleteAllNights()

    /**
     * @param keyTimeMillis: The millisecond value that is used to derive any corresponding
     * SleepNight
     */
    @Query("SELECT * from daily_sleep_quality_table WHERE nightId = :keyTimeMillis")
    fun getNightFromLong(keyTimeMillis: Long): SleepNight?

    //Returns every saved night, starting from the most recent
    @Query("SELECT * from daily_sleep_quality_table ORDER BY nightId DESC")
    fun getAllNights(): LiveData<List<SleepNight>>

    /**
     * @return: The most recently saved SleepNight entity
     */
    @Query("SELECT * FROM daily_sleep_quality_table ORDER BY nightId DESC LIMIT 1")
    fun getTonight(): SleepNight?
}
