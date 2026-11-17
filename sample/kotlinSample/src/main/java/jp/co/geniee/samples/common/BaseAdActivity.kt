package jp.co.geniee.samples.common

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import jp.co.geniee.samples.R
import java.util.Locale

/**
 * Template for every "load an ad" sample screen (Geniee SDK, Google mediation, AppLovin MAX).
 * It owns the UI (ad unit input, options, actions, status card) and state rendering;
 * subclasses only implement the format-specific SDK calls and report results through
 * [renderState] and [setInfo].
 */
abstract class BaseAdActivity : AppCompatActivity() {

    private val infoValues = LinkedHashMap<String, TextView>()
    private lateinit var adUnitInput: EditText
    private lateinit var stateBadge: TextView
    private lateinit var messageText: TextView
    private lateinit var slotTitle: TextView
    private lateinit var slotPlaceholder: TextView
    private lateinit var slotContainer: FrameLayout
    private lateinit var optionsContainer: LinearLayout
    private lateinit var loadButton: Button
    private lateinit var showButton: Button
    private var state = AdState.IDLE
    private var loadStartedAt = 0L

    protected abstract fun getFormatName(): String

    /** Shown under the format name, e.g. "Geniee SDK". */
    protected abstract fun getProviderName(): String

    protected abstract fun getDefaultAdUnitId(): String

    protected abstract fun getAdUnitPreferenceKey(): String

    /** Rows of the status card, in display order. */
    protected abstract fun getInfoLabels(): Array<String>

    protected abstract fun loadAd(adUnitId: String)

    protected abstract fun destroyAd()

    protected open fun getAdUnitLabel(): String = "Ad unit ID"

    /** Fullscreen formats get a Show button; inline formats get the ad slot at the top. */
    protected open fun isFullscreen(): Boolean = true

    protected open fun showAd() {}

    /** Add screen specific controls with [addSwitch] / [addSpinner]. */
    protected open fun onCreateOptions() {}

    /** Label of an optional text action under the buttons (e.g. "Open Ad Inspector"). */
    protected open fun getExtraActionLabel(): String? = null

    protected open fun onExtraActionClicked() {}

    /** Hook to run SDK initialization before the first load. */
    protected open fun requestLoad(adUnitId: String) {
        loadAd(adUnitId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ad_sample)
        title = getFormatName()

        findViewById<TextView>(R.id.ad_sample_format).text = getFormatName()
        findViewById<TextView>(R.id.ad_sample_provider).text = getProviderName()
        findViewById<TextView>(R.id.ad_sample_ad_unit_title).text = getAdUnitLabel()
        adUnitInput = findViewById(R.id.ad_sample_ad_unit_input)
        stateBadge = findViewById(R.id.ad_sample_state_badge)
        messageText = findViewById(R.id.ad_sample_message)
        slotTitle = findViewById(R.id.ad_sample_slot_title)
        slotPlaceholder = findViewById(R.id.ad_sample_slot_placeholder)
        slotContainer = findViewById(R.id.ad_sample_slot_container)
        optionsContainer = findViewById(R.id.ad_sample_options_container)
        loadButton = findViewById(R.id.ad_sample_load_button)
        showButton = findViewById(R.id.ad_sample_show_button)

        val clearButton = findViewById<View>(R.id.ad_sample_clear_button)
        clearButton.setOnClickListener {
            adUnitInput.setText("")
            adUnitInput.requestFocus()
        }
        adUnitInput.doAfterTextChanged { s ->
            clearButton.visibility = if (!s.isNullOrEmpty()) View.VISIBLE else View.GONE
        }
        adUnitInput.hint = "Enter " + getAdUnitLabel()
        adUnitInput.setText(loadSavedAdUnitId())

        if (isFullscreen()) {
            showButton.visibility = View.VISIBLE
            showButton.setOnClickListener { showAd() }
        } else {
            findViewById<View>(R.id.ad_sample_slot_card).visibility = View.VISIBLE
            setAdSlotSize(320, 50)
        }
        loadButton.setOnClickListener { onLoadClicked() }

        getExtraActionLabel()?.let { extraAction ->
            val extraActionView = findViewById<TextView>(R.id.ad_sample_extra_action)
            extraActionView.text = extraAction
            extraActionView.visibility = View.VISIBLE
            extraActionView.setOnClickListener { onExtraActionClicked() }
        }

        for (label in getInfoLabels()) {
            addInfoRow(label)
        }
        onCreateOptions()

        renderState(AdState.IDLE, "Not loaded.")
    }

