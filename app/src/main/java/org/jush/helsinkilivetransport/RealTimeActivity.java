package org.jush.helsinkilivetransport;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import org.jush.helsinkilivetransport.api.VehicleMonitoringDelivery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import de.greenrobot.event.EventBus;

public class RealTimeActivity extends FragmentActivity {
    private static final int INITIAL_DELAY = 3000;
    private static final int REPEAT_PERIOD = 5000;
    private final Map<String, Marker> currentMarkers = new HashMap<>();
    private Timer timer;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private IconGenerator ferryIconGenerator;
    private IconGenerator subwayIconGenerator;
    private IconGenerator railIconGenerator;
    private IconGenerator tramIconGenerator;
    private IconGenerator busIconGenerator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_time);

        ferryIconGenerator = new IconGenerator(this);
        ferryIconGenerator.setStyle(IconGenerator.STYLE_BLUE);
        subwayIconGenerator = new IconGenerator(this);
        subwayIconGenerator.setStyle(IconGenerator.STYLE_ORANGE);
        railIconGenerator = new IconGenerator(this);
        railIconGenerator.setStyle(IconGenerator.STYLE_RED);
        tramIconGenerator = new IconGenerator(this);
        tramIconGenerator.setStyle(IconGenerator.STYLE_GREEN);
        busIconGenerator = new IconGenerator(this);
        busIconGenerator.setStyle(IconGenerator.STYLE_BLUE);

        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        setUpMapIfNeeded();
        if (mMap != null) {
            fetchRealTimeVehicles();
        }
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
    }

    @Override
    protected void onPause() {
        if (timer != null) {
            timer.cancel();
        }
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    private void fetchRealTimeVehicles() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new FetchRealTimeVehiclesTask(), INITIAL_DELAY, REPEAT_PERIOD);
    }

    /**
     * Called from {@link FetchRealTimeVehiclesTask} through EventBus
     *
     * @param vehicleActivities
     */
    @SuppressWarnings("unused")
    public void onEventMainThread(List<VehicleMonitoringDelivery.VehicleActivity>
                                          vehicleActivities) {
        for (VehicleMonitoringDelivery.VehicleActivity vehicleActivity : vehicleActivities) {
            VehicleMonitoringDelivery.VehicleActivity.MonitoredVehicleJourney
                    monitoredVehicleJourney = vehicleActivity
                    .getMonitoredVehicleJourney();
            VehicleMonitoringDelivery.VehicleActivity.MonitoredVehicleJourney.VehicleLocation
                    vehicleLocation = monitoredVehicleJourney
                    .getVehicleLocation();
            VehicleMonitoringDelivery.VehicleActivity.MonitoredVehicleJourney.LineRef
                    .LineInformation lineInformation = monitoredVehicleJourney
                    .getLineRef()
                    .getUserFriendlyLine();
            if (lineInformation.getType() == VehicleMonitoringDelivery.VehicleActivity
                    .MonitoredVehicleJourney.LineRef.LineInformation.LineType.UNKNOWN) {
                continue;
            }

            LatLng vehicleLatLng = new LatLng(vehicleLocation.getLatitude(), vehicleLocation
                    .getLongitude());

            String vehicleId = monitoredVehicleJourney.getVehicleRef().getValue();
            // Check if we already have a marker for the vehicle
            Marker vehicleMarker = currentMarkers.get(vehicleId);
            if (vehicleMarker != null) {
                // If we do have it then just update the position
                vehicleMarker.setPosition(vehicleLatLng);
            } else {
                // If not then create a new marker
                String lineId = lineInformation.getId();
                Bitmap icon;
                switch (lineInformation.getType()) {
                    case FERRY:
                        icon = ferryIconGenerator.makeIcon(lineId);
                        break;
                    case SUBWAY:
                        icon = subwayIconGenerator.makeIcon(lineId);
                        break;
                    case RAIL:
                        icon = railIconGenerator.makeIcon(lineId);
                        break;
                    case TRAM:
                        icon = tramIconGenerator.makeIcon(lineId);
                        break;
                    case BUS:
                    default:
                        icon = busIconGenerator.makeIcon(lineId);
                        break;
                }
                vehicleMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory
                        .fromBitmap(icon))
                        .position(vehicleLatLng));
                currentMarkers.put(vehicleId, vehicleMarker);
            }
        }
    }

}
