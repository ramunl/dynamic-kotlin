package ru.rian.dynamics.ui

import android.support.v4.app.Fragment
import ru.rian.dynamics.retrofit.model.Feed

class UserFeedsFragment: Fragment() {

    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: Feed?)
    }
}