package org.nexttracks.android.support.preferences

import android.content.SharedPreferences.OnSharedPreferenceChangeListener

interface OnModeChangedPreferenceChangedListener : OnSharedPreferenceChangeListener {
    fun onAttachAfterModeChanged()
}