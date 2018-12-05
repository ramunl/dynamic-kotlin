package ru.rian.dynamics.utils

import android.text.format.Time
import ru.rian.dynamics.InitApp
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Created by Roman on 14.04.2015.
 */
object RiaDateUtils {

    private lateinit var locale: Locale
    var inputFormat = "kk:mm"
    private var mDateFormat: DateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
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

    fun FormatHeaderTimeForScheduleOrCompetition(dFormat: Date): String {
        val locale = Locale(LocaleHelper.getLanguage())
        val langStr = LocaleHelper.getLanguage()
        var timeFormat = "dd.MM.yyyy, EEEE"
        if (langStr == "zh") {
            timeFormat = "yyyy.MM.dd, EEEE"
        }
        val sdf = SimpleDateFormat(timeFormat, locale)
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

    fun getDateFromTimeStamp(aDate: Long): Date {
        val cal = Calendar.getInstance()
        cal.timeInMillis = aDate * 1000L
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

    fun formatTime(date: Date): String {
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
        val date = RiaDateUtils.getDateFromTimeStamp(createdAt.toLong())
        return formatDateTime(date)
    }
}

