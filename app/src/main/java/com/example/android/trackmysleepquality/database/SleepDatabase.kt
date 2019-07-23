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

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * This class is abstract because Room handles its implementation already.
 */
@Database(entities = [SleepNight::class], version = 1, exportSchema = false)
abstract class SleepDatabase : RoomDatabase() {
    abstract val sleepDatabaseDao: SleepDatabaseDao

    /**
     * Allows clients to access the methods for creating/retrieving the database without
     * instantiating the class.
     */
    companion object{
        /**
         * Its value will never be cached, all read/write operations will be done to main memory,
         * and the value stays constant across all threads.
         */
        @Volatile
        private var INSTANCE: SleepDatabase? = null //Keeps a reference to the database once that's created

        /**
         * Returns a reference to the database
         */
        fun getInstance(context: Context) : SleepDatabase {
            //Prevents conflicts between threads for database values
            synchronized(this){
                var instance = INSTANCE
                if(instance == null){
                    instance = Room.databaseBuilder(
                            context.applicationContext,
                            SleepDatabase::class.java,
                            "sleep_history_database"
                    ).fallbackToDestructiveMigration() //It's not like there's any users who can get annoyed with losing their sleep data...yet
                            .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}