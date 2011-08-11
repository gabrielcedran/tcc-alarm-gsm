package br.fav.alarme.android;

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
	        if(bundle != null ){
	            Object [] pdus=(Object[]) bundle.get("pdus");
	            SmsMessage message = SmsMessage.createFromPdu((byte[]) pdus[0]);
	            if (!message.isEmail()) {
		            if(message.getMessageBody().length() >=2 ) {
		            	Intent intentAlarme = new Intent("ALARME_ANDROID");
	            		intentAlarme.putExtra("Message", message.getMessageBody());
	            		intentAlarme.putExtra("Cellphone", message.getOriginatingAddress());	     
	            		context.startService(intentAlarme);
		            }
	            }
	        }
		}   
    }       
	
}
