package com.example.android.trackmysleepquality.sleeptracker

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.databinding.ListItemSleepNightBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ClassCastException

private const val ITEM_VIEW_TYPE_HEADER = 0
private const val ITEM_VIEW_TYPE_DATA = 1
/**
 * This Adapter provides a list of [SleepNight] entities to a RecyclerView.
 */
class SleepNightAdapter(val clickListener: SleepNightClickListener) :
        ListAdapter<GridItemWrapper, RecyclerView.ViewHolder>(SleepNightDiffCallback()){
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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, pos: Int) {
        when (holder) {
            is ViewHolder -> {
                val nightItem = getItem(pos) as GridItemWrapper.WrappedSleepNightItem
                holder.bind(clickListener, nightItem.sleepNight)
            }
        }
    }


    /**
     * @param parent: The ViewGroup the View will be added to before getting displayed. In practice,
     * this is always a RecyclerView.
     * @param viewType:
     * @return A ViewHolder for ItemViews
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            ITEM_VIEW_TYPE_HEADER -> TextViewHolder.from(parent)
            ITEM_VIEW_TYPE_DATA -> ViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType ${viewType}")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)){
            is GridItemWrapper.Header -> ITEM_VIEW_TYPE_HEADER
            is GridItemWrapper.WrappedSleepNightItem -> ITEM_VIEW_TYPE_DATA
        }
    }

    private val adapterScope = CoroutineScope(Dispatchers.Default) // Doesn't block the UI thread
    fun addHeaderAndSubmitList(list: List<SleepNight>?){
        adapterScope.launch {
            val items = when (list) {
                null -> listOf(GridItemWrapper.Header)
                else -> listOf(GridItemWrapper.Header) + list.map { GridItemWrapper.WrappedSleepNightItem(it) }
            }
            withContext(Dispatchers.Main){
                submitList(items)
            }
        }
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
        fun bind(clickListener: SleepNightClickListener, item: SleepNight){
            binding.sleep = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
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

class SleepNightDiffCallback : DiffUtil.ItemCallback<GridItemWrapper>(){
    override fun areContentsTheSame(oldItem: GridItemWrapper, newItem: GridItemWrapper): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areItemsTheSame(oldItem: GridItemWrapper, newItem: GridItemWrapper): Boolean {
        //Because SleepNight is a data class, the == operator can be used
        return oldItem == newItem
    }
}

/**
 * ClickListener for SleepNights in the List
 * @param clickListener
 */
class SleepNightClickListener(val clickListener: (sleepId: Long) -> Unit){
    fun onClick(night : SleepNight) {
        Log.d("SleepNightClickListener", "Click")
        clickListener(night.nightId)
    }
}

class TextViewHolder(view: View): RecyclerView.ViewHolder(view) {
    companion object {
        fun from(parent: ViewGroup): TextViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.header, parent, false)
            return TextViewHolder(view)
        }
    }
}

/**
 * Provides a wrapper class for ListItems. Not really useful for RecyclerViews with 1 or 2 item
 * types, this is just to serve as an example.
 *
 * A sealed class can't be overridden elsewhere in this code.
 */
sealed class GridItemWrapper
{
    /**
     * Wrapper class for a SleepNight
     * @param sleepNight: The specific instance of SleepNight being wrapped
     * @return a GridItemWrapper wrapper
     */
    data class WrappedSleepNightItem(val sleepNight: SleepNight): GridItemWrapper() {
        override val id = sleepNight.nightId
    }

    /**
     * Wrapper class for the RecyclerView's Header, of which there's just one instance
     */
    object Header: GridItemWrapper() {
        override val id = Long.MIN_VALUE
    }
    abstract val id: Long
}