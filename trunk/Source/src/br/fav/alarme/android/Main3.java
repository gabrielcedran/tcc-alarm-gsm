package br.fav.alarme.android;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Main3 extends Activity {

	private LocationManager locationManager;
	private static Context context;

	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		final EditText txtDeviceName = (EditText) findViewById(R.id.txtDeviceName);

		context = this;
		
		final Button btnScan = (Button) findViewById(R.id.btnScan);
		btnScan.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {
	
				locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE); 
		        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, networkLocationListener);
		        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, gpsLocationListener);
		        
		        /*locationManager.removeUpdates(networkLocationListener);
		        locationManager.removeUpdates(gpsLocationListener);*/
			}
		});
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

            Toast.makeText(context, "New GPS location: "
                    + String.format("%9.6f", location.getLatitude()) + ", "
                    + String.format("%9.6f", location.getLongitude()), Toast.LENGTH_LONG).show();

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
        	
        	Toast.makeText(context, "New GPS location: "
                    + String.format("%9.6f", location.getLatitude()) + ", "
                    + String.format("%9.6f", location.getLongitude()), Toast.LENGTH_LONG).show();

        }
    };

}