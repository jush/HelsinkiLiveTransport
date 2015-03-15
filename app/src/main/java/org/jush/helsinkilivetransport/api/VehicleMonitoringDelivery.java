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

            @SerializedName("VehicleRef")
            private VehicleRef vehicleRef;

            public VehicleLocation getVehicleLocation() {
                return vehicleLocation;
            }

            public LineRef getLineRef() {
                return lineRef;
            }

            public VehicleRef getVehicleRef() {
                return vehicleRef;
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

                private static LineInformation parseRouteId(String value) {
                    if (value.startsWith("1019")) {
                        return new LineInformation(LineInformation.LineType.FERRY, "Ferry");
                    } else if (value.startsWith("1300")) {
                        return new LineInformation(LineInformation.LineType.SUBWAY, value
                                .substring(4, 5));
                    } else if (value.startsWith("300")) {
                        return new LineInformation(LineInformation.LineType.RAIL, value.substring
                                (4, 5));
                    } else if (value.startsWith("100") || value.startsWith("1010")) {
                        return new LineInformation(LineInformation.LineType.TRAM, value
                                .replaceAll("^.0*", ""));
                    } else if (value.charAt(0) == '1' || value.charAt(0) == '2' || value.charAt
                            (0) == '4') { // if starts with 1,2 or 4
                        return new LineInformation(LineInformation.LineType.BUS, value.replaceAll
                                ("^.0*", ""));
                    }
                    return new LineInformation(LineInformation.LineType.UNKNOWN, "");
                }

                public String getValue() {
                    return value;
                }

                public LineInformation getUserFriendlineLine() {
                    return parseRouteId(value);
                }

                public static class LineInformation {
                    private final LineType type;
                    private final String id;

                    public LineInformation(LineType type, String id) {
                        this.type = type;
                        this.id = id;
                    }

                    @Override
                    public String toString() {
                        return String.format("%s - %s", type.name(), id);
                    }

                    public String getId() {
                        return id;
                    }

                    public LineType getType() {
                        return type;
                    }

                    public enum LineType {
                        FERRY,
                        SUBWAY,
                        RAIL,
                        TRAM,
                        BUS,
                        UNKNOWN,
                    }
                }
            }

            public static class VehicleRef {
                @SerializedName("value")
                private String value;

                public String getValue() {
                    return value;
                }
            }
        }
    }
}
