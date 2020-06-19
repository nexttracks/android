package org.nexttracks.android.ui.status;

import org.nexttracks.android.services.MessageProcessor;
import org.nexttracks.android.ui.base.view.MvvmView;
import org.nexttracks.android.ui.base.viewmodel.MvvmViewModel;

import java.util.Date;

public interface StatusMvvm {

    interface View extends MvvmView {
    }

    interface ViewModel<V extends MvvmView> extends MvvmViewModel<V> {
        String getEndpointState();
        String getEndpointMessage();
        String getEndpointQueue();

        String getLocationUpdated();
        String getServiceStarted();
        String getDozeWhitelisted();
    }
}
