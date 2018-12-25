package ru.rian.dynamics.utils


internal val ARTICLE_LIST_LIMIT = 20

internal val MILLI_SEC_IN_DAY = (24 * 60 * 60 * 1000).toLong() //millisecond per 24 hours

const val getHandshake = "getHandshake"
const val getArticles = "getArticles"
const val login = "login"
const val logout = "logout"
const val getFeeds = "getFeeds"
const val createFeed = "createFeed"
const val deleteFeed = "deleteFeed"
const val synchronizeFeeds = "synchronizeFeeds"
const val upsertSubscription = "upsertSubscription"
const val getNotifications = "getNotifications"


enum class FragmentId {
    ARTICLE_FRAGMENT_ID
}

const val FEED_TYPE_STORY: String = "story"
const val FEED_TYPE_USER: String = "user-feed"
const val FEED_TYPE_COMMON: String = "feed"

const val BASE_URL: String = "http://mterm.rian.ru/api/v3/"
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