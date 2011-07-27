package br.fav.alarme.android;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

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
	            if(str.length() >=2 ) {
	            	Intent intentAlarme = new Intent("ALARME_ANDROID");
	            	PendingIntent p = PendingIntent.getService(context, 0, intentAlarme, 0);
	            }
	        }
		}   
    }        
}
