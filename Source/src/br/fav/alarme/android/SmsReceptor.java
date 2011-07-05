package br.fav.alarme.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SmsReceptor extends BroadcastReceiver {

	static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";  

	private static GPSManager gpsManager;
	private static PICManager picManager;
	private static SmsReceptor smsReceptor;
	
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
	            	if(validarSenha(str.substring(1))) {
	            		menu(Integer.parseInt(str.substring(0, 1)));	
	            	}
	            }
	        }
		}   
    }        
	
	private static boolean validarSenha(String senha) {
		return true;
	}
	
	private static void menu(Integer opcao) {
		switch (opcao) {
			case 1: ativarAlarme();
					break;
			case 3: travarDestravar();
					break;
			case 7: obterPosicaoAtual();
					break;
			case 9: desativarAlarme();
					break;
		}
	}
	
	private static void ativarAlarme() {
		getPICManagerInstance().enviarMessagemParaOPic(1);
		getGPSManagerInstance().rastrear();
	}
	
	private static void travarDestravar() {
		getPICManagerInstance().enviarMessagemParaOPic(3);
	}
	
	private static void obterPosicaoAtual() {
		getGPSManagerInstance().rastrearUmaVez();
	}
	
	private static void desativarAlarme() {
		getPICManagerInstance().enviarMessagemParaOPic(9);
		getGPSManagerInstance().cancelarRastreamento();
	}
	
	private static GPSManager getGPSManagerInstance() {
		if(gpsManager == null) {
			//gpsManager = new GPSManager(smsReceptor);
		}
		return gpsManager;
	}
	
	private static PICManager getPICManagerInstance() {
		if(picManager == null) {
			//picManager = new PICManager(smsReceptor);
		}
		return picManager;
	}
}
