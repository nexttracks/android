package org.nexttracks.android.ui.preferences

import android.content.Intent
import android.os.Bundle
import androidx.preference.Preference
import dagger.Binds
import dagger.Module
import org.nexttracks.android.R
import org.nexttracks.android.injection.modules.android.FragmentModules.BaseFragmentModule
import org.nexttracks.android.injection.scopes.PerFragment
import org.nexttracks.android.services.MessageProcessorEndpointHttp
import org.nexttracks.android.services.MessageProcessorEndpointMqtt
import org.nexttracks.android.ui.preferences.connection.ConnectionActivity
import org.nexttracks.android.ui.preferences.editor.EditorActivity

class PreferencesFragment : AbstractPreferenceFragment() {
    override fun onCreatePreferencesFix(savedInstanceState: Bundle?, rootKey: String?) {
        super.onCreatePreferencesFix(savedInstanceState, rootKey)
        setPreferencesFromResource(R.xml.preferences_root, rootKey)
        // Have to do these manually here, as there's an android bug that prevents the activity from being found when launched from intent declared on the preferences XML.
        findPreference<Preference>(UI_SCREEN_CONFIGURATION)!!.intent = Intent(context, EditorActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

        //TODO move this to a preferences fragment rather than its own activity.
        findPreference<Preference>(UI_PREFERENCE_SCREEN_CONNECTION)!!.intent = Intent(context, ConnectionActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
    }

    override fun onResume() {
        super.onResume()
        findPreference<Preference>(UI_PREFERENCE_SCREEN_CONNECTION)!!.summary = connectionMode
    }

    private val connectionMode: String
        get() = when (preferences.mode) {
            MessageProcessorEndpointHttp.MODE_ID -> getString(R.string.mode_http_private_label)
            MessageProcessorEndpointMqtt.MODE_ID -> getString(R.string.mode_mqtt_private_label)
            else -> getString(R.string.mode_mqtt_private_label)
        }

    companion object {
        private const val UI_PREFERENCE_SCREEN_CONNECTION = "connectionScreen"
        private const val UI_SCREEN_CONFIGURATION = "configuration"
    }

    @Module(includes = [BaseFragmentModule::class])
    internal abstract class FragmentModule {
        @Binds
        @PerFragment
        abstract fun bindFragment(reportingFragment: PreferencesFragment): PreferencesFragment
    }
}
