package br.fav.alarme.android;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Main extends Activity {
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		SharedPreferences settings = getSharedPreferences("AlarmeAndroid", 0);

		final EditText txtServerName = (EditText) findViewById(R.id.txtServerName);
		txtServerName.setText(settings.getString("serverName", ""));

		final EditText txtPicName = (EditText) findViewById(R.id.txtPicName);
		txtPicName.setText(settings.getString("picName", ""));

		final EditText txtSenha = (EditText) findViewById(R.id.txtSenha);
		txtSenha.setText(settings.getString("senha", ""));

		final EditText txtIdCarro = (EditText) findViewById(R.id.txtIdCarro);
		txtIdCarro.setText(settings.getString("idCarro", ""));
		
		final Button btnSave = (Button) findViewById(R.id.btnSave);
		
		btnSave.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				final EditText txtServerName = (EditText) findViewById(R.id.txtServerName);

				final EditText txtPicName = (EditText) findViewById(R.id.txtPicName);
				
				final EditText txtSenha = (EditText) findViewById(R.id.txtSenha);
				
				final EditText txtIdCarro = (EditText) findViewById(R.id.txtIdCarro);

				SharedPreferences settings = getSharedPreferences("AlarmeAndroid", 0);
				SharedPreferences.Editor editor = settings.edit();
			    editor.putString("serverName", txtServerName.getText().toString());
			    editor.putString("picName", txtPicName.getText().toString());
			    editor.putString("senha", txtSenha.getText().toString());
			    editor.putString("idCarro", txtIdCarro.getText().toString());
			    editor.commit();
			}
		});
	}

}