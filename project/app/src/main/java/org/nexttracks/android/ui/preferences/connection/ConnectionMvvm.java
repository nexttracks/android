package org.nexttracks.android.ui.preferences.connection;

import org.nexttracks.android.ui.base.view.MvvmView;
import org.nexttracks.android.ui.base.viewmodel.MvvmViewModel;
import org.nexttracks.android.ui.preferences.connection.dialog.ConnectionHostHttpDialogViewModel;
import org.nexttracks.android.ui.preferences.connection.dialog.ConnectionHostMqttDialogViewModel;
import org.nexttracks.android.ui.preferences.connection.dialog.ConnectionIdentificationViewModel;
import org.nexttracks.android.ui.preferences.connection.dialog.ConnectionModeDialogViewModel;
import org.nexttracks.android.ui.preferences.connection.dialog.ConnectionParametersViewModel;
import org.nexttracks.android.ui.preferences.connection.dialog.ConnectionSecurityViewModel;


public interface ConnectionMvvm {

    interface View extends MvvmView {
        void showModeDialog();
        void showHostDialog();
        void showIdentificationDialog();
        void showSecurityDialog();
        void showParametersDialog();
        void recreateOptionsMenu();

        }

    interface ViewModel<V extends MvvmView> extends MvvmViewModel<V> {
        void onModeClick();
        void onHostClick();
        void onIdentificationClick();
        void onSecurityClick();
        void onParametersClick();

        int getModeId();
        void setModeId(int newModeId);

        ConnectionHostMqttDialogViewModel getHostDialogViewModelMqtt();
        ConnectionHostHttpDialogViewModel getHostDialogViewModelHttp();
        ConnectionModeDialogViewModel getModeDialogViewModel();
        ConnectionIdentificationViewModel getIdentificationDialogViewModel();
        ConnectionSecurityViewModel getConnectionSecurityViewModel();
        ConnectionParametersViewModel getConnectionParametersViewModel();
    }
}
