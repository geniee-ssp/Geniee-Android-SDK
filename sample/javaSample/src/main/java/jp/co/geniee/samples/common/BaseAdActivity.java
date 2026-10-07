package jp.co.geniee.samples.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import jp.co.geniee.samples.R;
import jp.co.geniee.samples.SharedPreferenceManager;

/**
 * Template for every "load an ad" sample screen (Geniee SDK, Google mediation, AppLovin MAX).
 * It owns the UI (ad unit input, options, actions, status card) and state rendering;
 * subclasses only implement the format-specific SDK calls and report results through
 * {@link #renderState} and {@link #setInfo}.
 */
public abstract class BaseAdActivity extends AppCompatActivity {
    private static final String TAG = "AdSample";
    private static final String PREFS_NAME = "Settings";

    // Shared vocabulary for the status card rows.
    protected static final String INFO_NETWORK = "Network";
    protected static final String INFO_ADAPTER_CLASS = "Adapter class";
    protected static final String INFO_ADAPTER_VERSION = "Adapter ver.";
    protected static final String INFO_SDK_VERSION = "SDK ver.";
    protected static final String INFO_AD_UNIT = "Ad unit";
    protected static final String INFO_PLACEMENT = "Placement";
    protected static final String INFO_SIZE = "Size";
    protected static final String INFO_LATENCY = "Latency";
    protected static final String INFO_REVENUE = "Revenue";
    protected static final String INFO_REWARD = "Reward";

    private final Map<String, TextView> infoValues = new LinkedHashMap<>();
    private EditText adUnitInput;
    private TextView stateBadge;
    private TextView messageText;
    private TextView slotTitle;
    private TextView slotPlaceholder;
    private FrameLayout slotContainer;
    private LinearLayout optionsContainer;
    private Button loadButton;
    private Button showButton;
    private AdState state = AdState.IDLE;
    private long loadStartedAt;

    protected abstract String getFormatName();

    /** Shown under the format name, e.g. "Geniee SDK". */
    protected abstract String getProviderName();

    protected abstract String getDefaultAdUnitId();

    protected abstract String getAdUnitPreferenceKey();

    /** Rows of the status card, in display order. */
    protected abstract String[] getInfoLabels();

    protected abstract void loadAd(String adUnitId);

    protected abstract void destroyAd();

    protected String getAdUnitLabel() {
        return "Ad unit ID";
    }

    /** Fullscreen formats get a Show button; inline formats get the ad slot at the top. */
    protected boolean isFullscreen() {
        return true;
    }

    protected void showAd() {
    }

    /** Add screen specific controls with {@link #addSwitch} / {@link #addSpinner}. */
    protected void onCreateOptions() {
    }

    /** Label of an optional text action under the buttons (e.g. "Open Ad Inspector"). */
    protected String getExtraActionLabel() {
        return null;
    }

    protected void onExtraActionClicked() {
    }

