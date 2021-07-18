package com.example.dashcode.util

import android.content.Context
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.dashcode.R
import com.example.dashcode.domain.MarkerData
import com.example.dashcode.domain.PlatformUser
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.MPPointF
import java.time.LocalDate
import java.time.ZoneId
import java.util.TimeZone
import java.util.Date

class TimeFormatter: ValueFormatter() {
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        val date = Date(value.toLong().times(1000))
        val sdf = java.text.SimpleDateFormat("d MMM yyyy")
        sdf.timeZone = TimeZone.getTimeZone("GMT-5")
        return sdf.format(date)
    }
}

class EpochDayFormatter: ValueFormatter() {
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        val localDate = LocalDate.ofEpochDay(value.toLong())
        val defaultZoneId = ZoneId.systemDefault()
        val date = Date.from(localDate.atStartOfDay(defaultZoneId).toInstant())
        val sdf = java.text.SimpleDateFormat("d MMM yyyy")
        sdf.timeZone = TimeZone.getTimeZone("GMT-5")
        return sdf.format(date)
    }
}

class CustomMarkerView(context: Context?, layoutResource: Int) : MarkerView(context, layoutResource) {

    val tvContent = findViewById<TextView>(R.id.marker_contest_name)

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        val markerData = e?.data as MarkerData
        val str = markerData.contestName + "\n" + markerData.rating + " (" + markerData.ratingChange + ")"
        tvContent.text = str
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF((-width / 2).toFloat(), height.times(-1).toFloat())
    }
}

fun PlatformUser.loadChartData(context: Context) : LineData {
    val entries =
        contests.map {
            Entry(
                it.updateTime.toFloat(),
                it.newRating.toFloat(),
                MarkerData(it.name, it.rank, it.newRating, it.ratingChange)
            )
        }

    val lineDataSet = LineDataSet(entries, "ratings")
        .apply {
            axisDependency = YAxis.AxisDependency.LEFT
            setDrawValues(false)
            setDrawCircles(false)
            setDrawHighlightIndicators(false)
            setDrawFilled(true)

            setDrawHorizontalHighlightIndicator(false)
            setDrawVerticalHighlightIndicator(true)
            enableDashedHighlightLine(10f, 5f, 0f)

            color = ContextCompat.getColor(
                context,
                getAccentColor(
                    this@loadChartData.platform,
                    this@loadChartData.contests.last().newRating
                )
            )
            fillDrawable = ContextCompat.getDrawable(
                context,
                getFillDrawable(
                    this@loadChartData.platform,
                    this@loadChartData.contests.last().newRating
                )
            )

            lineWidth = 2f
            valueTextSize = 9f
    }
    val dataSet = mutableListOf<ILineDataSet>()
    dataSet.add(lineDataSet)
    return LineData(dataSet)
}