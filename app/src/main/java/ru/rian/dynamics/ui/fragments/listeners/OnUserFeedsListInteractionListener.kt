package ru.rian.dynamics.ui.fragments.listeners

import ru.rian.dynamics.retrofit.model.Feed

interface OnUserFeedsListInteractionListener {
    fun onUserFeedsListInteraction(item: Feed?)
}