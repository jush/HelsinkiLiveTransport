package org.jush.helsinkilivetransport.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VehicleMonitoringDelivery {
    @SerializedName("VehicleActivity")
    private List<VehicleActivity> vehicleActivities;

    public List<VehicleActivity> getVehicleActivities() {
        return vehicleActivities;
    }

    public static class VehicleActivity {
        @SerializedName("MonitoredVehicleJourney")
        private MonitoredVehicleJourney monitoredVehicleJourney;

        public MonitoredVehicleJourney getMonitoredVehicleJourney() {
            return monitoredVehicleJourney;
        }

        public static class MonitoredVehicleJourney {
            @SerializedName("VehicleLocation")
            private VehicleLocation vehicleLocation;

            public VehicleLocation getVehicleLocation() {
                return vehicleLocation;
            }

            public static class VehicleLocation {
                @SerializedName("Longitude")
                private double longitude;
                @SerializedName("Latitude")
                private double latitude;

                public double getLatitude() {
                    return latitude;
                }

                public double getLongitude() {
                    return longitude;
                }
            }
        }
    }
}
