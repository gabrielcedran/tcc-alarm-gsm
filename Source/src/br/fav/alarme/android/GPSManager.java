package br.fav.alarme.android;

import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Looper;

public class GPSManager {

	private LocationManager locationManager;
	private Service service;
	private SharedPreferences settings;
	
	public GPSManager(Service service) {
		this.service = service;
		settings = service.getSharedPreferences("AlarmeAndroid", 0);
	}
	
	public void rastrear() {
		locationManager = (LocationManager) service.getSystemService(Context.LOCATION_SERVICE); 
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, networkLocationListener,
        		Looper.getMainLooper());
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, gpsLocationListener,
        		Looper.getMainLooper());
        
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

            CoordinatesSender.enviarCoordenadas(String.valueOf(location.getLatitude()), 
            		String.valueOf(location.getLongitude()), settings.getString("idCarro", ""), settings.getString("serverName", ""));
            
        	locationManager.removeUpdates(networkLocationListener);
        	locationManager.removeUpdates(gpsLocationListener);
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
        	
        	CoordinatesSender.enviarCoordenadas(String.valueOf(location.getLatitude()), 
            	String.valueOf(location.getLongitude()), settings.getString("idCarro", ""), settings.getString("serverName", ""));

        	locationManager.removeUpdates(networkLocationListener);
        	locationManager.removeUpdates(gpsLocationListener);
            
        }
    };
   
}
