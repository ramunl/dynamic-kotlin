package ru.rian.dynamics.utils

import android.text.format.Time
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Roman on 14.04.2015.
 */
object RiaDateUtils {

    private lateinit var locale: Locale
    var inputFormat = "kk:mm"
    private var mDateFormat: SimpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private var inputParser = SimpleDateFormat(inputFormat, Locale.getDefault())
    private lateinit var filterTimeFormat: SimpleDateFormat

    //cal.add(Calendar.HOUR_OF_DAY, -1);//<-------- fix Russian GMT +4 to GMT +3
    val todayTime: Long
        get() {
            val timeZone = TimeZone.getDefault()
            val today = Time(timeZone.id)
            today.setToNow()

            val cal = Calendar.getInstance()
            cal.timeInMillis = today.toMillis(true)


            return cal.timeInMillis
        }

    fun setLocale(loc: Locale) {
        locale = loc
        mDateFormat = SimpleDateFormat("HH:mm", locale)
        inputParser = SimpleDateFormat(inputFormat, locale)
        val langStr = LocaleHelper.getLanguage()
        var timeFormat = "dd.MM.yy"
        if (langStr == "zh") {
            timeFormat = "yy.MM.dd"
        }
        filterTimeFormat = SimpleDateFormat(timeFormat, locale)
    }

    fun formatArticleListHeaderTime(date: Int?): String {
        val locale = Locale(LocaleHelper.getLanguage())
        val langStr = LocaleHelper.getLanguage()
        var timeFormat = "dd.MM.yyyy, EEEE"
        if (langStr == "zh") {
            timeFormat = "yyyy.MM.dd, EEEE"
        }
        val sdf = SimpleDateFormat(timeFormat, locale)
        val dFormat = getDateFromTimeStamp(date)
        return sdf.format(dFormat)
    }

    fun formatFilterTime(date: Date): String {
        val langStr = LocaleHelper.getLanguage()
        var timeFormat = "dd.MM.yy"
        if (langStr == "zh") {
            timeFormat = "yy.MM.dd"
        }
        filterTimeFormat = SimpleDateFormat(timeFormat, locale)
        return filterTimeFormat.format(date)
    }

    fun getTimeStamp(aDate: Long): Long {
//String str = Long.toString(output);
        //long timestamp = Long.parseLong(str) * 1000;
        return aDate / 1000L
    }

    fun getDateFromTimeStamp(aDate: Int?): Date {
        val cal = Calendar.getInstance()
        cal.timeInMillis = aDate?.times(1000L) ?: 0
        //cal.add(Calendar.HOUR_OF_DAY, -1);//<-------- fix Russian GMT +4 to GMT +3
        return cal.time
    }

    fun parseStartDate(aTwoDateString: String): Date {
        // "08:00 - 10:00"
        val dashIndex = aTwoDateString.indexOf("-")
        val firstTime = aTwoDateString.substring(0, dashIndex - 1)
        return parseDate(firstTime)
    }

    fun parseStopDate(aTwoDateString: String): Date {
        // "08:00 - 10:00"
        val dashIndex = aTwoDateString.indexOf("-")
        val secondTime = aTwoDateString.substring(dashIndex + 1, aTwoDateString.length).trim { it <= ' ' }
        return parseDate(secondTime)
    }

    private fun parseDate(date: String): Date {
        try {
            return inputParser.parse(date)
        } catch (e: java.text.ParseException) {
            return Date(0)
        }

    }

    fun formatTime(createdAt: Int?): String {
        var date = getDateFromTimeStamp(createdAt!!)
        return mDateFormat.format(date)
    }

    fun formatDateTime(date: Date): String {
        val locale = Locale(LocaleHelper.getLanguage())
        val langStr = LocaleHelper.getLanguage()
        var timeFormat = "dd.MM.yyyy HH:mm"
        if (langStr == "zh") {
            timeFormat = "yyyy.MM.dd HH:mm"
        }
        val sdf = SimpleDateFormat(timeFormat, locale)
        return sdf.format(date)
    }

    fun formatDateTime(createdAt: Int): String {
        val date = RiaDateUtils.getDateFromTimeStamp(createdAt)
        return formatDateTime(date)
    }

    internal fun areTheDatesAtTheSameDay(aPrev: Int?, aCurrent: Int?): Boolean {
        var aPrevDate = getDateFromTimeStamp(aPrev)
        var aCurrentDate = getDateFromTimeStamp(aCurrent)

        val calInst = Calendar.getInstance()
        calInst.time = aPrevDate
        val prevDay = calInst.get(Calendar.DAY_OF_WEEK)
        calInst.time = aCurrentDate
        val currDay = calInst.get(Calendar.DAY_OF_WEEK)
        //there two conditions we must check to understand is it the same day or not:
        //it must be the same day and time difference must be < 24 hours
        return aCurrentDate.time - aPrevDate.time < MILLI_SEC_IN_DAY && currDay == prevDay
    }
}

