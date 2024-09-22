package bsu.meneses.it304busreservationandtracking;

public class CustomLocation {
    private double latitude;
    private double longitude;
    private float altitude;
    private float accuracy;
    private float speed;

    // No-argument constructor
    public CustomLocation() {}

    public CustomLocation(double latitude, double longitude, float altitude, float accuracy, float speed) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.accuracy = accuracy;
        this.speed = speed;
    }

    // Getters and Setters
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public float getAltitude() { return altitude; }
    public void setAltitude(float altitude) { this.altitude = altitude; }

    public float getAccuracy() { return accuracy; }
    public void setAccuracy(float accuracy) { this.accuracy = accuracy; }

    public float getSpeed() { return speed; }
    public void setSpeed(float speed) { this.speed = speed; }
}
