package br.fav.alarme.android;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Main extends Activity {
	
	private BluetoothAdapter mBluetoothAdapter;
	private final List<BluetoothDevice> lstDevices = new ArrayList<BluetoothDevice>();
	private BluetoothDevice alarmeDevice;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        final EditText txtDeviceName = (EditText) findViewById(R.id.txtDeviceName);
        
        final Button btnScan = (Button) findViewById(R.id.btnScan);
        btnScan.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
				if (!mBluetoothAdapter.isEnabled()) {
				    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				    startActivityForResult(enableBtIntent, 3);
				} else {
					IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
					registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
					mBluetoothAdapter.startDiscovery();
					while(mBluetoothAdapter.isDiscovering()) {
						try {
							synchronized(this) {
								Toast.makeText(btnScan.getContext(), "Procurando dispositivos, aguarde.", Toast.LENGTH_SHORT).show();	
								this.wait(1000);
							}
						} catch (InterruptedException e) {
							Toast.makeText(btnScan.getContext(), "Erro procurar dispositivos!", Toast.LENGTH_SHORT).show();
						}
					}
					for(BluetoothDevice device : lstDevices) {
						if(txtDeviceName.getText().toString().equals(device.getName())) {
							alarmeDevice = device;
							break;
						} else {
							alarmeDevice = null;
						}
					}
					if(alarmeDevice != null) {
						new ConnectThread(alarmeDevice).start();
					}
				}
			}
		});
    }
    
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);   
                lstDevices.add(device);
            }
        }
    };
    
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            } catch (IOException e) { }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }
            
        }
    }
}