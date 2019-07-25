package com.example.android.trackmysleepquality.sleepdetail

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.TextItemViewHolder
import com.example.android.trackmysleepquality.database.SleepNight

class SleepNightAdapter : RecyclerView.Adapter<TextItemViewHolder>(){
    var sleepData = listOf<SleepNight>()
    override fun getItemCount() =  sleepData.size

    override fun onBindViewHolder(holder: TextItemViewHolder, position: Int) {
        holder.textView.text = sleepData[position].sleepQuality.toString()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextItemViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}