package com.example.dashcode.ui.main

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dashcode.R
import com.example.dashcode.databinding.ChartItemBinding
import com.example.dashcode.domain.PlatformUser
import com.example.dashcode.util.loadChartData
import com.example.dashcode.util.CustomMarkerView
import com.example.dashcode.util.EpochDayFormatter
import com.example.dashcode.util.TimeFormatter
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ClassCastException

private const val ITEM_VIEW_TYPE_CODEFORCES = 0

class ChartAdapter: ListAdapter<DataItem, RecyclerView.ViewHolder>(DiffCallback()) {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is ViewHolder -> {
                val chartItem = getItem(position) as DataItem.AccountChartItem
                holder.bind(chartItem.platformUser)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            ITEM_VIEW_TYPE_CODEFORCES -> ViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)) {
            is DataItem.AccountChartItem -> ITEM_VIEW_TYPE_CODEFORCES
        }
    }

    class ViewHolder private constructor(private val binding: ChartItemBinding) : RecyclerView.ViewHolder(binding.root), OnChartValueSelectedListener {

        fun bind(item: PlatformUser) {

            binding.platformUser = item

            val marker = CustomMarkerView(binding.ratingChart.context, R.layout.chart_marker_view)

            val chart = binding.ratingChart
                .apply {
                    setBackgroundColor(Color.WHITE)
                    animateX(1000)
                    setDrawGridBackground(false)
                    setDrawBorders(false)

                    setMarker(marker)

                    setOnChartValueSelectedListener(this@ViewHolder)

                    xAxis.apply {
                        setDrawGridLines(false)
                        setDrawAxisLine(false)
                        labelCount = 4
                        isDragEnabled = true
                        position = XAxis.XAxisPosition.BOTTOM
                        valueFormatter = when(item.platform) {
                            "codeforces.com" -> TimeFormatter()
                            else -> EpochDayFormatter()
                        }
                    }


                    axisLeft.apply {
                        setDrawAxisLine(false)
                        setDrawZeroLine(false)
                    }

                    axisRight.isEnabled = false

                    description = Description().apply { text = "" }

                    legend.isEnabled = false

                    isHighlightPerDragEnabled = true
                    isHighlightPerTapEnabled = true

                    data = item.loadChartData(binding.ratingChart.context)
                    invalidate()
            }

            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup) : ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ChartItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }


        //TODO: Do something else instead of logging?
        override fun onValueSelected(e: Entry?, h: Highlight?) {
            Log.i("ChartAdapter", e.toString())
        }

        override fun onNothingSelected() {
            Log.i("ChartAdapter", "Nothing Selected")
        }
    }

    private val adapterScope = CoroutineScope(Dispatchers.Default)
    fun addItemsAndSubmitList(list: List<PlatformUser>) {
        adapterScope.launch {

            val items = list.map { DataItem.AccountChartItem(it) }

            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }
}

class DiffCallback : DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }
}

sealed class DataItem {
    abstract val id: String

    data class AccountChartItem(val platformUser: PlatformUser) : DataItem() {
        override val id = "${platformUser.platform}/${platformUser.handle}"
    }
}
