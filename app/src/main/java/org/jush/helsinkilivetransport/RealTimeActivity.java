package org.jush.helsinkilivetransport;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import org.jush.helsinkilivetransport.api.RealTimeVehicles;
import org.jush.helsinkilivetransport.api.RealTimeVehiclesApi;
import org.jush.helsinkilivetransport.api.VehicleMonitoringDelivery;

import java.util.Timer;
import java.util.TimerTask;

import retrofit.RestAdapter;

public class RealTimeActivity extends FragmentActivity {
    private Timer timer;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_time);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        super.onPause();
    }

    private void fetchRealTimeVehicles() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new FetchRealTimeVehiclesTask(), 3000, 5000);
    }

    private class FetchRealTimeVehiclesTask extends TimerTask {
        public final String TAG = RealTimeActivity.class.getSimpleName();
        private final IconGenerator ferryIconGenerator = new IconGenerator(getApplicationContext());
        private final IconGenerator subwayIconGenerator = new IconGenerator(getApplicationContext
                ());
        private final IconGenerator railIconGenerator = new IconGenerator(getApplicationContext());
        private final IconGenerator tramIconGenerator = new IconGenerator(getApplicationContext());
        private final IconGenerator busIconGenerator = new IconGenerator(getApplicationContext());

        public FetchRealTimeVehiclesTask() {
            ferryIconGenerator.setStyle(IconGenerator.STYLE_BLUE);
            subwayIconGenerator.setStyle(IconGenerator.STYLE_ORANGE);
            railIconGenerator.setStyle(IconGenerator.STYLE_RED);
            tramIconGenerator.setStyle(IconGenerator.STYLE_GREEN);
            busIconGenerator.setStyle(IconGenerator.STYLE_BLUE);
        }

        @Override
        public void run() {
            RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://dev.hsl.fi/")
                    .build();
            RealTimeVehiclesApi service = restAdapter.create(RealTimeVehiclesApi.class);
            RealTimeVehicles realTimeVehicles = service.fetchRealTimeVehicles();
            Log.d(TAG, "Result: " + realTimeVehicles);
            final VehicleMonitoringDelivery vehicleMonitoringDelivery = realTimeVehicles.getSiri()
                    .getServiceDelivery()
                    .getVehicleMonitoringDeliveries()
                    .get(0);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onPostExecute(vehicleMonitoringDelivery);
                }
            });
        }

        protected void onPostExecute(VehicleMonitoringDelivery vehicleMonitoringDelivery) {
            // Clear the map
            mMap.clear();
            for (VehicleMonitoringDelivery.VehicleActivity vehicleActivity :
                    vehicleMonitoringDelivery
                    .getVehicleActivities()) {
                VehicleMonitoringDelivery.VehicleActivity.MonitoredVehicleJourney
                        monitoredVehicleJourney = vehicleActivity
                        .getMonitoredVehicleJourney();
                VehicleMonitoringDelivery.VehicleActivity.MonitoredVehicleJourney.VehicleLocation
                        vehicleLocation = monitoredVehicleJourney
                        .getVehicleLocation();
                VehicleMonitoringDelivery.VehicleActivity.MonitoredVehicleJourney.LineRef
                        .LineInformation lineInformation = monitoredVehicleJourney
                        .getLineRef()
                        .getUserFriendlineLine();
                if (lineInformation.getType() == VehicleMonitoringDelivery.VehicleActivity
                        .MonitoredVehicleJourney.LineRef.LineInformation.LineType.UNKNOWN) {
                    continue;
                }
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
                mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(icon))
                        .position(new LatLng(vehicleLocation.getLatitude(), vehicleLocation
                                .getLongitude())));
            }
        }
    }
}
