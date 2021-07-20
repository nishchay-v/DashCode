package com.example.dashcode.util

import android.graphics.Color
import androidx.core.content.ContextCompat
import com.example.dashcode.R
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

fun timeToSeconds(timeStamp: String): Int {
    val date = LocalDate.parse(timeStamp.subSequence(0, 10))
    return date.toEpochDay().toInt()
}

fun timeStampToDate(timeStamp: String): String {
    val localDate = LocalDate.parse(timeStamp.subSequence(0, 10))
    val defaultZoneId = ZoneId.systemDefault()
    val date = Date.from(localDate.atStartOfDay(defaultZoneId).toInstant())
    val sdf = java.text.SimpleDateFormat("d MMMM")
    sdf.timeZone = TimeZone.getTimeZone("GMT-5")
    return sdf.format(date)
}

fun timeStampToTime(timeStamp: String): String = timeStamp.subSequence(11, 16) as String

fun getAccentColor(platform: String, rating: Int = -1): Int {
    return when(platform) {
        "codeforces.com" -> when(rating) {
            in 0..999 -> R.color.cfNewbie
            in 1000..1199 -> R.color.cfPupil
            in 1200..1399 -> R.color.cfApprentice
            in 1400..1599 -> R.color.cfSpecialist
            in 1600..1799 -> R.color.cfExpert
            in 1800..1999 -> R.color.cfCandidateMaster
            in 2000..2199 -> R.color.cfMaster
            in 2200..2399 -> R.color.cfInternationalMaster
            in 2400..2699 -> R.color.cfGrandmaster
            in 2700..2999 -> R.color.cfInternationalGrandmaster
            else -> R.color.cfLegendary
        }
        "codechef.com" -> when(rating) {
            in 0..1399 -> R.color.cc1Star
            in 1400..1599 -> R.color.cc2Star
            in 1600..1799 -> R.color.cc3Star
            in 1800..1999 -> R.color.cc4Star
            in 2000..2199 -> R.color.cc5Star
            in 2200..2499 -> R.color.cc6Star
            else -> R.color.cc7Star
        }
        "leetcode.com" -> R.color.cfMaster
        else -> R.color.cfLegendary
    }
}

fun getFillDrawable(platform: String, rating: Int = -1): Int {
    return when(platform) {
        "codeforces.com" -> when(rating) {
            in 0..999 -> R.drawable.fade_cf_newbie
            in 1000..1199 -> R.drawable.fade_cf_pupil
            in 1200..1399 -> R.drawable.fade_cf_apprentice
            in 1400..1599 -> R.drawable.fade_cf_specialist
            in 1600..1799 -> R.drawable.fade_cf_specialist
            in 1800..1999 -> R.drawable.fade_cf_candidate_master
            in 2000..2199 -> R.drawable.fade_cf_master
            in 2200..2399 -> R.drawable.fade_cf_master
            in 2400..2699 -> R.drawable.fade_cf_grandmaster
            in 2700..2999 -> R.drawable.fade_cf_international_grandmaster
            else -> R.drawable.fade_cf_legendary
        }
        "codechef.com" -> when(rating) {
            in 0..1399 -> R.drawable.fade_cc_1star
            in 1400..1599 -> R.drawable.fade_cc_2star
            in 1600..1799 -> R.drawable.fade_cc_3star
            in 1800..1999 -> R.drawable.fade_cc_4star
            in 2000..2199 -> R.drawable.fade_cc_5star
            in 2200..2499 -> R.drawable.fade_cc_6star
            else -> R.drawable.fade_cc_7star
        }
        "leetcode.com" -> R.drawable.fade_cf_master
        else -> R.drawable.fade_cf_legendary
    }
}

//fun getTextAccentColor(platform: String, rating: Int = -1): Int = ContextCompat.getColor(req, )

fun getRank(platform: String, rating: Int = -1): String{
    return when(platform) {
        "codeforces.com" -> when(rating) {
            in 0..999 -> "Newbie"
            in 1000..1199 -> "Pupil"
            in 1200..1399 -> "Apprentice"
            in 1400..1599 -> "Specialist"
            in 1600..1799 -> "Expert"
            in 1800..1999 -> "Candidate Master"
            in 2000..2199 -> "Master"
            in 2200..2399 -> "International Master"
            in 2400..2699 -> "Grandmaster"
            in 2700..2999 ->"International Grandmaster"
            else -> "Legendary"
        }
        "codechef.com" -> when(rating) {
            in 0..1399 -> "1★"
            in 1400..1599 -> "2★"
            in 1600..1799 -> "3★"
            in 1800..1999 -> "4★"
            in 2000..2199 -> "5★"
            in 2200..2499 -> "6★"
            else -> "7★"
        }
        else -> ""
    }}

fun getContestIcon(platform: String): Int {
    return when(platform) {
        "codeforces.com" -> R.drawable.ic_codeforces_colored
        "codechef.com" -> R.drawable.ic_codechef_logo
        "leetcode.com" -> R.drawable.ic_leetcode_logo
        "atcoder.jp" -> R.drawable.ic_atcoder_logo
        "topcoder.com" -> R.drawable.ic_topcoder_logo
        "codingcompetitions.withgoogle.com" -> R.drawable.ic_google_logo
        "hackerearth.com" -> R.drawable.ic_hackerearth_logo
        "kaggle.com" -> R.drawable.ic_kaggle_logo
        "spoj.com" -> R.drawable.ic_spoj_logo
        else -> R.drawable.ic_baseline_code_24
    }
}

fun getPlatformName(platform: String): String {
    return when(platform) {
        "codeforces.com" -> "CodeForces"
        "codechef.com" -> "CodeChef"
        "leetcode.com" -> "LeetCode"
        "atcoder.jp" -> "AtCoder"
        else -> "Unknown Platform"
    }
}

fun getChangeColor(change: String): Int {
    return when(change[0]) {
        '+' -> Color.GREEN
        '-' -> Color.RED
        else -> Color.GRAY
    }
}
