package br.fav.alarme.android;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

public class AlarmeProxy extends Service implements Runnable {

	private String message;
	private String cellNumber;
	private static GPSManager gpsManager;
	private static PICManager picManager;
	private static SmsSender smsSender;
	private SharedPreferences settings;

		
	public void ativarAlarme() {
		getGPSManagerInstance(this).rastrear();
		getPICManagerInstance(this).setTentativas(0);
		if(getPICManagerInstance(this).enviarMessagemParaOPic(49)) {
			getSmsSenderInstance(this).sendSMS(cellNumber, "Alarme ativado com sucesso");
		} else {
			getSmsSenderInstance(this).sendSMS(cellNumber, "Não foi possível ativar o alarme");	
		}
	}
	
	public void travar() {
		getPICManagerInstance(this).setTentativas(0);
		if(getPICManagerInstance(this).enviarMessagemParaOPic(51)) {
			getSmsSenderInstance(this).sendSMS(cellNumber, "Porta travada com sucesso");			
		} else {
			getSmsSenderInstance(this).sendSMS(cellNumber, "Não foi possível travar a porta");				
		}
	}
	
	public void destravar() {
		getPICManagerInstance(this).setTentativas(0);
		if(getPICManagerInstance(this).enviarMessagemParaOPic(55)) {
			getSmsSenderInstance(this).sendSMS(cellNumber, "Porta destravada com sucesso");
		} else {
			getSmsSenderInstance(this).sendSMS(cellNumber, "Não foi possível destravar a porta");	
		}
	}
	
	public void localizador() {
		getPICManagerInstance(this).setTentativas(0);
		if(getPICManagerInstance(this).enviarMessagemParaOPic(50)) {
			getSmsSenderInstance(this).sendSMS(cellNumber, "Localizador ativado com sucesso");			
		} else {
			getSmsSenderInstance(this).sendSMS(cellNumber, "Não foi possível ativar o localizador");				
		}
	}
	
	public void cancelarLocalizador() {
		getPICManagerInstance(this).setTentativas(0);
		if(getPICManagerInstance(this).enviarMessagemParaOPic(54)) {
			getSmsSenderInstance(this).sendSMS(cellNumber, "Localizador desativado com sucesso");			
		} else {
			getSmsSenderInstance(this).sendSMS(cellNumber, "Não foi possível desativar o localizador");				
		}
	}
	
	public void obterPosicaoAtual() {
		getGPSManagerInstance(this).rastrearUmaVez();
		gpsManager = null;
	}
	
	public void desativarAlarme() {
		getGPSManagerInstance(this).cancelarRastreamento();
		getPICManagerInstance(this).setTentativas(0);
		if(getPICManagerInstance(this).enviarMessagemParaOPic(57)) {
			getSmsSenderInstance(this).sendSMS(cellNumber, "Alarme desativado com sucesso");		
		} else {
			getSmsSenderInstance(this).sendSMS(cellNumber, "Não foi possível desativar o alarme");		
		}
		gpsManager = null;
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
	
	private static SmsSender getSmsSenderInstance(Service service) {
		if(smsSender == null) {
			smsSender = new SmsSender(service);
		}
		return smsSender;
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
		cellNumber = intent.getExtras().getString("Cellphone");
		settings = getSharedPreferences("AlarmeAndroid", 0);
		new Thread(this).start();
	}

	@Override
	public void onCreate() {
		
	}
	
	private void menu(Integer opcao) {
		switch (opcao) {
			case 1: ativarAlarme();
					this.stopSelf();
					break;
			case 2: localizador();
					this.stopSelf();
					break;
			case 3: travar();
					this.stopSelf();
					break;
			case 5: obterPosicaoAtual();
					this.stopSelf();
					break;
			case 6: cancelarLocalizador();
					this.stopSelf();
					break;
			case 7: destravar();
					this.stopSelf();
					break;
			case 9: desativarAlarme();
					this.stopSelf();
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