    /** Hook to run SDK initialization before the first load. */
    protected void requestLoad(String adUnitId) {
        loadAd(adUnitId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_sample);
        setTitle(getFormatName());

        ((TextView) findViewById(R.id.ad_sample_format)).setText(getFormatName());
        ((TextView) findViewById(R.id.ad_sample_provider)).setText(getProviderName());
        ((TextView) findViewById(R.id.ad_sample_ad_unit_title)).setText(getAdUnitLabel());
        adUnitInput = findViewById(R.id.ad_sample_ad_unit_input);
        stateBadge = findViewById(R.id.ad_sample_state_badge);
        messageText = findViewById(R.id.ad_sample_message);
        slotTitle = findViewById(R.id.ad_sample_slot_title);
        slotPlaceholder = findViewById(R.id.ad_sample_slot_placeholder);
        slotContainer = findViewById(R.id.ad_sample_slot_container);
        optionsContainer = findViewById(R.id.ad_sample_options_container);
        loadButton = findViewById(R.id.ad_sample_load_button);
        showButton = findViewById(R.id.ad_sample_show_button);

        View clearButton = findViewById(R.id.ad_sample_clear_button);
        clearButton.setOnClickListener(v -> {
            adUnitInput.setText("");
            adUnitInput.requestFocus();
        });
        adUnitInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                clearButton.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }
        });
        adUnitInput.setHint("Enter " + getAdUnitLabel());
        String savedAdUnitId = getPreferences().getString(getAdUnitPreferenceKey(), getDefaultAdUnitId());
        // Older builds saved the "YOUR_ZONE_ID" placeholder as a real value; treat it as empty.
        adUnitInput.setText("YOUR_ZONE_ID".equals(savedAdUnitId) ? "" : savedAdUnitId);

        if (isFullscreen()) {
            showButton.setVisibility(View.VISIBLE);
            showButton.setOnClickListener(v -> showAd());
        } else {
            findViewById(R.id.ad_sample_slot_card).setVisibility(View.VISIBLE);
            setAdSlotSize(320, 50);
        }
        loadButton.setOnClickListener(v -> onLoadClicked());

        String extraAction = getExtraActionLabel();
        if (extraAction != null) {
            TextView extraActionView = findViewById(R.id.ad_sample_extra_action);
            extraActionView.setText(extraAction);
            extraActionView.setVisibility(View.VISIBLE);
            extraActionView.setOnClickListener(v -> onExtraActionClicked());
        }

        for (String label : getInfoLabels()) {
            addInfoRow(label);
        }
        onCreateOptions();

        renderState(AdState.IDLE, "Not loaded.");
    }

    @Override
    protected void onDestroy() {
        destroyAd();
        super.onDestroy();
    }

    private void onLoadClicked() {
        String adUnitId = getAdUnitId();
        if (adUnitId.isEmpty()) {
            adUnitInput.setError(getAdUnitLabel() + " is required");
            return;
        }
        getPreferences().edit().putString(getAdUnitPreferenceKey(), adUnitId).apply();
        hideKeyboard();

        clearInfo();
        loadStartedAt = SystemClock.elapsedRealtime();
        renderState(AdState.LOADING, "Loading...");
        requestLoad(adUnitId);
    }

    // region API for subclasses

    protected String getAdUnitId() {
        return adUnitInput.getText().toString().trim();
    }

    protected AdState getState() {
        return state;
    }

    /** Time since Load was tapped, formatted for the Latency row. */
    protected String getElapsedSinceLoad() {
        return formatMillis(SystemClock.elapsedRealtime() - loadStartedAt);
    }

    protected void renderState(AdState state, String message) {
        Log.i(TAG, getProviderName() + " " + getFormatName() + " " + state.label + ": " + message);
        this.state = state;

        GradientDrawable badge = new GradientDrawable();
        badge.setCornerRadius(dp(12));
        badge.setColor(ContextCompat.getColor(this, state.colorRes));
        stateBadge.setBackground(badge);
        stateBadge.setText(state.label.toUpperCase(Locale.US));
        messageText.setText(message);

        loadButton.setEnabled(state.canLoad);
        showButton.setEnabled(state.canShow);

        if (!isFullscreen()) {
            renderSlotPlaceholder(state);
        }
    }

    protected void setInfo(String label, String value) {
        TextView view = infoValues.get(label);
        if (view != null) {
            view.setText(orDash(value));
        }
    }

    protected void clearInfo() {
        for (TextView view : infoValues.values()) {
            view.setText("-");
        }
    }

    /** Container for inline (banner) ad views; the placeholder stays below the ad view. */
    protected FrameLayout getAdSlot() {
        return slotContainer;
    }

    protected void setAdSlotSize(int widthDp, int heightDp) {
        ViewGroup.LayoutParams params = slotContainer.getLayoutParams();
        params.width = (int) dp(widthDp);
        params.height = (int) dp(heightDp);
        slotContainer.setLayoutParams(params);
        slotTitle.setText(String.format(Locale.US, "Ad slot · %d × %d", widthDp, heightDp));
    }

    protected Switch addSwitch(String label, final String preferenceKey) {
        Switch view = new Switch(this);
        view.setText(label);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        view.setTextColor(ContextCompat.getColor(this, R.color.ad_sample_text_primary));
        view.setMinHeight((int) dp(44));
        view.setChecked(SharedPreferenceManager.getInstance(this).getBoolean(preferenceKey));
        view.setOnCheckedChangeListener((buttonView, isChecked) ->
                SharedPreferenceManager.getInstance(this).putBoolean(preferenceKey, isChecked));
        addOption(view);
        return view;
    }

    protected Spinner addSpinner(String label, String[] items) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setMinimumHeight((int) dp(44));

        TextView labelView = new TextView(this);
        labelView.setText(label);
        labelView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        labelView.setTextColor(ContextCompat.getColor(this, R.color.ad_sample_text_primary));
        row.addView(labelView, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        Spinner spinner = new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        row.addView(spinner, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        addOption(row);
        return spinner;
    }

    protected static String formatMillis(long millis) {
        return String.format(Locale.US, "%d ms", millis);
    }

    protected static String orDash(String value) {
        return value == null || value.isEmpty() ? "-" : value;
    }

    // endregion

    private void addOption(View view) {
        findViewById(R.id.ad_sample_options_card).setVisibility(View.VISIBLE);
        optionsContainer.addView(view, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    private void addInfoRow(String label) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rowParams.topMargin = (int) dp(8);

        TextView labelView = new TextView(this);
        labelView.setText(label);
        labelView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        labelView.setTextColor(ContextCompat.getColor(this, R.color.ad_sample_text_secondary));
        row.addView(labelView, new LinearLayout.LayoutParams((int) dp(104), ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView valueView = new TextView(this);
        valueView.setText("-");
        valueView.setTextColor(ContextCompat.getColor(this, R.color.ad_sample_text_primary));
        valueView.setTextIsSelectable(true);
        if (INFO_ADAPTER_CLASS.equals(label)) {
            valueView.setTypeface(Typeface.MONOSPACE);
            valueView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        } else {
            valueView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        }
        if (INFO_NETWORK.equals(label)) {
            valueView.setTypeface(Typeface.DEFAULT_BOLD);
        }
        row.addView(valueView, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        ((LinearLayout) findViewById(R.id.ad_sample_info_container)).addView(row, rowParams);
        infoValues.put(label, valueView);
    }

    private void renderSlotPlaceholder(AdState state) {
        switch (state) {
            case LOADING:
                slotPlaceholder.setText("Loading banner...");
                break;
            case FAILED:
                slotPlaceholder.setText("No ad");
                break;
            default:
                slotPlaceholder.setText("Banner ad will appear here");
                break;
        }
        // The ad view is added on top of the placeholder; hide it once an ad is shown.
        slotPlaceholder.setVisibility(state == AdState.LOADED ? View.INVISIBLE : View.VISIBLE);
    }

    private SharedPreferences getPreferences() {
        return getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    }

    private void hideKeyboard() {
        adUnitInput.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(adUnitInput.getWindowToken(), 0);
    }

    private float dp(int value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }
}
