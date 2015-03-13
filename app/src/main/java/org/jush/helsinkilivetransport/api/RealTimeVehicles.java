package org.jush.helsinkilivetransport.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RealTimeVehicles {
    @SerializedName("Siri")
    private Siri siri;

    public Siri getSiri() {
        return siri;
    }

    public static class Siri {
        @SerializedName("ServiceDelivery")
        private ServiceDelivery serviceDelivery;

        public ServiceDelivery getServiceDelivery() {
            return serviceDelivery;
        }

        public static class ServiceDelivery {
            @SerializedName("VehicleMonitoringDelivery")
            private List<VehicleMonitoringDelivery> vehicleMonitoringDeliveries;

            public List<VehicleMonitoringDelivery> getVehicleMonitoringDeliveries() {
                return vehicleMonitoringDeliveries;
            }
        }
    }
}
