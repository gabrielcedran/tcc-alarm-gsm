package br.fav;

import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Main extends Activity {
	
	private BluetoothAdapter btAdapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button btnConnectToPic = (Button) findViewById(R.id.btn_connect_pic);
        btnConnectToPic.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				btAdapter = BluetoothAdapter.getDefaultAdapter();
				doDiscovery();
				v.setVisibility(View.GONE);
				// Get a set of currently paired devices
		        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
		        for (BluetoothDevice device : pairedDevices) {
	                //mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
	            }
			}
		});
    }
    
    /**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery() {

        // Indicate scanning in the title
        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.scanning);

        // Turn on sub-title for new devices
        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        // If we're already discovering, stop it
        if (btAdapter.isDiscovering()) {
            btAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        btAdapter.startDiscovery();
    }
}