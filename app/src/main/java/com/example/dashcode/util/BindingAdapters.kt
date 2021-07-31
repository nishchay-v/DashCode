package com.example.dashcode.util

import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.lifecycle.LiveData
import com.example.dashcode.domain.PlatformUser

@BindingAdapter("ratingText")
fun goneIfNotNull(view: TextView, it: LiveData<Int?>) {
    view.text = if (it.value != null) it.value.toString() else "-1000"
}

@BindingAdapter("allUsers")
fun getAllUsers(view: TextView, users: LiveData<String>) {
    view.text = users.value
}

@BindingAdapter("contestDuration")
fun durationHours(view: TextView, seconds: Int) {
    val hours = "${seconds / 3600} hours"
    view.text = hours
}

@BindingAdapter("platformRank")
fun getRank(view: TextView, user: PlatformUser) {
    view.text = getRank(user.platform, user.currentRating)
}

@BindingAdapter("dateText")
fun getDate(view: TextView, timeStamp: String) {
    view.text = timeStampToDate(timeStamp)
}

@BindingAdapter("timeText")
fun getTime(view: TextView, timeStamp: String) {
    view.text = timeStampToTime(timeStamp)
}

@BindingAdapter("platformIcon")
fun getIcon(view: ImageView, platform: String) {
    view.setImageResource(getContestIcon(platform))
}

@BindingAdapter("intAsText")
fun getRatingText(view: TextView, num: Int) {
    view.text = num.toString()
}

@BindingAdapter("platformName")
fun getPlatform(view: TextView, platform: String) {
    view.text = getPlatformName(platform)
}

@BindingAdapter("ratingChangeText")
fun getRatingChangeText(view: TextView, change:String) {
    view.text = "($change)"
    view.setTextColor(getChangeColor(change))
}

@InverseBindingAdapter(attribute = "selectedPosition")
fun getSelectedPosition(spinner: Spinner): String {
    return spinner.selectedItem.toString()
}