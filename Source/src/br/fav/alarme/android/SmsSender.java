package br.fav.alarme.android;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.telephony.SmsManager;

public class SmsSender {

	private Service service;

	public SmsSender(Service service) {
		this.service = service;
	}

	public void sendSMS(String phoneNumber, String message) {
		PendingIntent pi = PendingIntent.getService(
				service.getApplicationContext(), 0,
				new Intent(service.getApplicationContext(), SmsSender.class), 0);
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(phoneNumber, null, message, pi, null);
	}
}
