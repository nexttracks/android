package org.nexttracks.android.injection.modules.android;

import org.nexttracks.android.injection.modules.android.ActivityModules.ConnectionActivityModule;
import org.nexttracks.android.injection.modules.android.ActivityModules.ContactsActivityModule;
import org.nexttracks.android.injection.modules.android.ActivityModules.EditorActivityModule;
import org.nexttracks.android.injection.modules.android.ActivityModules.LoadActivityModule;
import org.nexttracks.android.injection.modules.android.ActivityModules.LogViewerActivityModule;
import org.nexttracks.android.injection.modules.android.ActivityModules.MapActivityModule;
import org.nexttracks.android.injection.modules.android.ActivityModules.PreferencesActivityModule;
import org.nexttracks.android.injection.modules.android.ActivityModules.RegionActivityModule;
import org.nexttracks.android.injection.modules.android.ActivityModules.RegionsActivityModule;
import org.nexttracks.android.injection.modules.android.ActivityModules.StatusActivityModule;
import org.nexttracks.android.injection.modules.android.ActivityModules.WelcomeActivityModule;
import org.nexttracks.android.injection.modules.android.ServiceModules.BackgroundServiceModule;
import org.nexttracks.android.injection.scopes.PerActivity;
import org.nexttracks.android.injection.scopes.PerReceiver;
import org.nexttracks.android.injection.scopes.PerService;
import org.nexttracks.android.support.receiver.StartBackgroundServiceReceiver;
import org.nexttracks.android.ui.preferences.LogViewerActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class AndroindBindingModule {
    @PerActivity
    @ContributesAndroidInjector(modules = {ContactsActivityModule.class})
    abstract org.nexttracks.android.ui.contacts.ContactsActivity bindContactsActivity();

    @PerActivity
    @ContributesAndroidInjector(modules = {MapActivityModule.class})
    abstract org.nexttracks.android.ui.map.MapActivity bindMapActivity();

    @PerActivity
    @ContributesAndroidInjector(modules = {PreferencesActivityModule.class})
    abstract org.nexttracks.android.ui.preferences.PreferencesActivity bindPreferencesActivity();

    @PerActivity
    @ContributesAndroidInjector(modules = {ConnectionActivityModule.class})
    abstract org.nexttracks.android.ui.preferences.connection.ConnectionActivity bindConnectionActivity();

    @PerActivity
    @ContributesAndroidInjector(modules = {LogViewerActivityModule.class})
    abstract LogViewerActivity bindLogViewerActivity();

    @PerActivity
    @ContributesAndroidInjector(modules = {EditorActivityModule.class})
    abstract org.nexttracks.android.ui.preferences.editor.EditorActivity bindEditorActivity();

    @PerActivity
    @ContributesAndroidInjector(modules = {LoadActivityModule.class})
    abstract org.nexttracks.android.ui.preferences.load.LoadActivity bindLoadActivity();

    @PerActivity
    @ContributesAndroidInjector(modules = {StatusActivityModule.class})
    abstract org.nexttracks.android.ui.status.StatusActivity bindStatusActivity();

    @PerActivity
    @ContributesAndroidInjector(modules = {WelcomeActivityModule.class})
    abstract org.nexttracks.android.ui.welcome.WelcomeActivity bindWelcomeActivity();

    @PerActivity
    @ContributesAndroidInjector(modules = {RegionsActivityModule.class})
    abstract org.nexttracks.android.ui.regions.RegionsActivity bindRegionsActivity();

    @PerActivity
    @ContributesAndroidInjector(modules = {RegionActivityModule.class})
    abstract org.nexttracks.android.ui.region.RegionActivity bindRegionActivity();

    @PerService
    @ContributesAndroidInjector(modules = {BackgroundServiceModule.class})
    abstract org.nexttracks.android.services.BackgroundService bindBackgroundService();

    @PerReceiver
    @ContributesAndroidInjector
    abstract StartBackgroundServiceReceiver bindBackgroundServiceReceiver();
}
