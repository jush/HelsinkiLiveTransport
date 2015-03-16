package org.jush.helsinkilivetransport;

import android.util.Log;

import org.jush.helsinkilivetransport.api.RealTimeVehicles;
import org.jush.helsinkilivetransport.api.RealTimeVehiclesApi;
import org.jush.helsinkilivetransport.api.VehicleMonitoringDelivery;

import java.util.TimerTask;

import de.greenrobot.event.EventBus;
import retrofit.RestAdapter;

class FetchRealTimeVehiclesTask extends TimerTask {
    public final String TAG = FetchRealTimeVehiclesTask.class.getSimpleName();

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
        EventBus.getDefault().post(vehicleMonitoringDelivery.getVehicleActivities());
    }
}
