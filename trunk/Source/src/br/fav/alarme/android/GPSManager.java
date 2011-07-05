package br.fav.alarme.android;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

public class GPSManager {

	private LocationManager locationManager;
	private Activity activity;
	
	public GPSManager(Activity activity) {
		this.activity = activity;
	}
	
	public void rastrearUmaVez() {
		locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE); 
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, gpsLocationListenerUmaVez);
	}
	
	public void rastrear() {
		
		locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE); 
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, networkLocationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, gpsLocationListener);
        
	}
	
	public void cancelarRastreamento() {
		
		locationManager.removeUpdates(networkLocationListener);
        locationManager.removeUpdates(gpsLocationListener);
        
	}
	
	private final LocationListener gpsLocationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
            case LocationProvider.AVAILABLE:
                break;
            case LocationProvider.OUT_OF_SERVICE:
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                break;
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
            
        }

        @Override
        public void onProviderDisabled(String provider) {
            
        }

        @Override
        public void onLocationChanged(Location location) {
            locationManager.removeUpdates(networkLocationListener);
            
            //location.getLatitude();
            //location.getLongitude();
            
        }
    };

    private final LocationListener networkLocationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
            case LocationProvider.AVAILABLE:
                break;
            case LocationProvider.OUT_OF_SERVICE:
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                break;
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
          
        }

        @Override
        public void onProviderDisabled(String provider) {
        
        }

        @Override
        public void onLocationChanged(Location location) {
        	
        	//location.getLatitude();
            //location.getLongitude();
            
        }
    };
    
    private final LocationListener gpsLocationListenerUmaVez = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
            case LocationProvider.AVAILABLE:
                break;
            case LocationProvider.OUT_OF_SERVICE:
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                break;
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
            
        }

        @Override
        public void onProviderDisabled(String provider) {
            
        }

        @Override
        public void onLocationChanged(Location location) {
            locationManager.removeUpdates(networkLocationListener);
            
            //location.getLatitude();
            //location.getLongitude();
            
            locationManager.removeUpdates(gpsLocationListenerUmaVez);
            
        }
    };
}