    override fun onDestroy() {
        destroyAd()
        super.onDestroy()
    }

    private fun onLoadClicked() {
        val adUnitId = getAdUnitId()
        if (adUnitId.isEmpty()) {
            adUnitInput.error = getAdUnitLabel() + " is required"
            return
        }
        getPreferences().edit()
            .putString(getAdUnitPreferenceKey(), adUnitId)
            .putString(getAdUnitPreferenceKey() + DEFAULT_KEY_SUFFIX, getDefaultAdUnitId())
            .apply()
        hideKeyboard()

        clearInfo()
        loadStartedAt = SystemClock.elapsedRealtime()
        renderState(AdState.LOADING, "Loading...")
        requestLoad(adUnitId)
    }

    /**
     * Returns the ad unit ID saved on the last load. The saved value is ignored when the default
     * in code has changed since it was saved, so updating getDefaultAdUnitId() takes effect
     * without clearing app data.
     */
    private fun loadSavedAdUnitId(): String {
        val defaultAdUnitId = getDefaultAdUnitId()
        val prefs = getPreferences()
        val savedDefault = prefs.getString(getAdUnitPreferenceKey() + DEFAULT_KEY_SUFFIX, null)
        val saved = prefs.getString(getAdUnitPreferenceKey(), null)
        if (saved == null || defaultAdUnitId != savedDefault) {
            return defaultAdUnitId
        }
        // Older builds saved the "YOUR_ZONE_ID" placeholder as a real value; treat it as empty.
        return if (saved == "YOUR_ZONE_ID") "" else saved
    }

    // region API for subclasses

    protected fun getAdUnitId(): String = adUnitInput.text.toString().trim()

    protected fun getState(): AdState = state

    /** Time since Load was tapped, formatted for the Latency row. */
    protected fun getElapsedSinceLoad(): String =
        formatMillis(SystemClock.elapsedRealtime() - loadStartedAt)

    protected fun renderState(state: AdState, message: String) {
        Log.i(TAG, "${getProviderName()} ${getFormatName()} ${state.label}: $message")
        this.state = state

        stateBadge.background = GradientDrawable().apply {
            cornerRadius = dp(12)
            setColor(ContextCompat.getColor(this@BaseAdActivity, state.colorRes))
        }
        stateBadge.text = state.label.uppercase(Locale.US)
        messageText.text = message

        loadButton.isEnabled = state.canLoad
        showButton.isEnabled = state.canShow

        if (!isFullscreen()) {
            renderSlotPlaceholder(state)
        }
    }

    protected fun setInfo(label: String, value: String?) {
        infoValues[label]?.text = orDash(value)
    }

    protected fun clearInfo() {
        for (view in infoValues.values) {
            view.text = "-"
        }
    }

    /** Container for inline (banner) ad views; the placeholder stays below the ad view. */
    protected fun getAdSlot(): FrameLayout = slotContainer

    protected fun setAdSlotSize(widthDp: Int, heightDp: Int) {
        val params = slotContainer.layoutParams
        params.width = dp(widthDp).toInt()
        params.height = dp(heightDp).toInt()
        slotContainer.layoutParams = params
        slotTitle.text = String.format(Locale.US, "Ad slot · %d × %d", widthDp, heightDp)
    }

