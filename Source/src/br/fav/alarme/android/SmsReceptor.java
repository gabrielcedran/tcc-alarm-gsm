package br.fav.alarme.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SmsReceptor extends BroadcastReceiver {

	static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";  

	
	@Override
    public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(ACTION)) {
			Bundle bundle= intent.getExtras();
	        SmsMessage[] msgs= null;
	        String str="";
	        if(bundle != null ){
	            Object [] pdus=(Object[]) bundle.get("pdus");
	            msgs=new SmsMessage[pdus.length];
	            for(int i=0; i< msgs.length;i++){
	                msgs[i]= SmsMessage.createFromPdu((byte[])pdus[i]);
	                str+= msgs[i].getMessageBody();
	            }
	             Toast.makeText(context, str, Toast.LENGTH_LONG).show();
	        }
		}   
    }         
	
}
