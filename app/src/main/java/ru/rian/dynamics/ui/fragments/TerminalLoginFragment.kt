package ru.rian.dynamics.ui.fragments

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.EditText
import kotlinx.android.synthetic.main.fragment_terminal_login.view.*
import ru.rian.dynamics.BuildConfig
import ru.rian.dynamics.InitApp
import ru.rian.dynamics.R
import ru.rian.dynamics.SchedulerProvider
import ru.rian.dynamics.di.component.DaggerTerminalLoginFragmentComponent
import ru.rian.dynamics.di.model.TerminalViewModel
import ru.rian.dynamics.di.module.ActivityModule
import ru.rian.dynamics.retrofit.model.Source
import ru.rian.dynamics.ui.helpers.SnackContainerProvider
import ru.rian.dynamics.ui.helpers.addMainMenuItem
import ru.rian.dynamics.utils.KLoggerWrap
import ru.rian.dynamics.utils.PreferenceHelper.getUserName
import ru.rian.dynamics.utils.PreferenceHelper.saveTokent
import ru.rian.dynamics.utils.PreferenceHelper.saveUserName
import ru.rian.dynamics.utils.hideKeyboard
import javax.inject.Inject


class TerminalLoginFragment : BaseFragment() {

    companion object {
        const val ARG_FEED_SOURCE = "feed_url"
        @JvmStatic
        fun newInstance(feedSource: Source) = TerminalLoginFragment().apply {
            arguments = Bundle().apply {
                putSerializable(TerminalLoginFragment.ARG_FEED_SOURCE, feedSource)
            }
        }

        var logger = KLoggerWrap(UserFeedsFragment::class)
    }

    @Inject
    lateinit var terminalViewModel: TerminalViewModel

    var loginButtonVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fragmentComponent = DaggerTerminalLoginFragmentComponent
            .builder()
            .appComponent(InitApp.get(context!!).component())
            .activityModule(ActivityModule(context as FragmentActivity, SchedulerProvider()))
            .build()
        fragmentComponent.inject(this)
        if (savedInstanceState != null) {
            loginButtonVisible = savedInstanceState.getBoolean("loginButtonVisible", false)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("loginButtonVisible", loginButtonVisible)
        //searchView?.let { outState.putString("query", it.query.toString()) }
        //outState.putParcelableArrayList("dataList", articlesAdapter.dataList)
    }

    lateinit var usernameEditText: EditText
    lateinit var passwordEditText: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_terminal_login, container, false)
        usernameEditText = view.usernameEditText
        passwordEditText = view.passwordEditText

        usernameEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkLoginButtonVisibility()
                saveUserName(p0.toString())
            }
        })

        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkLoginButtonVisibility()
            }
        })

        usernameEditText.setText(getUserName())

        return view
    }

    private val minLengthToProcess = 1

    fun checkLoginButtonVisibility() {
        if (showLoginButton()) {
            if (!loginButtonVisible) {
                updateLoginButtonState()
                loginButtonVisible = true
            }
        } else {
            if (loginButtonVisible) {
                updateLoginButtonState()
                loginButtonVisible = false
            }
        }
    }

    private fun showLoginButton() =
        usernameEditText.length() > minLengthToProcess && passwordEditText.length() > minLengthToProcess

    private fun updateLoginButtonState() {
        (context as Activity).invalidateOptionsMenu()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        setupToolBarTitle(getString(R.string.terminal_title))
        if (loginButtonVisible) {
            addMainMenuItem(
                menu,
                R.drawable.ic_check_white,
                R.id.menu_action_toolbar_login
            )
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_action_toolbar_login -> {
                login()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun login() {
        logger.kDebug("login")
        hideKeyboard(context!!)
        terminalViewModel.apply {
            var disposable = provideTerminalLogin(usernameEditText.text.toString(), passwordEditText.text.toString())
                ?.subscribe(
                    { result ->
                        logger.kDebug(result.toString())
                        result?.apply {
                            if (token.isNullOrEmpty() ) {
                                showMessage(if(BuildConfig.DEBUG) message!! else getString(R.string.login_passsword_error))
                            } else {
                                token?.let { saveTokent(it) }
                            }
                        }
                    }, { e -> showError(e, SnackContainerProvider.ActionToInvoke(::login)) })
            disposable?.let { compositeDisposable.add(it) }
        }
    }

    override fun onStop() {
        super.onStop()
        hideKeyboard(context!!)
    }
}