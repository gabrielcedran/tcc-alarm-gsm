/*
 * Copyright (C) 2010 Eirik Taylor
 *
 * This work is licensed under a Creative Commons Attribution-Noncommercial-Share Alike 3.0 Unported License.
 * See the following website for more information: 
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 * 
 */

package com.uzzors2k.blu_car;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class blu_car extends Activity { 
	
	// Constants for Touch events
	protected static final int ACTION_DOWN = 0;
	protected static final int ACTION_UP = 1;
	protected static final int ACTION_MOVE = 2;
	
	// Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    
    // Program variables
    private byte AttinyOut;
    private boolean ledStat;
    private boolean connectStat = false;
    private Button led_button;
    private Button forward_button;
    private Button reverse_button;
    private Button connect_button;
    private TextView xAccel;
    protected static final int MOVE_TIME = 80;
    private long lastWrite = 0;
    private AlertDialog aboutAlert;
    private View aboutView;
    
    // Sensor and Wakelock
    private SensorManager mySensorManager; 
    private List<Sensor> sensors; 
    private Sensor accSensor;
    protected PowerManager.WakeLock mWakeLock;
    
	// Bluetooth Stuff
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothSocket btSocket = null; 
    private OutputStream outStream = null;
    private ConnectThread mConnectThread = null;
    private String deviceAddress = null;
    // Well known SPP UUID (will *probably* map to RFCOMM channel 1 (default) if not in use); 
    // see comments in onResume(). 
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    
     /** Called when the activity is first created. */ 
     @Override 
     public void onCreate(Bundle savedInstanceState) { 
          super.onCreate(savedInstanceState); 
          setContentView(R.layout.main); 
          
          // Finds buttons in .xml layout file
          led_button = (Button) findViewById(R.id.led_button);
          forward_button = (Button) findViewById(R.id.forward_button);
          reverse_button = (Button) findViewById(R.id.reverse_button);
          connect_button = (Button) findViewById(R.id.connect_button);
          xAccel = (TextView) findViewById(R.id.accText);
          
          // Set Sensor + Manager 
          mySensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE); 
          sensors = mySensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER); 
          if(sensors.size() > 0) 
          { 
            accSensor = sensors.get(0); 
          }
          
          // Create about dialog
          AlertDialog.Builder builder = new AlertDialog.Builder(this);
       // Construct about menu with weblink
          LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
          aboutView = inflater.inflate(R.layout.about_text, null);
          // Create dialog
          builder.setMessage(null).setView(aboutView).setCancelable(true).setTitle(R.string.about).setIcon(R.drawable.ic_menu_info_details);
          aboutAlert = builder.create();
          
          // Prevent phone from sleeping
          final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE); 
          this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "My Tag"); 
          this.mWakeLock.acquire();
          
          // Check whether bluetooth adapter exists
          mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); 
          if (mBluetoothAdapter == null) { 
               Toast.makeText(this, R.string.no_bt_device, Toast.LENGTH_LONG).show(); 
               finish(); 
               return; 
          } 

          // If BT is not on, request that it be enabled.
          if (!mBluetoothAdapter.isEnabled()) {
              Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
              startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
          }
          
          /**********************************************************************
           * Buttons for controlling BluCar
           */
          
          // Connect to Bluetooth Module
          connect_button.setOnClickListener(new View.OnClickListener() {
 			
 			@Override
 			public void onClick(View v) {
				if (connectStat) {
					// Attempt to disconnect from the device
					disconnect();
				}else{
					// Attempt to connect to the device
					connect();
				}
 			}
 		});
       
         // Toggle Headlights
         led_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ledStat) {
					AttinyOut = (byte) (AttinyOut & 124);
					led_button.setText(R.string.ledON);
					ledStat = false;
				}else{
					AttinyOut = (byte) (AttinyOut | 128);
					led_button.setText(R.string.ledOFF);
					ledStat = true;
				}
				write(AttinyOut);
			}
		});
         
         // Drive forward
         forward_button.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if ((event.getAction() == ACTION_DOWN) | (event.getAction() == ACTION_MOVE)) {
					forward_button.setPressed(true);
					AttinyOut = (byte) (AttinyOut | 16);
					write(AttinyOut);
					return true;
					
				}else if (event.getAction() == ACTION_UP) {
					forward_button.setPressed(false);
					AttinyOut = (byte) (AttinyOut & 236);
					write(AttinyOut);
					return true;
				}
				forward_button.setPressed(false);
				return false;
			}
		});
         
         // Back up
         reverse_button.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if ((event.getAction() == ACTION_DOWN) | (event.getAction() == ACTION_MOVE)) {
					reverse_button.setPressed(true);
					AttinyOut = (byte) (AttinyOut | 32);
					write(AttinyOut);
					return true;
					
				}else if (event.getAction() == ACTION_UP) {
					reverse_button.setPressed(false);
					AttinyOut = (byte) (AttinyOut & 220);
					write(AttinyOut);
					return true;
				}
				reverse_button.setPressed(false);
				return false;
			}
		});
        
     }
     
     /** Thread used to connect to a specified Bluetooth Device */
     public class ConnectThread extends Thread {
    	private String address;

		ConnectThread(String MACaddress) {
			address = MACaddress;
    	 }

		public void run() {
    		// When this returns, it will 'know' about the server, 
             // via it's MAC address. 
             BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address); 

             // We need two things before we can successfully connect 
             // (authentication issues aside): a MAC address, which we 
             // already have, and an RFCOMM channel. 
             // Because RFCOMM channels (aka ports) are limited in 
             // number, Android doesn't allow you to use them directly; 
             // instead you request a RFCOMM mapping based on a service 
             // ID. In our case, we will use the well-known SPP Service 
             // ID. This ID is in UUID (GUID to you Microsofties) 
             // format. Given the UUID, Android will handle the 
             // mapping for you. Generally, this will return RFCOMM 1, 
             // but not always; it depends what other BlueTooth services 
             // are in use on your Android device. 
             try { 
                  btSocket = device.createRfcommSocketToServiceRecord(MY_UUID); 
             } catch (IOException e) { 
             } 

             // Discovery may be going on, e.g., if you're running a 
             // 'scan for devices' search from your handset's Bluetooth 
             // settings, so we call cancelDiscovery(). It doesn't hurt 
             // to call it, but it might hurt not to... discovery is a 
             // heavyweight process; you don't want it in progress when 
             // a connection attempt is made. 
             mBluetoothAdapter.cancelDiscovery(); 

             // Blocking connect, for a simple client nothing else can 
             // happen until a successful connection is made, so we 
             // don't care if it blocks. 
             try { 
                  btSocket.connect(); 
             } catch (IOException e1) { 
                  try { 
                       btSocket.close(); 
                  } catch (IOException e2) { 
                  } 
             } 

             // Create a data stream so we can talk to server. 
             try { 
                  outStream = btSocket.getOutputStream(); 
             } catch (IOException e2) { 
             } 
    	 }
     }
     
     private final SensorEventListener mSensorListener = new SensorEventListener() {

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {}

		@Override
		public void onSensorChanged(SensorEvent event) {
			// Checks whether to send steering command or not
			long date = System.currentTimeMillis();
			if (date - lastWrite > MOVE_TIME) {
				xAccel.setText(" " + event.values[0]);
				if (event.values[0] > 4.5) {
					// Turn right
					AttinyOut = (byte) (AttinyOut & 248);
					AttinyOut = (byte) (AttinyOut | 4);
				}else if (event.values[0] < -4.5) {
					// Turn left
					AttinyOut = (byte) (AttinyOut & 244);
					AttinyOut = (byte) (AttinyOut | 8);
				}else {
					// Center the steering servo
					AttinyOut = (byte) (AttinyOut & 240);
				}
				write(AttinyOut);
				lastWrite = date;
			}
		}
     };
     
     public void connect() {
    	 // Launch the DeviceListActivity to see devices and do scan
         Intent serverIntent = new Intent(this, DeviceListActivity.class);
         startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
     }
     
     public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	 switch (requestCode) {
         case REQUEST_CONNECT_DEVICE:
             // When DeviceListActivity returns with a device to connect
             if (resultCode == Activity.RESULT_OK) {
            	// Get the device MAC address
        		deviceAddress = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        		// Connect to device with specified MAC address
                mConnectThread = new ConnectThread(deviceAddress);
                mConnectThread.start();
                
                // Set button to display current status
                connectStat = true;
                connect_button.setText(R.string.connected);
                
    	 		// Reset the BluCar
    	 		AttinyOut = 0;
    	 		ledStat = false;
    	 		write(AttinyOut);
             }
             break;
         case REQUEST_ENABLE_BT:
             // When the request to enable Bluetooth returns
             if (resultCode == Activity.RESULT_OK) {
                 // Bluetooth is now enabled
             } else {
                 // User did not enable Bluetooth or an error occured
                 Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                 finish();
             }
         }
     }
     
     public void write(byte data) {
    	 if (outStream != null) {
             try {
            	 outStream.write(data);
             } catch (IOException e) {
             }
         }
     }
     
     public void emptyOutStream() {
    	 if (outStream != null) {
             try {
            	 outStream.flush();
             } catch (IOException e) {
             }
         }
     }
     
     public void disconnect() {
    	 if (outStream != null) {
    		 try {
    	 			outStream.close();
    	 			connectStat = false;
    				connect_button.setText(R.string.disconnected);
    	 		} catch (IOException e) {
    	 		}
    	 } 
     }
     
     @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         MenuInflater inflater = getMenuInflater();
         inflater.inflate(R.menu.option_menu, menu);
         return true;
     }
     
     @Override
     public boolean onOptionsItemSelected(MenuItem item) {
         switch (item.getItemId()) {
         case R.id.about:
             // Show info about the author (that's me!)
        	 aboutAlert.show();
             return true;
         case R.id.quit:
             // Quit the application
        	 System.exit(0);
             return true;
         }
         return false;
     }
     
     @Override 
     public void onStart() { 
          super.onStart(); 
     } 

     @Override 
     public void onResume() { 
          super.onResume();
          mySensorManager.registerListener(mSensorListener, accSensor, SensorManager.SENSOR_DELAY_GAME);
     } 

     @Override 
     public void onPause() { 
          super.onPause(); 
     } 

     @Override 
     public void onStop() {   
         super.onStop(); 
     } 

     @Override 
     public void onDestroy() { 
    	 if (mWakeLock.isHeld()) { 
             mWakeLock.release();
    	 }
    	 emptyOutStream();
         disconnect();
         if (mSensorListener != null) {
        	 mySensorManager.unregisterListener(mSensorListener);
         }
         super.onDestroy(); 
     } 
}