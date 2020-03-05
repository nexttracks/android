package org.nexttracks.android.robolectric;

import android.Manifest;
import android.content.Intent;
import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.nexttracks.android.App;
import org.nexttracks.android.R;
import org.nexttracks.android.ui.map.MapActivity;
import org.nexttracks.android.ui.welcome.WelcomeActivity;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.os.Build.VERSION_CODES.LOLLIPOP_MR1;
import static android.os.Build.VERSION_CODES.M;
import static android.os.Build.VERSION_CODES.P;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.robolectric.shadows.ShadowView.clickOn;

/**
 * Objectbox doesn't like being used with Robolectric due to trying to repeatedly load the native
 * lib and not failing gracefully if it's already loaded. Thus, we have a new App that swaps out the
 * ObjectboxWaypointsRepo and uses a component that injects a DummyWaypointsRepo instead.
 */
class TestApp extends App {
    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        AppComponentForTest appComponent = DaggerAppComponentForTest.builder().app(this).build();
        appComponent.inject(this);
        return appComponent;
    }
}


@RunWith(RobolectricTestRunner.class)
@Config(minSdk = LOLLIPOP, maxSdk = P, application = TestApp.class, shadows = {ShadowViewPager.class})
public class WelcomeActivityTest {

    @Spy
    private WelcomeActivity welcomeActivity;


    @Before
    public void setup() {

        ActivityController<WelcomeActivity> welcomeActivityActivityController = Robolectric.buildActivity(WelcomeActivity.class);


        welcomeActivity = welcomeActivityActivityController.get();
        ShadowApplication application = Shadows.shadowOf(welcomeActivity.getApplication());

        application.grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION);
        assertNotNull(welcomeActivity);
        welcomeActivityActivityController.setup();

    }

    @Test
    @Config(minSdk = M)
    public void DoneButtonShouldStartMapActivityOnVersionWithBackgroundRestriction() {
        // Shows welcome
        assertEquals(View.VISIBLE, welcomeActivity.findViewById(R.id.btn_next).getVisibility());
        assertEquals(View.GONE, welcomeActivity.findViewById(R.id.done).getVisibility());
        assertTrue(welcomeActivity.findViewById(R.id.btn_next).isEnabled());
        clickOn(welcomeActivity.findViewById(R.id.btn_next));

        // Shows Restrictions
        assertEquals(View.VISIBLE, welcomeActivity.findViewById(R.id.btn_next).getVisibility());
        assertEquals(View.GONE, welcomeActivity.findViewById(R.id.done).getVisibility());
        assertTrue(welcomeActivity.findViewById(R.id.btn_next).isEnabled());
        clickOn(welcomeActivity.findViewById(R.id.btn_next));

        // Shows done
        assertEquals(View.GONE, welcomeActivity.findViewById(R.id.btn_next).getVisibility());
        assertEquals(View.VISIBLE, welcomeActivity.findViewById(R.id.done).getVisibility());
        assertFalse(welcomeActivity.findViewById(R.id.btn_next).isEnabled());
        assertTrue(welcomeActivity.findViewById(R.id.done).isEnabled());

        clickOn(welcomeActivity.findViewById(R.id.done));
        Intent expectedIntent = new Intent(welcomeActivity, MapActivity.class);
        Intent actualIntent = ShadowApplication.getInstance().getNextStartedActivity();
        assertEquals(expectedIntent.getComponent(), actualIntent.getComponent());
    }

    @Test
    @Config(maxSdk = LOLLIPOP_MR1)
    public void DoneButtonShouldStartMapActivityOnVersionWithoutBackgroundRestriction() {
        // Shows welcome
        assertEquals(View.VISIBLE, welcomeActivity.findViewById(R.id.btn_next).getVisibility());
        assertEquals(View.GONE, welcomeActivity.findViewById(R.id.done).getVisibility());
        assertTrue(welcomeActivity.findViewById(R.id.btn_next).isEnabled());
        clickOn(welcomeActivity.findViewById(R.id.btn_next));

        // Shows Done
        assertEquals(View.GONE, welcomeActivity.findViewById(R.id.btn_next).getVisibility());
        assertEquals(View.VISIBLE, welcomeActivity.findViewById(R.id.done).getVisibility());
        assertFalse(welcomeActivity.findViewById(R.id.btn_next).isEnabled());
        assertTrue(welcomeActivity.findViewById(R.id.done).isEnabled());
        clickOn(welcomeActivity.findViewById(R.id.done));

        Intent expectedIntent = new Intent(welcomeActivity, MapActivity.class);
        Intent actualIntent = ShadowApplication.getInstance().getNextStartedActivity();
        assertEquals(expectedIntent.getComponent(), actualIntent.getComponent());
    }
}
