package ru.rian.dynamics

import android.app.Application
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatDelegate
import com.onesignal.OneSignal
import org.json.JSONException
import ru.rian.dynamics.di.component.AppComponent
import ru.rian.dynamics.di.component.DaggerAppComponent
import ru.rian.dynamics.di.model.AppModule
import ru.rian.dynamics.di.model.NetModule
import ru.rian.dynamics.ui.MainActivity


class InitApp : Application() {

    init {
        instance = this
    }

    companion object {
        private var instance: InitApp? = null

        fun appContext(): Context {
            return instance!!.applicationContext
        }

        fun get(context: Context): InitApp {
            return context.applicationContext as InitApp
        }
    }


    @Override
    override fun onCreate() {
        super.onCreate()
        HandroidLoggerAdapter.DEBUG = BuildConfig.DEBUG
       // HandroidLoggerAdapter.APP_NAME = getString(R.string.app_name)

        OneSignal.startInit(this).autoPromptLocation(false)
            .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
            .setNotificationOpenedHandler { result ->
                val additionalData = result.notification.payload.additionalData
                val content = result.notification.payload.title
                if (additionalData != null) {
                    try {
                        additionalData.put("title", content)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                }
                val articleStr = additionalData!!.toString()
                val resultIntent = Intent(applicationContext, MainActivity::class.java)
                resultIntent.putExtra("article", articleStr)
                //resultIntent.putExtras(intent.getExtras());
                resultIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                applicationContext.startActivity(resultIntent)
            }
            .disableGmsMissingPrompt(true)
            .init()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    fun component(): AppComponent {
        return DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .netModule(NetModule())
            .build()
    }
}