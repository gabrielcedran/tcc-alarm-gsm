package br.fav.alarme.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Main4 extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		final EditText txtDeviceName = (EditText) findViewById(R.id.txtDeviceName);

		final Button btnScan = (Button) findViewById(R.id.btnScan);
		
		final Main4 main = this;
		
		btnScan.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				/*GPSManager gpsManager = new GPSManager(main);
				gpsManager.rastrearUmaVez();
				gpsManager.rastrear();
				gpsManager.cancelarRastreamento();
				PICManager picManager = new PICManager(main);
				picManager.enviarMessagemParaOPic(1);*/
				
			}
		});
	}

}