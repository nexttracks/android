package org.nexttracks.android.ui.preferences

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import org.nexttracks.android.R

class LicenseFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_licenses, rootKey)
    }
}