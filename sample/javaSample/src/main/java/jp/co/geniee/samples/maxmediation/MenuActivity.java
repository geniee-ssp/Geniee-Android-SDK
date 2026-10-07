package jp.co.geniee.samples.maxmediation;

import android.content.Intent;
import android.os.Bundle;

import jp.co.geniee.samples.BaseMenuActivity;
import jp.co.geniee.samples.MenuItem;

public class MenuActivity extends BaseMenuActivity {
    private static final int MENU_POSITION_MEDIATION_DEBUGGER = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize early so the first load request does not wait for SDK init.
        MaxMediationHelper.initialize(this, () -> {
        });
    }

    @Override
    protected MenuItem[] getListViewContents() {
        MenuItem[] menuItem = {
                new MenuItem("Banner", "", new Intent(this, jp.co.geniee.samples.maxmediation.BannerActivity.class)),
                new MenuItem("Interstitial", "", new Intent(this, jp.co.geniee.samples.maxmediation.InterstitialActivity.class)),
                new MenuItem("Rewarded", "", new Intent(this, jp.co.geniee.samples.maxmediation.RewardedActivity.class)),
                new MenuItem("Mediation Debugger", "Check that the Geniee adapter is detected", null)
        };

        return menuItem;
    }

    @Override
    protected void onMenuItemClick(int position) {
        if (position == MENU_POSITION_MEDIATION_DEBUGGER) {
            MaxMediationHelper.initialize(this, () -> MaxMediationHelper.showMediationDebugger(this));
        }
    }
}
