package org.nexttracks.android.ui.status;

import org.nexttracks.android.services.MessageProcessor;
import org.nexttracks.android.ui.base.view.MvvmView;
import org.nexttracks.android.ui.base.viewmodel.MvvmViewModel;

import java.util.Date;

public interface StatusMvvm {

    interface View extends MvvmView {
    }

    interface ViewModel<V extends MvvmView> extends MvvmViewModel<V> {
        MessageProcessor.EndpointState getEndpointState();
        String getEndpointMessage();
        int getEndpointQueue();

        long getLocationUpdated();
        Date getServiceStarted();
        String getDozeWhitelisted();
    }
}
