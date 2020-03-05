package org.nexttracks.android.robolectric;

import org.greenrobot.eventbus.EventBus;
import org.nexttracks.android.data.WaypointModel;
import org.nexttracks.android.data.repos.WaypointsRepo;

import java.util.List;

import io.objectbox.android.ObjectBoxLiveData;
import io.objectbox.query.Query;

public class DummyWaypointsRepo extends WaypointsRepo {

    public DummyWaypointsRepo(EventBus eventBus) {
        super(eventBus);
    }

    @Override
    public WaypointModel get(long tst) {
        return null;
    }

    @Override
    public List<WaypointModel> getAll() {
        return null;
    }

    @Override
    public List<WaypointModel> getAllWithGeofences() {
        return null;
    }

    @Override
    public ObjectBoxLiveData<WaypointModel> getAllLive() {
        return null;
    }

    @Override
    public Query<WaypointModel> getAllQuery() {
        return null;
    }

    @Override
    public void insert_impl(WaypointModel w) {

    }

    @Override
    public void update_impl(WaypointModel w) {

    }

    @Override
    public void delete_impl(WaypointModel w) {

    }

}
