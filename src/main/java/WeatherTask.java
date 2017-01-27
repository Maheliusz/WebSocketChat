import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Michał Zakrzewski on 2017-01-18.
 */
public class WeatherTask {
    private String weather;
    public WeatherTask(){
        weather = getWeatherFromUrl();
    }

    public String getWeather() {

        return weather;
    }

    private String getWeatherFromUrl() {
        try {
            String url =
                    "http://api.openweathermap.org/data/2.5/weather?id=3094802&APPID=86a88d10a120fd9540562229f93d270d";
            JSONObject json = JSONReader.readJsonFromUrl(url);
            String weather = "";
            JSONArray arr = json.getJSONArray("weather");
            for (int i = 0; i < arr.length(); i++) {
                weather += arr.getJSONObject(i).getString("main")
                        /*+ " - " + arr.getJSONObject(i).getString("description")*/
                        + "\n";
            }
            return "Weather in " + json.getString("name") + ": "
                    + weather + ",\n"
                    + "Celsius degrees: " + (json.getJSONObject("main").getDouble("temp") - 273.15);
        } catch (JSONException | IOException e) {
            //e.printStackTrace();
            return "We've encountered some problems :(";
        }
    }

}
