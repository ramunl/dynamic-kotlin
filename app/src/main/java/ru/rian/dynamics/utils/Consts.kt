package ru.rian.dynamics.utils

import org.slf4j.Marker
import org.slf4j.MarkerFactory

const val HTTP_TIMEOUT_REQUEST = 20 * 1000
const val HTTP_TIMEOUT_CONNECTION = 5 * 1000

const val HEADER_ACCEPT = "Accept"
const val HEADER_ACCEPT_VALUE_APP_JSON = "application/json"
const val HEADER_USER_AGENT = "User-Agent"
const val HEADER_TOKEN = "Mterm-Token"

val DEFAULT_USER_AGENT =
    "Mozilla/5.0 (Linux; Android 6.0.1; SM-A510F Build/MMB29K; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/59.0.3071.125 Mobile Safari/537.36"

val ACTIVITY_MARKER: Marker = MarkerFactory.getMarker("Activity")
internal val ARTICLE_LIST_LIMIT = 20
internal val MILLI_SEC_IN_DAY = (24 * 60 * 60 * 1000).toLong() //millisecond per 24 hours
/*
const val getHandshake = "getHandshake"
const val getArticles = "getArticles"
const val login = "login"
const val logout = "logout"
const val getFeeds = "selectFeeds"
const val createFeed = "createFeed"
const val deleteFeed = "deleteFeed"
const val synchronizeFeeds = "synchronizeFeeds"
const val upsertSubscription = "upsertSubscription"
const val getNotifications = "getNotifications"*/


const val TYPE_FEED_SUBSCRIPTION_ALL = "allnews"
const val TYPE_FEED_SUBSCRIPTION_BREAKING = "breakingnews"

enum class FragmentId {
    USER_FEED_FRAGMENT_ID,
    MAIN_FEED_FRAGMENT_ID,
    USER_FEEDS_FRAGMENT_ID,
    TERMINAL_LOGIN_FRAGMENT_ID
}

const val FEED_TYPE_STORY: String = "story"
const val FEED_TYPE_USER: String = "user-feed"
const val FEED_TYPE_COMMON: String = "feed"

const val BASE_URL: String = "http://mterm.rian.ru/api/v3/"
const val LOGIN_PATH: String = "login"
const val HS_PATH: String = "handshake"
const val TRENDING: String = "trending"
const val PLAYER_ID: String = "PLAYER_ID"

const val PROMO_DIALOG_FLAG: String = "PROMO_DIALOG_FLAG"
const val SETTING_PROMO_NEWS: String = "SETTING_PROMO_NEWS"
const val SETTING_PROMO_OPEN_FLAG: String = "SETTING_PROMO_OPEN_FLAG"
const val SETTING_MY_NEWS: String = "SETTING_MY_NEWS"
const val SETTING_MY_FLASHES: String = "SETTING_MY_FLASHES"
const val SETTING_ALL_NEWS: String = "SETTING_ALL_NEWS"


const val SEARCH_TYPE_AND: String = "and"
const val SEARCH_TYPE_ALL: String = "all"
const val SEARCH_TYPE_OR: String = "or"

const val SEARCH_TYPE_KEY: String = "SEARCH_TYPE_KEY"
const val FLASH_BOOLEAN_KEY: String = "FLASH_BOOLEAN_KEY"
const val PASS_STRING_KEY: String = "PASS_STRING_KEY"
const val LOGIN_STRING_KEY: String = "LOGIN_STRING_KEY"
const val TOKEN_STRING_KEY: String = "TOKEN_STRING_KEY"
const val SAMPLE_LONG_KEY: String = "SAMPLE_LONG_KEY"
const val FIRST_LAUNCH_KEY: String = "LAUNCH_KEY"
const val HANDSHAKE: String = "HANDSHAKE"
const val FEEDS: String = "FEEDS"
const val EVENTS: String = "EVENTS"
const val FEED_SELECTED: String = "FEED_SELECTED"
const val FEEDS_NEW: String = "FEEDS_NEW"
fun asd() = {}