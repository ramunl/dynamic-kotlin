package ru.rian.dynamics.utils

/**
 * Created by Roman on 11/5/2015.
 */

import android.content.res.Configuration
import android.os.Build
import android.preference.PreferenceManager
import ru.rian.dynamics.InitApp
import ru.rian.dynamics.R
import java.util.*

/**
 * This class is used to change your application locale and persist this change for the next time
 * that your app is going to be used.
 *
 *
 * You can also change the locale of your application on the fly by using the setLocale method.
 *
 *
 * Created by gunhansancar on 07/10/15.
 */
object LocaleHelper {

    private val SELECTED_LANGUAGE = "Locale.Helper.Selected.Language"

    fun onCreate() {
        val lang = getPersistedData(Locale.getDefault().language)
        setLocale(lang)
    }

    fun onCreate(defaultLanguage: String) {
        val lang = getPersistedData(defaultLanguage)
        setLocale(lang)
    }

    fun getLanguage(): String? {
        return getPersistedData(Locale.getDefault().language)
    }

    fun getLanguageStr(): String {
        var id = -1
        val langStr = LocaleHelper.getLanguage()
        if (langStr == "ru") {
            id = R.string.lang_ru
        } else if (langStr == "en") {
            id = R.string.lang_en
        } else if (langStr == "es") {
            id = R.string.lang_es
        } else if (langStr == "ar") {
            id = R.string.lang_ar
        } else if (langStr == "zh") {
            id = R.string.lang_zh
        }
        if (id == -1) {
            id = R.string.lang_ru
        }
        return InitApp.applicationContext().getString(id)
    }

    fun setLocale(language: String?) {
        persist(language)
        updateResources(language)
    }

    private fun getPersistedData(defaultLanguage: String): String? {
        val preferences = PreferenceManager.getDefaultSharedPreferences(InitApp.applicationContext())
        return preferences.getString(SELECTED_LANGUAGE, defaultLanguage)
    }

    private fun persist(language: String?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(InitApp.applicationContext())
        val editor = preferences.edit()

        editor.putString(SELECTED_LANGUAGE, language)
        editor.apply()
    }

    fun getStringResourceByName(resId: Int): String {
        var resourceName = InitApp.applicationContext().resources.getResourceName(resId)
        resourceName = resourceName.substring(resourceName.indexOf('/') + 1)
        return resourceName
    }

    private fun updateResources(language: String?) {
        val locale = Locale(language)
        RiaDateUtils.setLocale(locale)
        Locale.setDefault(locale)

        val resources = InitApp.applicationContext().resources

        val configuration = resources.configuration
        configuration.locale = locale
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale)
        }
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    fun getString(resourceId: Int): String {
        val ctx = InitApp.applicationContext()
        val lang = LocaleHelper.getLanguage()
        val locale = Locale(lang)
        return getLocaleStringResource(locale, resourceId)
    }

    fun getLocaleStringResource(requestedLocale: Locale, resourceId: Int): String {
        val result: String
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) { // use latest api
            val config = Configuration(InitApp.applicationContext().resources.configuration)
            config.setLocale(requestedLocale)
            result = InitApp.applicationContext().createConfigurationContext(config).getText(resourceId).toString()
        } else { // support older android versions
            val resources = InitApp.applicationContext().resources
            val conf = resources.configuration
            val savedLocale = conf.locale
            conf.locale = requestedLocale
            resources.updateConfiguration(conf, null)

            // retrieve resources from desired locale
            result = resources.getString(resourceId)

            // restore original locale
            conf.locale = savedLocale
            resources.updateConfiguration(conf, null)
        }

        return result
    }
}