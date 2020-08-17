package org.nexttracks.android.ui.regions;

import org.nexttracks.android.data.WaypointModel;
import org.nexttracks.android.data.repos.WaypointsRepo;
import org.nexttracks.android.injection.scopes.PerActivity;
import org.nexttracks.android.services.LocationProcessor;
import org.nexttracks.android.ui.base.viewmodel.BaseViewModel;

import javax.inject.Inject;

import io.objectbox.query.Query;
import timber.log.Timber;

@PerActivity
public class RegionsViewModel extends BaseViewModel<RegionsMvvm.View> implements RegionsMvvm.ViewModel<RegionsMvvm.View> {
    private final LocationProcessor locationProcessor;
    private WaypointsRepo waypointsRepo;

    @Inject
    public RegionsViewModel(WaypointsRepo waypointsRepo, LocationProcessor locationProcessor) {
        super();
        Timber.v("new vm instantiated");
        this.waypointsRepo = waypointsRepo;
        this.locationProcessor = locationProcessor;
    }

    public Query<WaypointModel> getWaypointsList() {
        return this.waypointsRepo.getAllQuery();
    }

    @Override
    public void delete(WaypointModel model) {
        waypointsRepo.delete(model);
    }

    @Override
    public void exportWaypoints() {
        locationProcessor.publishWaypointsMessage();
    }
}

