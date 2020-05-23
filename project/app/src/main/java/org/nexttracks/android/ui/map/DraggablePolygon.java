package org.nexttracks.android.ui.map;

import android.util.TypedValue;
import android.view.MotionEvent;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;

public class DraggablePolygon extends Polygon {

    private boolean draggable;
    private double radius;
    private GeoPoint geoPoint;
    private OnPolygonDragListener onPolygonDragListener;

    public DraggablePolygon(double radius) {
        this(null, radius);
    }

    public DraggablePolygon(MapView mapView, double radius) {
        super(mapView);
        this.draggable = false;
        this.radius = radius;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event, MapView mapView) {
        if (draggable) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (onPolygonDragListener != null)
                    onPolygonDragListener.onMarkerDragEnd(this);
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                moveToEventPosition(event, mapView);
                if (onPolygonDragListener != null)
                    onPolygonDragListener.onMarkerDrag(this);
            }
        }
        return super.onTouchEvent(event, mapView);
    }

    public void moveToEventPosition(final MotionEvent event, final MapView mapView){
        float offsetY = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 0, mapView.getContext().getResources().getDisplayMetrics());
        final Projection pj = mapView.getProjection();
        this.setPosition(((GeoPoint) pj.fromPixels((int)event.getX(), (int)(event.getY()-offsetY))));
        mapView.invalidate();
    }

    public void setPosition(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
        this.setPoints(Polygon.pointsAsCircle(geoPoint, this.radius));
    }

    public void setPosition(GeoPoint geoPoint, double radius) {
        this.radius = radius;
        this.setPosition(geoPoint);
    }

    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
    }

    public void setOnPolygonDragListener(OnPolygonDragListener onPolygonDragListener) {
        this.onPolygonDragListener = onPolygonDragListener;
    }

    public OnPolygonDragListener getOnPolygonDragListener() {
        return onPolygonDragListener;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public interface OnPolygonDragListener{
        abstract void onMarkerDrag(DraggablePolygon polygon);
        abstract void onMarkerDragEnd(DraggablePolygon polygon);
        abstract void onMarkerDragStart(DraggablePolygon polygon);
    }
}
