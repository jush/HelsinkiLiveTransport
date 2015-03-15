package org.jush.helsinkilivetransport;

import android.util.Log;

import org.jush.helsinkilivetransport.api.RealTimeVehicles;
import org.jush.helsinkilivetransport.api.RealTimeVehiclesApi;
import org.jush.helsinkilivetransport.api.VehicleMonitoringDelivery;

import java.util.TimerTask;

import retrofit.RestAdapter;

class FetchRealTimeVehiclesTask extends TimerTask {
    private RealTimeActivity realTimeActivity;
    public final String TAG = RealTimeActivity.class.getSimpleName();

    public FetchRealTimeVehiclesTask(RealTimeActivity realTimeActivity) {
        this.realTimeActivity = realTimeActivity;
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
        realTimeActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                realTimeActivity.onUpdateVehiclePositions(vehicleMonitoringDelivery
                        .getVehicleActivities());
            }
        });
    }
}
