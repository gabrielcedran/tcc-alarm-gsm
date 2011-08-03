package br.fav.alarme.android;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class AlarmeProxy extends Service implements Runnable {

	private final IBinder conexao = new LocalBinder();
	private String message;
	private static GPSManager gpsManager;
	private static PICManager picManager;
		
	public void ativarAlarme() {
		new Thread(this).start();
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
		return conexao;
	}

	@Override
	public void run() {
		getPICManagerInstance(this).enviarMessagemParaOPic(1);
		getGPSManagerInstance(this).rastrear();
	}
	
	@Override
	public void onStart(Intent intent, int startId){
		
	}

	@Override
	public void onCreate() {
		
	}
	
	public class LocalBinder extends Binder {
        public AlarmeProxy getService() {
            return AlarmeProxy.this;
        }
    }

}
