package com.example.cortevard_demo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class JSONWeatherParser {

	public static Weather getWeather(String data) throws JSONException {
		Weather weather = new Weather();

		if(data!=null){
			// We create out JSONObject from the data
			JSONObject jObj = new JSONObject(data);

			// We start extracting the info
			Location loc = new Location();

			JSONObject coordObj = getObject("coord", jObj);
			loc.setLatitude(getFloat("lat", coordObj));
			loc.setLongitude(getFloat("lon", coordObj));

			JSONObject sysObj = getObject("sys", jObj);
			loc.setCountry(getString("country", sysObj));
			loc.setSunrise(getInt("sunrise", sysObj));
			loc.setSunset(getInt("sunset", sysObj));
			loc.setCity(getString("name", jObj));
			weather.location = loc;

			// We get weather info (This is an array)
			JSONArray jArr = jObj.getJSONArray("weather");

			// We use only the first value
			JSONObject JSONWeather = jArr.getJSONObject(0);
			weather.currentCondition.setWeatherId(getInt("id", JSONWeather));
			weather.currentCondition.setDescr(getString("description", JSONWeather));
			weather.currentCondition.setCondition(getString("main", JSONWeather));
			weather.currentCondition.setIcon(getString("icon", JSONWeather));

			JSONObject mainObj = getObject("main", jObj);
			weather.currentCondition.setHumidity(getInt("humidity", mainObj));
			weather.currentCondition.setPressure(getInt("pressure", mainObj));
			weather.temperature.setMaxTemp(getFloat("temp_max", mainObj));
			weather.temperature.setMinTemp(getFloat("temp_min", mainObj));
			weather.temperature.setTemp(getFloat("temp", mainObj));

			// Wind
			JSONObject wObj = getObject("wind", jObj);
			//se pasa de m/s a km/h
			float speed = getFloat("speed", wObj)* (float) 3.60;
			weather.wind.setSpeed(speed);
			try{
				weather.wind.setDeg(getFloat("deg", wObj));
				weather.wind.setRumbo(getRumbo(getFloat("deg", wObj)));
			} catch (Exception e) {
				//puede ser que no venga el valor deg
			}
			// Clouds
			JSONObject cObj = getObject("clouds", jObj);
			weather.clouds.setPerc(getInt("all", cObj));

			// We download the icon to show
			return weather;
		}else{
			return null;
		}
	}

	private static String getRumbo(float wd){
		String rumbo = "error";
		if (wd < 0 || wd > 360) {
			rumbo = "error";
		}
		if (wd >= 0 && wd <= 11.25) {
			rumbo = "N";
		}
		if (wd > 348.75 && wd <= 360) {
			rumbo = "N";
		}
		if (wd > 11.25 && wd <= 33.75) {
			rumbo = "NNE";
		}
		if (wd > 33.75 && wd <= 56.25) {
			rumbo = "NE";
		}
		if (wd > 56.25 && wd <= 78.75) {
			rumbo =  "ENE";
		}
		if (wd > 78.75 && wd <= 101.25) {
 			rumbo =  "E";
		}
		if (wd > 101.25 && wd <= 123.75) {
			rumbo = "ESE";
		}
		if (wd > 123.75 && wd <= 146.25) {
			rumbo = "SE";
		}
		if (wd > 146.25 && wd <= 168.75) {
			rumbo = "SSE";
		}
		if (wd > 168.75 && wd <= 191.25) {
			rumbo = "S";
		}
		if (wd > 191.25 && wd <= 213.75) {
			rumbo = "SSO";
		}
		if (wd > 213.75 && wd <= 236.25) {
			rumbo = "SO";
		}
		if (wd > 236.25 && wd <= 258.75) {
			rumbo = "OSO";
		}
		if (wd > 258.75 && wd <= 281.25) {
			rumbo = "O";
		}
		if (wd > 281.25 && wd <= 303.75) {
			rumbo = "ONO";
		}
		if (wd > 303.75 && wd <= 326.25) {
			rumbo = "NO";
		}
		if (wd > 326.25 && wd <= 348.75) {
			rumbo = "NNO";
		}
		return rumbo;
	}

	private static JSONObject getObject(String tagName, JSONObject jObj)  throws JSONException {
		JSONObject subObj = jObj.getJSONObject(tagName);
		return subObj;
	}
	
	private static String getString(String tagName, JSONObject jObj) throws JSONException {
		return jObj.getString(tagName);
	}

	private static float  getFloat(String tagName, JSONObject jObj) throws JSONException {
		return (float) jObj.getDouble(tagName);
	}
	
	private static int  getInt(String tagName, JSONObject jObj) throws JSONException {
		return jObj.getInt(tagName);
	}
	
}
