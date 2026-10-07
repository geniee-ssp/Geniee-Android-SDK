package jp.co.geniee.samples.common;

import androidx.annotation.ColorRes;

import jp.co.geniee.samples.R;

/**
 * Lifecycle of an ad in the sample screens. Each state knows how it is rendered and which
 * actions are available, so the screens only need to switch state.
 */
public enum AdState {
    IDLE("Idle", R.color.ad_sample_state_idle, true, false),
    LOADING("Loading", R.color.ad_sample_state_loading, false, false),
    LOADED("Loaded", R.color.ad_sample_state_loaded, true, true),
    SHOWING("Showing", R.color.ad_sample_state_showing, false, false),
    CLOSED("Closed", R.color.ad_sample_state_closed, true, false),
    FAILED("Failed", R.color.ad_sample_state_failed, true, false);

    public final String label;
    @ColorRes
    public final int colorRes;
    public final boolean canLoad;
    public final boolean canShow;

    AdState(String label, @ColorRes int colorRes, boolean canLoad, boolean canShow) {
        this.label = label;
        this.colorRes = colorRes;
        this.canLoad = canLoad;
        this.canShow = canShow;
    }
}
