package br.fav.alarme.android;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

public class AlarmeProxy extends Service implements Runnable {

	private String message;
	private static GPSManager gpsManager;
	private static PICManager picManager;
	private SharedPreferences settings = getSharedPreferences("AlarmeAndroid", 0);

		
	public void ativarAlarme() {
		getGPSManagerInstance(this).rastrear();
		getPICManagerInstance(this).enviarMessagemParaOPic(1);
	}
	
	public void travar() {
		getPICManagerInstance(this).enviarMessagemParaOPic(3);
	}
	
	public void destravar() {
		getPICManagerInstance(this).enviarMessagemParaOPic(7);
	}
	
	public void obterPosicaoAtual() {
		getGPSManagerInstance(this).rastrear();
	}
	
	public void desativarAlarme() {
		getPICManagerInstance(this).enviarMessagemParaOPic(9);
	}
	
	private static GPSManager getGPSManagerInstance(Service service) {
		if(gpsManager == null) {
			gpsManager = new GPSManager(service);
		}
		return gpsManager;
	}
	
	private static PICManager getPICManagerInstance(Service service) {
		if(picManager == null) {
			picManager = new PICManager(service);
		}
		return picManager;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void run() {
		while(message == null || "".equals(message)) {
			try {
				synchronized (this) {
					this.wait(1000);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(validarSenha(message.substring(1,message.length()))) {
			menu(Integer.parseInt(message.substring(0,1)));
		}
		message = null;
	}
	
	@Override
	public void onStart(Intent intent, int startId){
		message = intent.getExtras().getString("Message");
	}

	@Override
	public void onCreate() {
		new Thread(this).start();
	}
	
	private void menu(Integer opcao) {
		switch (opcao) {
			case 1: ativarAlarme();
					break;
			case 3: travar();
					break;
			case 7: destravar();
					break;
			case 5: obterPosicaoAtual();
					break;
			case 9: desativarAlarme();
					break;
		}
	}
	
	private boolean validarSenha(String senha) {
		if(settings.getString("senha", "").equals(senha)) {			
			return true;
		}
		return false;
	}

}
