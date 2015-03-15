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

            @SerializedName("LineRef")
            private LineRef lineRef;

            public VehicleLocation getVehicleLocation() {
                return vehicleLocation;
            }

            public LineRef getLineRef() {
                return lineRef;
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

            public static class LineRef {
                @SerializedName("value")
                private String value;

                public String getValue() {
                    return value;
                }

                public String getUserFriendlineLine() {
                    return parseRouteId(value);
                }

                private static String parseRouteId(String value) {
                    if (value.startsWith("1019")) {
                        return "Ferry";
                    } else if (value.startsWith("1300")) {
                        return "Subway";
                    } else if (value.startsWith("300")) {
                        return "Rail " + value.substring(4, 5);
                    } else if (value.startsWith("100") || value.startsWith("1010")) {
                        return "Tram " + value.replaceAll("^.0*", "");
                    } else if (value.charAt(0) == '1' || value.charAt(0) == '2' || value.charAt
                            (0) == '4') { // if starts with 1,2 or 4
                        return "Bus " + value.replaceAll("^.0*", "");
                    }
                    return null;
                }
            }
        }
    }
}
