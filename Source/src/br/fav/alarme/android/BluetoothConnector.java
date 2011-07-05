package br.fav.alarme.android;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

public class BluetoothConnector extends Activity {

	// API
	private BluetoothAdapter mBluetoothAdapter;
	// API
	private BluetoothSocket mmSocket;
	// API
	private final List<BluetoothDevice> lstDevices = new ArrayList<BluetoothDevice>();
	// API
	private OutputStream outStream = null;
	// API
	private BluetoothDevice alarmeDevice;
	// API
	private BluetoothSocket btSocket = null;
	// Nome do dispositivo bluetooth utilizado pelo PIC
	private String PIC = "";
	// Flag que controla se os dados foram enviados para o PIC com sucesso
	private boolean dataSent = false;
	// Well known SPP UUID (will *probably* map to RFCOMM channel 1 (default) if
	// not in use);
	// see comments in onResume().
	private static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	public void sendMessageToPic() {
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (!mBluetoothAdapter.isEnabled()) {
			dataSent = false;
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, 3);
		} else {
			dataSent = false;
			IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
			registerReceiver(mReceiver, filter);

			mBluetoothAdapter.startDiscovery();
			while (mBluetoothAdapter.isDiscovering()) {
				try {
					synchronized (this) {
						this.wait(1000);
					}
				} catch (InterruptedException e) {
					// do nothing
				}
			}

			filter = new IntentFilter(
					BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
			registerReceiver(mReceiver, filter);

			for (BluetoothDevice device : lstDevices) {
				if (PIC.equals(device.getName())) {
					alarmeDevice = device;
					break;
				} else {
					alarmeDevice = null;
				}
			}
			if (alarmeDevice != null) {
				connect();
				int i = 0;
				while (outStream == null && i < 3) {
					synchronized (this) {
						try {
							this.wait(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					i++;
				}
				try {
					if (outStream != null) {
						outStream.write(1);
						dataSent = true;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					outStream = null;
					e.printStackTrace();
				}
			}
		}
		
		if(!dataSent) {
			sendMessageToPic();
		}
	}
	
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// When discovery finds a device
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				BluetoothDevice deviceFound = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				for (BluetoothDevice device : lstDevices) {
					if (deviceFound.getAddress().equals(device.getAddress())) {
						return;
					}
				}

				lstDevices.add(deviceFound);
			}
		}
	};

	private void connect() {
		BluetoothSocket tmp = null;
		try {
			Method m = alarmeDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class});          
			tmp = (BluetoothSocket) m.invoke(alarmeDevice, 1); 
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		btSocket = tmp;
		mBluetoothAdapter.cancelDiscovery();
		try {
			btSocket.connect();
			outStream = btSocket.getOutputStream();
		} catch (IOException e) {
			try {
				btSocket.close();
			} catch (IOException e2) {
			}
			return;
		}
	}
}
