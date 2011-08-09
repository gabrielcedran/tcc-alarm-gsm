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
	private Boolean rastreando = false;
	
	public GPSManager(Service service) {
		this.service = service;
		settings = service.getSharedPreferences("AlarmeAndroid", 0);
	}

	public void rastrearUmaVez() {
		
		locationManager = (LocationManager) service.getSystemService(Context.LOCATION_SERVICE); 
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, gpsLocationListenerUmaVez,
        		Looper.getMainLooper());
	}
	
	public void rastrear() {
		locationManager = (LocationManager) service.getSystemService(Context.LOCATION_SERVICE); 
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, networkLocationListener,
        		Looper.getMainLooper());
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, gpsLocationListener,
        		Looper.getMainLooper());
        rastreando = true;
        
	}
	
	public void cancelarRastreamento() {
		if(rastreando) {
			locationManager.removeUpdates(networkLocationListener);
	        locationManager.removeUpdates(gpsLocationListener);
		}
        rastreando = false;
        
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

        	CoordinatesSender.enviarCoordenadas(String.valueOf(location.getLatitude()), 
            	String.valueOf(location.getLongitude()), settings.getString("idCarro", ""), settings.getString("serverName", ""));
            
            locationManager.removeUpdates(gpsLocationListenerUmaVez);
        }
    };
    
}
