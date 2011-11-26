package br.fav.alarme.android;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class CoordinatesSender {

	public static Boolean enviarCoordenadas(String latitude, String longitude,
			String idCarro, String serverName, String senha) {
		
		String urlString = "http://";
		urlString += serverName;
		urlString += "/AlarmeAndroid/ReceberCoordenadas?";
		urlString += "latitude="+latitude;
		urlString += "&longitude="+longitude;
		urlString += "&idCarro="+idCarro;
		urlString += "&senha="+senha;
		
		try {
			URL url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestProperty("Request-Method", "GET");
			connection.setDoInput(true);
			connection.setDoOutput(false);
			connection.connect();
			connection.getResponseMessage();
			connection.disconnect();
			return true;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}

}
