package com.example.android.trackmysleepquality.sleepdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.convertDurationToFormatted
import com.example.android.trackmysleepquality.convertNumericQualityToString
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.databinding.ListItemSleepNightBinding
import com.example.android.trackmysleepquality.sleepdetail.SleepNightAdapter.ViewHolder.Companion.from

/**
 * This Adapter provides a list of [SleepNight] entities to a RecyclerView.
 */
class SleepNightAdapter : androidx.recyclerview.widget.ListAdapter<SleepNight,
        SleepNightAdapter.ViewHolder>(SleepNightDiffCallback()){
    /**
     * ListAdapter automatically tracks the data.
     *
     * var sleepData = listOf<SleepNight>()
     * set(value) {
     *     field = value
     *
     *     notifyDataSetChanged() is inefficient (to the point of making the UI lag) for anything
     *     more complicated than a TextView. DiffUtils does it way better.
     *
     *     notifyDataSetChanged()
    }*
    override fun getItemCount() =  sleepData.size*/

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }


    /**
     * @param parent: The ViewGroup the View will be added to before getting displayed. In practice,
     * this is always a RecyclerView.
     * @param viewType:
     * @return A ViewHolder for ItemViews
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return from(parent)
    }

    /**
     * ViewHolder can only be called from within this Adapter class. It holds references to the
     * items in the RecyclerView.
     *
     * @param binding: The binding which will hold the corresponding SleepNight's values
     */
    class ViewHolder private constructor(val binding: ListItemSleepNightBinding):
            RecyclerView.ViewHolder(binding.root){

        /**
         *
         * @param item: The SleepNight object which gets its data rendered
         */
         fun bind(item: SleepNight){
            val itemRes = itemView.context.resources
            //References to binding object fields have been moved below
            binding.sleepLength.text =
                    convertDurationToFormatted(item.startTimeMilli, item.endTimeMilli, itemRes)
            binding.sleepQuality.text = convertNumericQualityToString(item.sleepQuality, itemRes)
            binding.qualityImage.setImageResource(when (item.sleepQuality) {
                0 -> R.drawable.ic_sleep_0
                1 -> R.drawable.ic_sleep_1
                2 -> R.drawable.ic_sleep_2
                3 -> R.drawable.ic_sleep_3
                4 -> R.drawable.ic_sleep_4
                5 -> R.drawable.ic_sleep_5
                else -> R.drawable.ic_sleep_active
            })
        }
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val binding = ListItemSleepNightBinding.inflate(LayoutInflater.from(parent.context),
                        parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class SleepNightDiffCallback : DiffUtil.ItemCallback<SleepNight>(){
    override fun areContentsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
        return oldItem.nightId == newItem.nightId
    }

    override fun areItemsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
        //Because SleepNight is a data class, the == operator can be used
        return oldItem == newItem
    }
}