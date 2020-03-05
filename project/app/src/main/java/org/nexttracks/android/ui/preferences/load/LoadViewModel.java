package org.nexttracks.android.ui.preferences.load;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.nexttracks.android.data.repos.WaypointsRepo;
import org.nexttracks.android.injection.qualifier.AppContext;
import org.nexttracks.android.injection.scopes.PerActivity;
import org.nexttracks.android.messages.MessageConfiguration;
import org.nexttracks.android.support.Parser;
import org.nexttracks.android.support.Preferences;
import org.nexttracks.android.ui.base.viewmodel.BaseViewModel;

import java.io.IOException;

import javax.inject.Inject;

import timber.log.Timber;


@PerActivity
public class LoadViewModel extends BaseViewModel<LoadMvvm.View> implements LoadMvvm.ViewModel<LoadMvvm.View> {
    private final Preferences preferences;
    private final Parser parser;
    private final WaypointsRepo waypointsRepo;

    private MessageConfiguration configuration;

    @Inject
    public LoadViewModel(@AppContext Context context, Preferences preferences, Parser parser, WaypointsRepo waypointsRepo) {
        this.preferences = preferences;
        this.parser = parser;
        this.waypointsRepo = waypointsRepo;
    }

    public void attachView(@NonNull LoadMvvm.View view, @Nullable Bundle savedInstanceState) {
        super.attachView(view, savedInstanceState);
    }


    public String setConfiguration(String json) throws IOException, Parser.EncryptionException {
        Timber.v("%s", json);

        this.configuration = (MessageConfiguration) parser.fromJson(json.getBytes());

        Timber.v("hasWaypoints: %s / #%s", configuration.hasWaypoints(), configuration.getWaypoints().size());
        return parser.toJsonPlainPretty(this.configuration);

    }



    public void saveConfiguration() {
        preferences.importFromMessage(configuration);

        if(configuration.hasWaypoints()) {
            waypointsRepo.importFromMessage(configuration.getWaypoints());
        }

        getView().showFinishDialog();
    }

    @Override
    public boolean hasConfiguration() {
        return this.configuration != null;
    }
}
