package ru.rian.dynamics.ui

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_content_common.*
import ru.rian.dynamics.R
import ru.rian.dynamics.retrofit.model.Feed
import ru.rian.dynamics.utils.FragmentId
import ru.rian.dynamics.utils.TYPE_FEED_SUBSCRIPTION_ALL
import ru.rian.dynamics.utils.TYPE_FEED_SUBSCRIPTION_BREAKING

@SuppressLint("Registered")
open class BaseActivity: AppCompatActivity() {

    private fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
        beginTransaction().func().commit()
    }

    protected fun AppCompatActivity.replaceFragment(fragment: Fragment, frameId: FragmentId) {
        supportFragmentManager.inTransaction { replace(R.id.fragmentContainer, fragment, frameId.name) }
    }



    private fun getNotificationRes(feed: Feed?): Int {
        var res = R.drawable.ic_notifications_none_gray
        if (feed?.notification != null) {
            if (TYPE_FEED_SUBSCRIPTION_BREAKING == feed.notification) {
                res = R.drawable.ic_notifications_flash_gray
            } else if (TYPE_FEED_SUBSCRIPTION_ALL == feed.notification) {
                res = R.drawable.ic_notifications_all_gray
            }
        }
        return res
    }

    protected fun setFloatCreateUserFeedButton() {
        var res = R.drawable.plus_fab
        buttonFloatDynamic.setImageDrawable(ContextCompat.getDrawable(this, res))
    }

    protected fun setFloatFlashFloatButton(feed:Feed) {
        var res = getNotificationRes(feed)
        buttonFloatDynamic.setImageDrawable(ContextCompat.getDrawable(this, res))
        /*buttonFloatDynamic.setOnClickListener { v ->
            //    BottomSheetChangePushFeedsDialogFragment.onClickProcess(getFragmentFeedSelected(), null);
            val bottomSheetDialogFragment = BottomSheetChangePushFeedsDialogFragment()
            val bundle = Bundle()
            bundle.putParcelable("feed", getFragmentFeedSelected())
            bottomSheetDialogFragment.setArguments(bundle)
            bottomSheetDialogFragment.show(
                (v.context as AppCompatActivity).supportFragmentManager,
                bottomSheetDialogFragment.getTag()
            )
        }*/
    }
}