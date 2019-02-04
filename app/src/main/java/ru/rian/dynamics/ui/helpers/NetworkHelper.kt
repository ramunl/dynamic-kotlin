package ru.rian.dynamics.ui.helpers

import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import okhttp3.internal.Util
import ru.rian.dynamics.BuildConfig
import ru.rian.dynamics.InitApp
import ru.rian.dynamics.utils.DEFAULT_USER_AGENT


object NetworkHelper {

    private var sVersionName: String? = null

    fun getCheckedUserAgentValueImpl(): String {
        if (TextUtils.isEmpty(userAgent())) {
            return DEFAULT_USER_AGENT
        }
        val strBld = StringBuilder()
        var i = 0
        val length = userAgent().length
        while (i < length) {
            val c = userAgent()[i]
            if(!(c <= '\u001f' && c != '\t' || c >= '\u007f')) {
                strBld.append(c)
            }
            i++
        }
        return strBld.toString()
    }

    fun userAgent(): String {
        return String.format(
            "%s/%s (%s; %s; %s; %s; %s) %s",
            USER_AGENT.APP_NAME,
            appVersionName,
            "android",
            USER_AGENT.SECURITY_PRESENT,
            Build.VERSION.RELEASE,
            USER_AGENT.LOCALIZATION,
            Build.MANUFACTURER + " " + Build.MODEL,
            userId()
        )
    }

    private fun userId(): String {
        return lazy {
            Settings.Secure.getString(
                InitApp.appContext().contentResolver,
                Settings.Secure.ANDROID_ID
            )
        }.value
    }


    private val appVersionName: String?
        get() {
            if (sVersionName == null) {
                val packageName = InitApp.appContext().getPackageName()
                try {
                    val info = InitApp.appContext().getPackageManager().getPackageInfo(packageName, 0)
                    sVersionName = info.versionName
                } catch (e: PackageManager.NameNotFoundException) {
                    if (BuildConfig.DEBUG) {
                        e.printStackTrace()
                    }
                    return "N/A"
                }
            }
            return sVersionName
        }


    fun isInternetAvailable(pContext: Context?): Boolean {
        if (pContext == null) {
            return false
        }
        val cm = pContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }

    object USER_AGENT {
        val SECURITY_NONE = "N" //http
        val SECURITY_PRESENT = "U" //https
        val APP_NAME = "dynamics"
        val LOCALIZATION = "ru"
    }
}
