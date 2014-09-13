package main;;

public class Coordinates {

	private double latitude, longitude;
	private int id;

	public Coordinates(double lat, double lng, int i) {
		this.latitude = lat;
		this.longitude = lng;
		this.id = i+1;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public int getId() {
		return id;
	}

	public void setId(int i) {
		this.id = i;
	}

}
