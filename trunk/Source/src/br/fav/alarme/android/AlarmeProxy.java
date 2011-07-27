package br.fav.alarme.android;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AlarmeProxy extends Service implements Runnable {

	private static GPSManager gpsManager;
	private static PICManager picManager;
	
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
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void run() {
		
	}

}
