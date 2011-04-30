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
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Main extends Activity {

	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothSocket mmSocket;
	private final List<BluetoothDevice> lstDevices = new ArrayList<BluetoothDevice>();
	private OutputStream outStream = null;
	private BluetoothDevice alarmeDevice;
	private BluetoothSocket btSocket = null;
	// Well known SPP UUID (will *probably* map to RFCOMM channel 1 (default) if
	// not in use);
	// see comments in onResume().
	private static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");

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
					Intent enableBtIntent = new Intent(
							BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivityForResult(enableBtIntent, 3);
				} else {
					IntentFilter filter = new IntentFilter(
							BluetoothDevice.ACTION_FOUND);
					registerReceiver(mReceiver, filter);

					mBluetoothAdapter.startDiscovery();
					while (mBluetoothAdapter.isDiscovering()) {
						try {
							synchronized (this) {
								Toast.makeText(btnScan.getContext(),
										"Procurando dispositivos, aguarde.",
										Toast.LENGTH_SHORT).show();
								this.wait(1000);
							}
						} catch (InterruptedException e) {
							Toast.makeText(btnScan.getContext(),
									"Erro procurar dispositivos!",
									Toast.LENGTH_SHORT).show();
						}
					}

					filter = new IntentFilter(
							BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
					registerReceiver(mReceiver, filter);

					for (BluetoothDevice device : lstDevices) {
						if (txtDeviceName.getText().toString()
								.equals(device.getName())) {
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
							if (outStream != null)
								outStream.write(1);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							outStream = null;
							e.printStackTrace();
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