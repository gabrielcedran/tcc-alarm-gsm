package br.fav.alarme.android;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Looper;

public class PICManager {

	// API
	private BluetoothAdapter mBluetoothAdapter;
	// API
	/*private BluetoothSocket mmSocket;*/
	// API
	private final List<BluetoothDevice> lstDevices = new ArrayList<BluetoothDevice>();
	// API
	private OutputStream outStream = null;
	// API
	private BluetoothDevice alarmeDevice;
	// API
	private BluetoothSocket btSocket = null;
	// Flag que controla se os dados foram enviados para o PIC com sucesso
	private boolean dataSent = false;
	// Flag que controla as tentativas de enviar os dados para o pic
	private int tentativas = 0;
	// Well known SPP UUID (will *probably* map to RFCOMM channel 1 (default) if
	// not in use);
	/*private static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");*/
	
	private Service service;
	
	private SharedPreferences settings;
	
	public PICManager(Service service) {
		this.service = service;
		settings = service.getSharedPreferences("AlarmeAndroid", 0);
	}
	
	public boolean enviarMessagemParaOPic(Integer codigo) {
		if(tentativas==0)
			Looper.prepare();
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (!mBluetoothAdapter.isEnabled()) {
			dataSent = false;
			tentativas++;
			// Intent enableBtIntent = new Intent(
			//		BluetoothAdapter.ACTION_REQUEST_ENABLE);
			// activity.startActivityForResult(enableBtIntent, 3);
		} else {
			tentativas++;
			dataSent = false;
			IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
			service.registerReceiver(mReceiver, filter);

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
			service.registerReceiver(mReceiver, filter);

			for (BluetoothDevice device : lstDevices) {
				if (settings.getString("picName", "").equals(device.getName())) {
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
						outStream.write(codigo);
						dataSent = true;
					}
				} catch (IOException e) {
					outStream = null;
					e.printStackTrace();
				}
			}
		}
		
		if(!dataSent && tentativas <= 3) {
			enviarMessagemParaOPic(codigo);
		} 
		tentativas = 0;
		if(tentativas > 3) {
			return false;
		}
		return true;
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
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
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
