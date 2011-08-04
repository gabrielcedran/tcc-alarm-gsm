package br.fav.alarme.android;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class AlarmeProxy extends Service implements Runnable {

	private String message;
	private static GPSManager gpsManager;
	private static PICManager picManager;
		
	public void ativarAlarme() {
		getGPSManagerInstance(this).rastrear();
		getPICManagerInstance(this).enviarMessagemParaOPic(1);
	}
	
	public void travarDestravar() {
		getPICManagerInstance(this).enviarMessagemParaOPic(3);
	}
	
	public void obterPosicaoAtual() {
		getGPSManagerInstance(this).rastrearUmaVez();
	}
	
	public void desativarAlarme() {
		getPICManagerInstance(this).enviarMessagemParaOPic(9);
		getGPSManagerInstance(this).cancelarRastreamento();
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
		menu(Integer.parseInt(message.substring(0,1)));
	}
	
	@Override
	public void onStart(Intent intent, int startId){
		message = intent.getExtras().getString("Message");
		new Thread(this).start();
	}

	@Override
	public void onCreate() {
		System.out.println("tESTE2");
	}
	
	private void menu(Integer opcao) {
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
	
	private boolean validarSenha(String senha) {
		return true;
	}

}