    protected fun addSwitch(label: String, preferenceKey: String): Switch {
        val view = Switch(this)
        view.text = label
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        view.setTextColor(ContextCompat.getColor(this, R.color.ad_sample_text_primary))
        view.minHeight = dp(44).toInt()
        view.isChecked = SharedPreferenceManager.getInstance(this).getBoolean(preferenceKey)
        view.setOnCheckedChangeListener { _, isChecked ->
            SharedPreferenceManager.getInstance(this).putBoolean(preferenceKey, isChecked)
        }
        addOption(view)
        return view
    }

    protected fun addSpinner(label: String, items: Array<String>): Spinner {
        val row = LinearLayout(this)
        row.orientation = LinearLayout.HORIZONTAL
        row.gravity = Gravity.CENTER_VERTICAL
        row.minimumHeight = dp(44).toInt()

        val labelView = TextView(this)
        labelView.text = label
        labelView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        labelView.setTextColor(ContextCompat.getColor(this, R.color.ad_sample_text_primary))
        row.addView(labelView, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))

        val spinner = Spinner(this)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        row.addView(spinner, LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        addOption(row)
        return spinner
    }

    // endregion

    private fun addOption(view: View) {
        findViewById<View>(R.id.ad_sample_options_card).visibility = View.VISIBLE
        optionsContainer.addView(view, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
    }

    private fun addInfoRow(label: String) {
        val row = LinearLayout(this)
        row.orientation = LinearLayout.HORIZONTAL
        val rowParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        rowParams.topMargin = dp(8).toInt()

        val labelView = TextView(this)
        labelView.text = label
        labelView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        labelView.setTextColor(ContextCompat.getColor(this, R.color.ad_sample_text_secondary))
        row.addView(labelView, LinearLayout.LayoutParams(dp(104).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT))

        val valueView = TextView(this)
        valueView.text = "-"
        valueView.setTextColor(ContextCompat.getColor(this, R.color.ad_sample_text_primary))
        valueView.setTextIsSelectable(true)
        if (label == INFO_ADAPTER_CLASS) {
            valueView.typeface = Typeface.MONOSPACE
            valueView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
        } else {
            valueView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        }
        if (label == INFO_NETWORK) {
            valueView.typeface = Typeface.DEFAULT_BOLD
        }
        row.addView(valueView, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))

        findViewById<LinearLayout>(R.id.ad_sample_info_container).addView(row, rowParams)
        infoValues[label] = valueView
    }

    private fun renderSlotPlaceholder(state: AdState) {
        slotPlaceholder.text = when (state) {
            AdState.LOADING -> "Loading banner..."
            AdState.FAILED -> "No ad"
            else -> "Banner ad will appear here"
        }
        // The ad view is added on top of the placeholder; hide it once an ad is shown.
        slotPlaceholder.visibility = if (state == AdState.LOADED) View.INVISIBLE else View.VISIBLE
    }

    private fun getPreferences(): SharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

    private fun hideKeyboard() {
        adUnitInput.clearFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(adUnitInput.windowToken, 0)
    }

    private fun dp(value: Int): Float =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value.toFloat(), resources.displayMetrics)

    companion object {
        private const val TAG = "AdSample"
        private const val PREFS_NAME = "Settings"
        // Stores the code default that was current when the ad unit ID was saved.
        private const val DEFAULT_KEY_SUFFIX = "_default"

        // Shared vocabulary for the status card rows.
        const val INFO_NETWORK = "Network"
        const val INFO_ADAPTER_CLASS = "Adapter class"
        const val INFO_ADAPTER_VERSION = "Adapter ver."
        const val INFO_SDK_VERSION = "SDK ver."
        const val INFO_AD_UNIT = "Ad unit"
        const val INFO_PLACEMENT = "Placement"
        const val INFO_SIZE = "Size"
        const val INFO_LATENCY = "Latency"
        const val INFO_REVENUE = "Revenue"
        const val INFO_REWARD = "Reward"

        fun formatMillis(millis: Long): String = String.format(Locale.US, "%d ms", millis)

        fun orDash(value: String?): String = if (value.isNullOrEmpty()) "-" else value
    }
}
