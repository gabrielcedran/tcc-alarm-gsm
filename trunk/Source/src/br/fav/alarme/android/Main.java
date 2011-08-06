package br.fav.alarme.android;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Main extends Activity implements ServiceConnection {

	private AlarmeProxy proxy;
	private ServiceConnection conexao;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		final EditText txtDeviceName = (EditText) findViewById(R.id.txtDeviceName);

		final Button btnScan = (Button) findViewById(R.id.btnScan);
		
		final Main main = this;
		
		conexao = this;
		btnScan.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				final EditText txtDeviceName = (EditText) findViewById(R.id.txtDeviceName);
				Intent intentAlarme = new Intent("ALARME_ANDROID");
				intentAlarme.putExtra("Message", txtDeviceName.getText().toString());
				startService(intentAlarme);
				
			}
		});
	}
	
	private void verificaUnbound(Integer opcao) {
		switch (opcao) {
			case 3: unbindService(conexao);
					proxy = null;
					break;
			case 7: unbindService(conexao);
					proxy = null;
					break;
			case 9: unbindService(conexao);
					proxy = null;
					break;
		}
	}

	private void menu(Integer opcao) {
		switch (opcao) {
			case 1: proxy.ativarAlarme();
					break;
			case 3: proxy.travarDestravar();
					break;
			case 7: proxy.obterPosicaoAtual();
					break;
			case 9: proxy.desativarAlarme();
					break;
		}
	}
	
	private boolean validarSenha(String senha) {
		return true;
	}
	
	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		if(proxy == null) {
			//proxy = ((LocalBinder) service).getService(); 
			menu(1);
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		// TODO Auto-generated method stub
		
	}

}