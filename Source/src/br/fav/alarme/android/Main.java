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
						try {
							BluetoothSocket bluetoothSocket = alarmeDevice.createRfcommSocketToServiceRecord(UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66"));
							bluetoothSocket.getOutputStream().write(1);
						} catch (IOException e) {
							Toast.makeText(btnScan.getContext(), "Erro ao parear!", Toast.LENGTH_SHORT).show();
						}
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
}