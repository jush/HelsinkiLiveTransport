package org.jush.helsinkilivetransport.api;

import retrofit.http.GET;

public interface RealTimeVehiclesApi {
    @GET("/siriaccess/vm/json")
    RealTimeVehicles fetchRealTimeVehicles();
}
