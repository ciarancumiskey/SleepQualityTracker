package com.example.android.trackmysleepquality.sleepdetail

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.TypedArrayUtils.getString
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.TextItemViewHolder
import com.example.android.trackmysleepquality.convertDurationToFormatted
import com.example.android.trackmysleepquality.convertNumericQualityToString
import com.example.android.trackmysleepquality.database.SleepNight

class SleepNightAdapter : RecyclerView.Adapter<SleepNightAdapter.ViewHolder>(){
    var sleepData = listOf<SleepNight>()
    set(value) {
        field = value
        /**
         * notifyDataSetChanged() is inefficient (to the point of making the UI lag) for anything
         * more complicated than a TextView.
         */
        notifyDataSetChanged()
    }
    override fun getItemCount() =  sleepData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = sleepData[position]
        val itemRes = holder.itemView.context.resources
        holder.slpLength.text =
                convertDurationToFormatted(item.startTimeMilli, item.endTimeMilli, itemRes)
        holder.slpQuality.text = convertNumericQualityToString(item.sleepQuality, itemRes)
        holder.slpQualImage.setImageResource(when (item.sleepQuality) {
            0 -> R.drawable.ic_sleep_0
            1 -> R.drawable.ic_sleep_1
            2 -> R.drawable.ic_sleep_2
            3 -> R.drawable.ic_sleep_3
            4 -> R.drawable.ic_sleep_4
            5 -> R.drawable.ic_sleep_5
            else -> R.drawable.ic_sleep_active
        })
    }

    /**
     * @param parent: The ViewGroup the View will be added to before getting displayed. In practice,
     * this is always a RecyclerView.
     * @param viewType:
     * @return A ViewHolder for ItemViews
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //attach toParent is false because RecyclerView takes care of attaching views automatically
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_sleep_night, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val slpLength: TextView = itemView.findViewById(R.id.sleep_length)
        val slpQuality: TextView = itemView.findViewById(R.id.sleep_quality)
        val slpQualImage: ImageView = itemView.findViewById(R.id.quality_image)
    }
}