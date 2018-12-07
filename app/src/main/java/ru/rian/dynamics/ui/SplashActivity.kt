package ru.rian.dynamics.ui

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_splash.*
import ru.rian.dynamics.InitApp
import ru.rian.dynamics.R
import ru.rian.dynamics.SchedulerProvider
import ru.rian.dynamics.di.component.DaggerActivityComponent
import ru.rian.dynamics.di.model.ActivityModule
import java.util.logging.Logger
import javax.inject.Inject

class SplashActivity : AppCompatActivity() {


    @Inject
    lateinit var mainViewModel: MainViewModel
    private lateinit var compositeDisposable: CompositeDisposable
    private var disposable: Disposable? = null

    companion object {
        val Log = Logger.getLogger(SplashActivity::class.java.name)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        compositeDisposable = CompositeDisposable()
        val activityComponent = DaggerActivityComponent
            .builder()
            .appComponent(InitApp.get(this).component())
            .activityModule(ActivityModule(SchedulerProvider()))
            .build()
        activityComponent.inject(this)

        if (mainViewModel.loading == true) {
            progressView.visibility = View.VISIBLE
        }
        requestHS()

    }

    private fun requestHS() {
        disposable = mainViewModel.provideHS()
            ?.subscribe({ result ->
                val res = result
                progressView.visibility = View.GONE
                mainViewModel.setIsLoading(false)
            }, { e ->
                e.printStackTrace()
                progressView.visibility = View.GONE
                mainViewModel.setIsLoading(false)
                Snackbar.make(rootLayout, getString(R.string.connection_error_title), Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.try_again) { requestHS() }
                    .setActionTextColor(resources.getColor(R.color.action_color))
                    .show()
            })
    }

    override fun onResume() {
        super.onResume()
        if (disposable != null) {
            bind()
        }
    }

    private fun bind() {
        compositeDisposable.add(disposable!!)
    }

    private fun unbind() {
        compositeDisposable.clear()
    }

    override fun onPause() {
        super.onPause()
        if (compositeDisposable.size() > 0) {
            unbind()
        }
    }

}
