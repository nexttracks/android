package org.nexttracks.android.injection.modules.android.ActivityModules;

import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;

import org.nexttracks.android.injection.qualifier.ActivityContext;
import org.nexttracks.android.injection.scopes.PerActivity;
import org.nexttracks.android.support.Preferences;
import org.nexttracks.android.support.DrawerProvider;
import org.nexttracks.android.support.RequirementsChecker;
import org.nexttracks.android.ui.base.navigator.Navigator;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public abstract class BaseActivityModule {
    public static final String ACTIVITY_FRAGMENT_MANAGER = "BaseActivityModule.activityFragmentManager";

    @Provides
    @PerActivity
    @ActivityContext
    static AppCompatActivity activityContext(AppCompatActivity activity) {
        return activity;
    }

    @Provides
    @PerActivity
    static DrawerProvider provideDrawerProvider(@ActivityContext AppCompatActivity context) { return new DrawerProvider(context); }

    @Provides
    @PerActivity
    @Named(ACTIVITY_FRAGMENT_MANAGER)
    static FragmentManager provideFragmentManager(@ActivityContext AppCompatActivity context) { return context.getSupportFragmentManager(); }


    @Provides
    @PerActivity
    static Navigator provideNavigator(@ActivityContext AppCompatActivity context) { return new Navigator(context); }

    @Provides
    @PerActivity
    static RequirementsChecker provideRequirementsChecker(@ActivityContext AppCompatActivity context, Preferences preferences) {return new RequirementsChecker(preferences, context); }
}
