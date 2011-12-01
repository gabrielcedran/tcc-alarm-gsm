package com.arnaldo.ControleBlue;



import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

//import com.arnaldo.teste1.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;




public class main extends Activity {
    /** Called when the activity is first created. */
	
private BluetoothAdapter mBluetoothAdapter = null;	
private BluetoothDevice  mBluetoothDevice = null;
protected BluetoothSocket mySocket;
private InputStream MyInStream;
private OutputStream MyOutStream;

private static final int REQUEST_ENABLE_BT = 2;
private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

TextView textViewLCD1;
TextView textViewLCD2;
TextView textViewLCD3;
TextView textViewLCD4;
TextView textViewLog;

ImageView imageLed1D;
ImageView imageLed1L;
ImageView imageLed2D;
ImageView imageLed2L;
ImageView imageLed3D;
ImageView imageLed3L;
ImageView imageLed4D;
ImageView imageLed4L;


Thread t = null;
byte[] bufferIn = new byte[100];
byte[] bufferIn_temp = new byte[10];
int bytesIn=0;
int bytesIn_tmp=0;

String strTempIn;
String strTempIn2;
String strBufferIn;
int primeira_posi;
int segunda_posi;

private boolean flag1;
private boolean flag2;
private boolean flag3;
private boolean flag4;	
private boolean flag_t;		
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);        
        textViewLog =  (TextView)findViewById(R.id.EditTextLog );
        textViewLCD1 = (TextView)findViewById(R.id.EditText1 );        
        textViewLCD2 = (TextView)findViewById(R.id.EditText2 );
        textViewLCD3 = (TextView)findViewById(R.id.EditText3 );        
        textViewLCD4 = (TextView)findViewById(R.id.EditText4 );
        
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();	
        
        Button Button1 = (Button)findViewById(R.id.Button1);
        Button1.setOnClickListener(goButton1);    
        Button Button2 = (Button)findViewById(R.id.Button2);
        Button2.setOnClickListener(goButton2);  
        Button Button3 = (Button)findViewById(R.id.Button3);
        Button3.setOnClickListener(goButton3);  
        Button Button4 = (Button)findViewById(R.id.Button4);
        Button4.setOnClickListener(goButton4);     
        Button Button5 = (Button)findViewById(R.id.ButtonSend);
        Button5.setOnClickListener(goButton5);  
        
        imageLed1D = (ImageView)findViewById(R.id.imageled1d );
        imageLed1L = (ImageView)findViewById(R.id.imageled1l );
        imageLed2D = (ImageView)findViewById(R.id.imageled2d );
        imageLed2L = (ImageView)findViewById(R.id.imageled2l );
        imageLed3D = (ImageView)findViewById(R.id.imageled3d );
        imageLed3L = (ImageView)findViewById(R.id.imageled3l );
        imageLed4D = (ImageView)findViewById(R.id.imageled4d );
        imageLed4L = (ImageView)findViewById(R.id.imageled4l );              
        
    } 
    
    
    
    
    @Override
    public void onStart() {
        super.onStart();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        
    	if (mBluetoothAdapter != null) 
     	{
    	    // Device does not support Bluetooth
     		textViewLog.setText("");
    		textViewLog.append("Bluetooth :" +  mBluetoothAdapter.toString());    
    		
           if (!mBluetoothAdapter.isEnabled()) 
            {
                //if (mChatService == null) setupChat();
            	textViewLog.append("Bluetooth is being activated"); 
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }     		
    		
           if (mBluetoothAdapter.isEnabled()) 
            {
        		mBluetoothAdapter.startDiscovery();         		
        	    Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
        	    for (BluetoothDevice device : devices) textViewLog.append("\nFound device: " + device);  
        	    textViewLog.append("\nConnecting");  
        	    
            	 String address ="00:1D:43:00:DF:26";
              	 mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(address);
              	 textViewLog.setText(mBluetoothDevice.toString() + "  " + mBluetoothDevice.getName());
              	 
                 BluetoothSocket tmp = null;
                 

                 try {
                     tmp = mBluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
                 } catch (IOException e) {
                     //Log.e(TAG, "CONNECTION IN THREAD DIDNT WORK");
                	 textViewLog.append("CONNECTION IN THREAD DIDNT WORK");
                 }
                 mySocket = tmp;
                 
                 try {
    				mySocket.connect();
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				//e.printStackTrace();
    				textViewLog.append("CONNECTION IN THREAD DIDNT WORK 2");
    			}   
    			try {
					MyInStream = mySocket.getInputStream();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			


    			
         		if (t==null)
         		{
         			//bufferIn[0]=0;
         			flag_t = true; 
	         	      t = new Thread() 
	         	      {
	         	          public void run() 
	         	          {
	         	              while (true) 
	         	              {
	         	            	 
		         	             // if (flag_t )
		         	             // {
		         	            	    try {
		         	            	    	bytesIn=MyInStream.read(bufferIn);
		         	            	    	strTempIn =  new String(bufferIn,0,bytesIn); 
		         	            	    	strBufferIn +=  strTempIn;
											//bytesIn = MyInStream.available();
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
		           	            		//if ((bytesIn > 0 ) ) 	
		           	            		//{
		           	            			messageHandler.sendMessage(Message.obtain(messageHandler, 1));
		           	            			//flag_t = false; 
		           	            		//}
	         	            	  //}
	         	              }
	         	          };
	         	      };
         		}
         	    t.start();  
    			
            }	
    	}else textViewLog.setText("Device does not support Bluetooth");  
    }
    
    
   // Instantiating the Handler associated with the main thread.
    private Handler messageHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) { 
        	        	
           switch(msg.what) {
                //handle update
                //.....
           	
            case 1:    
            	
				primeira_posi= strBufferIn.indexOf('$') ;
				strBufferIn = strBufferIn.substring(primeira_posi);
				
				primeira_posi= strBufferIn.indexOf('$') ;
				segunda_posi= strBufferIn.indexOf('\r',primeira_posi);						
				if ( (primeira_posi > -1)  &&  (segunda_posi > 2))
					 {
					   strTempIn2 =  strBufferIn.substring(primeira_posi, segunda_posi - primeira_posi+1);				   
					   textViewLog.append("\nComando recebido: "+strBufferIn );
					   strBufferIn = strBufferIn.substring(segunda_posi+1);
					   
					   //textViewLog.append("\n" );
					   bufferIn_temp = strTempIn2.getBytes();
					   
					   
					     switch ( bufferIn_temp[1])
		                 {
		                     case '1':		                        
		                        switch (bufferIn_temp[2])
		                         {                         
		                           case '1':   //  liga led 1
		                             if ( bufferIn_temp[3] == '1') 
		                               { 
		                            	   imageLed1D.setVisibility( View.INVISIBLE );
		                            	   flag1 = true;
		                               }
		                               else
		                               {
		                            	   imageLed1D.setVisibility( View.VISIBLE );
		                            	   flag1 = false;
		                               };                                                                             
		                           break;   
		                                                    
		                       case '2':   //  liga led 2
		                             if (bufferIn_temp[3] == '1') 
		                               { 
		                            	   imageLed2D.setVisibility( View.INVISIBLE );
		                            	   flag2 = true;
		                               }
		                               else
		                               {
		                            	   imageLed2D.setVisibility( View.VISIBLE );
		                            	   flag2 = false;
		                               };                                                                             
		                           break; 
		                                                    
		                           case '3':   //  liga led 3
		                             if ( bufferIn_temp[3] == '1') 
		                               { 
		                            	   imageLed3D.setVisibility( View.INVISIBLE );
		                            	   flag3 = true;
		                               }
		                               else
		                               {
		                            	   imageLed3D.setVisibility( View.VISIBLE );
		                            	   flag3 = false;
		                               };                                                                             
		                           break; 
		                                                    
		                           case '4':   //  liga led 4
		                             if ( bufferIn_temp[3] == '1') 
		                               { 
		                            	   imageLed4D.setVisibility( View.INVISIBLE );
		                            	   flag4 = true;
		                               }
		                               else
		                               {
		                            	   imageLed4D.setVisibility( View.VISIBLE );
		                            	   flag4 = false;
		                               };                                                                             
		                           break;
		                         }                                           
		                     break;    
		                     
		                     
		                     case '2':

		                     break;           
		                 }
					   
					   
					 };	    
            break; 

            }
        }

    };     
    
    
    private OnClickListener goButton1 = new OnClickListener()
    {
        public void onClick(View v)
        {
         	if (mySocket != null) 
         	{
        	    // Device does not support Bluetooth
         		try {
					MyOutStream = mySocket.getOutputStream();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}     
			
				try {
					if (flag1) 
					{ 
						MyOutStream.write("$110\r".getBytes()); 
						imageLed1D.setVisibility( View.VISIBLE );
						flag1 = false;
					}
					else
					{						
						MyOutStream.write("$111\r".getBytes()); 
						imageLed1D.setVisibility( View.INVISIBLE );
						flag1 = true;
					};
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
				
        	}else textViewLog.setText("Device does not support Bluetooth");  
        		
        };
    };
    
 
    private OnClickListener goButton2 = new OnClickListener()
    {
        public void onClick(View v)
        {
         	if (mBluetoothAdapter != null) 
         	{
        	    // Device does not support Bluetooth
         		try {
					MyOutStream = mySocket.getOutputStream();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}     
			
				try {
					if (flag2) 
					{ 
						MyOutStream.write("$120\r".getBytes()); 
						imageLed2D.setVisibility( View.VISIBLE );
						flag2 = false;
					}
					else
					{						
						MyOutStream.write("$121\r".getBytes()); 
						imageLed2D.setVisibility( View.INVISIBLE );
						flag2 = true;
					};
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
				
        	}else textViewLog.setText("Device does not support Bluetooth");  
        		
        };
    };
    
    private OnClickListener goButton3 = new OnClickListener()
    {
        public void onClick(View v)
        {
         	if (mBluetoothAdapter != null) 
         	{
        	    // Device does not support Bluetooth
         		try {
					MyOutStream = mySocket.getOutputStream();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}     
			
				try {
					if (flag3) 
					{ 
						MyOutStream.write("$130\r".getBytes()); 
						imageLed3D.setVisibility( View.VISIBLE );
						flag3 = false;
					}
					else
					{						
						MyOutStream.write("$131\r".getBytes()); 
						imageLed3D.setVisibility( View.INVISIBLE );
						flag3 = true;
					};
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
				
        	}else textViewLog.setText("Device does not support Bluetooth");  
        		
        };
    };
    
    private OnClickListener goButton4 = new OnClickListener()
    {
        public void onClick(View v)
        {
         	if (mBluetoothAdapter != null) 
         	{
        	    // Device does not support Bluetooth
         		try {
					MyOutStream = mySocket.getOutputStream();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}     
			
				try {
					if (flag4) 
					{ 
						MyOutStream.write("$140\r".getBytes()); 
						imageLed4D.setVisibility( View.VISIBLE );
						flag4 = false;
					}
					else
					{						
						MyOutStream.write("$141\r".getBytes()); 
						imageLed4D.setVisibility( View.INVISIBLE );
						flag4 = true;
					};
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
				
        	}else textViewLog.setText("Device does not support Bluetooth");  
        		
        };
    };
    
   
    
    private OnClickListener goButton5 = new OnClickListener()
    {
    	
    	 String envio;  
        public void onClick(View v)
        {
         	if (mBluetoothAdapter != null) 
         	{
        	    // Device does not support Bluetooth
         		try {
					MyOutStream = mySocket.getOutputStream();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}     
			
				try {
						envio = "$21" + textViewLCD1.getText() +"\n" + textViewLCD2.getText() +"\n" + textViewLCD3.getText() +"\n" + textViewLCD4.getText() + "\r";						
						MyOutStream.write(envio.getBytes()); 
						flag4 = false;				


				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
				
        	}else textViewLog.setText("Device does not support Bluetooth");  
        		
        };
    };
    
    
    

    
    
    
    
    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
        	mySocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
        // Make sure we're not doing discovery anymore
        if (mBluetoothAdapter != null) 
        {
        	mBluetoothAdapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        //this.unregisterReceiver(mBluetoothAdapter);
    } 
    
    
    
    
    
}