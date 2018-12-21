package ru.rian.dynamics.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import org.json.JSONObject
import ru.rian.dynamics.InitApp
import ru.rian.dynamics.retrofit.model.HSResult
import ru.rian.dynamics.retrofit.model.Source

object PreferenceHelper {


    fun prefs(): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(InitApp.appContext())

    fun putHStoPrefs(result: HSResult?) {
        val prefs = prefs()
        prefs["createFeed"] = result?.apiRequestArray?.createFeed
        prefs["deleteFeed"] = result?.apiRequestArray?.deleteFeed
        prefs["getArticles"] = result?.apiRequestArray?.getArticles
        prefs["getFeeds"] = result?.apiRequestArray?.getFeeds
        prefs["login"] = result?.apiRequestArray?.login
        prefs["logout"] = result?.apiRequestArray?.logout
        prefs["synchronizeFeeds"] = result?.apiRequestArray?.synchronizeFeeds
        prefs["upsertSubscription"] = result?.apiRequestArray?.upsertSubscription
    }

    fun customPrefs(context: Context, name: String): SharedPreferences =
        context.getSharedPreferences(name, Context.MODE_PRIVATE)

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = this.edit()
        operation(editor)
        editor.apply()
    }

    /**
     * puts a key value pair in shared prefs if doesn't exists, otherwise updates value on given [key]
     */
    operator fun SharedPreferences.set(key: String, value: Any?) {
        when (value) {
            is Source? -> edit { putSource(it, key, value) }
            is String? -> edit { it.putString(key, value) }
            is Int -> edit { it.putInt(key, value) }
            is Boolean -> edit { it.putBoolean(key, value) }
            is Float -> edit { it.putFloat(key, value) }
            is Long -> edit { it.putLong(key, value) }
            else -> throw UnsupportedOperationException("Not yet implemented")
        }
    }

    private fun putSource(prefs: SharedPreferences.Editor, key: String, value: Source?) {
        val obj = JSONObject()
        obj.put("url", value?.url)
        obj.put("method", value?.method)
        prefs.putString(key, obj.toString())
    }

    inline fun getSource(key: String): Source? {
        val prefs = prefs()
        val obj = JSONObject(prefs.getString(key, null))
        val url = obj.getString("url")
        val method = obj.getString("method")
        return Source(url, method)
    }

    /**
     * finds value on given key.
     * [T] is the type of value
     * @param defaultValue optional default value - will take null for strings, false for bool and -1 for numeric values if [defaultValue] is not specified
     */
    inline operator fun <reified T : Any> SharedPreferences.get(key: String, defaultValue: T? = null): T? {
        return when (T::class) {
            Source::class -> getSource(key) as T?
            String::class -> getString(key, defaultValue as? String) as T?
            Int::class -> getInt(key, defaultValue as? Int ?: -1) as T?
            Boolean::class -> getBoolean(key, defaultValue as? Boolean ?: false) as T?
            Float::class -> getFloat(key, defaultValue as? Float ?: -1f) as T?
            Long::class -> getLong(key, defaultValue as? Long ?: -1) as T?
            else -> throw UnsupportedOperationException("Not yet implemented")
        }
    }
}