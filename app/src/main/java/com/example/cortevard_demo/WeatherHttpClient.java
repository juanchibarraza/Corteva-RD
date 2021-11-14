package com.example.cortevard_demo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class WeatherHttpClient {

	private static String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?";
	//private static String IMG_URL = "http://openweathermap.org/img/w/";
	private static String API_Key = "83cbfb2b392a6f8caec0e83ab641e8d9";
	//http://api.openweathermap.org/data/2.5/weather?q=city,country&APPID={your_key}

	
	public String getWeatherData(String lat, String lon) {
		HttpURLConnection con = null ;
		InputStream is = null;

		try {
			con = (HttpURLConnection) ( new URL(BASE_URL + "lat=" + lat + "&lon=" + lon + "&APPID=" + API_Key + "&lang=es&units=metric")).openConnection();
			con.setRequestMethod("GET");
			con.setDoInput(true);
			con.setDoOutput(true);
			con.connect();
			
			// Let's read the response
			StringBuffer buffer = new StringBuffer();
			is = con.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = null;
			while (  (line = br.readLine()) != null )
				buffer.append(line + "\r\n");
			
			is.close();
			con.disconnect();
			return buffer.toString();
	    }
		catch(Throwable t) {
			t.printStackTrace();
		}
		finally {
			try { is.close(); } catch(Throwable t) {}
			try { con.disconnect(); } catch(Throwable t) {}
		}

		return null;
				
	}
	/*
	public byte[] getImage(String code) {
		HttpURLConnection con = null ;
		InputStream is = null;
		try {
			con = (HttpURLConnection) ( new URL(IMG_URL + code)).openConnection();
			con.setRequestMethod("GET");
			con.setDoInput(true);
			con.setDoOutput(true);
			con.connect();
			
			// Let's read the response
			is = con.getInputStream();
			byte[] buffer = new byte[1024];
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			while ( is.read(buffer) != -1)
				baos.write(buffer);
			
			return baos.toByteArray();
	    }
		catch(Throwable t) {
			t.printStackTrace();
		}
		finally {
			try { is.close(); } catch(Throwable t) {}
			try { con.disconnect(); } catch(Throwable t) {}
		}
		
		return null;
		
	}*/
}
