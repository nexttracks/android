package org.nexttracks.android.robolectric;

import org.nexttracks.android.App;
import org.nexttracks.android.injection.components.AppComponent;
import org.nexttracks.android.injection.modules.AppModule;
import org.nexttracks.android.injection.modules.android.AndroindBindingModule;
import org.nexttracks.android.injection.scopes.PerApplication;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;

@PerApplication
@Component(modules = {AppModule.class, DummyWaypointsModule.class, AndroidSupportInjectionModule.class, AndroindBindingModule.class})
public interface AppComponentForTest extends AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder app(App app);

        AppComponentForTest build();

    }
}
