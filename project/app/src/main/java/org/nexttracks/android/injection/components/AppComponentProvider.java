package org.nexttracks.android.injection.components;

import org.nexttracks.android.injection.components.AppComponent;

public class AppComponentProvider {
    private static AppComponent appComponent;

    public static AppComponent getAppComponent() {
        return appComponent;
    }

    public static void setAppComponent(AppComponent component) {
        appComponent = component;
    }
}
