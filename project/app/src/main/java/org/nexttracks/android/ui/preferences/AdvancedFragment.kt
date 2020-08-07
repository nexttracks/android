package org.nexttracks.android.ui.preferences

import android.os.Bundle
import dagger.Binds
import dagger.Module
import org.nexttracks.android.R
import org.nexttracks.android.injection.modules.android.FragmentModules.BaseFragmentModule
import org.nexttracks.android.injection.scopes.PerFragment

@PerFragment
class AdvancedFragment : AbstractPreferenceFragment() {

    override fun onCreatePreferencesFix(savedInstanceState: Bundle?, rootKey: String?) {
        super.onCreatePreferencesFix(savedInstanceState, rootKey)
        setPreferencesFromResource(R.xml.preferences_advanced, rootKey)
    }

    @Module(includes = [BaseFragmentModule::class])
    internal abstract class FragmentModule {
        @Binds
        @PerFragment
        abstract fun bindFragment(reportingFragment: AdvancedFragment): AdvancedFragment
    }

}
