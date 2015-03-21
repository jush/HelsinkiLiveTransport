package org.jush.helsinkilivetransport;

import android.util.Log;

import org.jush.helsinkilivetransport.api.RealTimeVehicles;
import org.jush.helsinkilivetransport.api.RealTimeVehiclesApi;
import org.jush.helsinkilivetransport.api.VehicleMonitoringDelivery;

import java.util.TimerTask;

import de.greenrobot.event.EventBus;
import retrofit.RestAdapter;
import retrofit.RetrofitError;

class FetchRealTimeVehiclesTask extends TimerTask {
    public final String TAG = FetchRealTimeVehiclesTask.class.getSimpleName();

    @Override
    public void run() {
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://dev.hsl.fi/")
                .build();
        RealTimeVehiclesApi service = restAdapter.create(RealTimeVehiclesApi.class);
        try {
            RealTimeVehicles realTimeVehicles = service.fetchRealTimeVehicles();
            if (realTimeVehicles == null) {
                Log.w(TAG, "No answer received");
                return;
            }
            VehicleMonitoringDelivery vehicleMonitoringDelivery = realTimeVehicles.getSiri()
                    .getServiceDelivery()
                    .getVehicleMonitoringDeliveries()
                    .get(0);
            EventBus.getDefault().post(vehicleMonitoringDelivery.getVehicleActivities());
        } catch (RetrofitError e) {
            Log.w(TAG, "Error while fetching data", e);
        }
    }
}
