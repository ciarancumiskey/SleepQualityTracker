package com.example.android.trackmysleepquality.sleepdetail

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.TypedArrayUtils.getString
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.TextItemViewHolder
import com.example.android.trackmysleepquality.database.SleepNight

class SleepNightAdapter : RecyclerView.Adapter<TextItemViewHolder>(){
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

    override fun onBindViewHolder(holder: TextItemViewHolder, position: Int) {
        val item = sleepData[position]
        holder.textView.text = item.sleepQuality.toString()
        when(item.sleepQuality) {
            //Quick fix for "-1" showing while the user's adding a new SleepNight
            -1 -> {
                //Stops this text turning red if the user last entered 0/1
                holder.textView.setTextColor(Color.BLACK)
                holder.textView.setText(R.string.pending)
            }
            0, 1 -> holder.textView.setTextColor(Color.RED)
            in 2..5 -> holder.textView.setTextColor(Color.BLACK)
            else -> holder.textView.setText(R.string.quality_too_high)
        }
    }

    /**
     * @param parent: The ViewGroup the View will be added to before getting displayed. In practice,
     * this is always a RecyclerView.
     * @param viewType:
     * @return A ViewHolder for TextItems
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        //attach toParent is false because RecyclerView takes care of attaching views automatically
        val view = layoutInflater.inflate(R.layout.text_item_view, parent, false)
                as TextView
        return TextItemViewHolder(view)
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val slpLength: TextView = itemView.findViewById(R.id.sleep_length)
        val slpQuality: TextView = itemView.findViewById(R.id.sleep_quality)
    }
}