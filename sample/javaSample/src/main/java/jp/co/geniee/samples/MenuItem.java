package jp.co.geniee.samples;

import android.content.Intent;

public class MenuItem {

    private String mTitle;
    private String mSubtitle;
    private Intent mIntent;

    public MenuItem(String title, String subTitle, Intent intent) {
        this.mTitle = title;
        this.mSubtitle = subTitle;
        this.mIntent = intent;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getSubtitle() {
        return mSubtitle;
    }

    public void setSubtitle(String mSubtitle) {
        this.mSubtitle = mSubtitle;
    }

    public Intent getIntent() {
        return mIntent;
    }

    public void setIntent(Intent mIntent) {
        this.mIntent = mIntent;
    }
}
