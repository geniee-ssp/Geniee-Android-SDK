package jp.co.geniee.samples.common

import androidx.annotation.ColorRes
import jp.co.geniee.samples.R

/**
 * Lifecycle of an ad in the sample screens. Each state knows how it is rendered and which
 * actions are available, so the screens only need to switch state.
 */
enum class AdState(
    val label: String,
    @ColorRes val colorRes: Int,
    val canLoad: Boolean,
    val canShow: Boolean,
) {
    IDLE("Idle", R.color.ad_sample_state_idle, true, false),
    LOADING("Loading", R.color.ad_sample_state_loading, false, false),
    LOADED("Loaded", R.color.ad_sample_state_loaded, true, true),
    SHOWING("Showing", R.color.ad_sample_state_showing, false, false),
    CLOSED("Closed", R.color.ad_sample_state_closed, true, false),
    FAILED("Failed", R.color.ad_sample_state_failed, true, false),
}
