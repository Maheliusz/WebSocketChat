import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Michał Zakrzewski on 2017-01-25.
 */
public class ChatBot {
    private WeatherTask weatherTask;

    public ChatBot() {
        weatherTask = new WeatherTask();
    }

    public String getAnswer(String question) {
        switch (question) {
            case "day":
                return getDay();
            case "time":
                return getTime();
            case "weather":
                return getWeather();
            default:
                return "Please, ask me only those questions: day, time, weather";
        }

    }

    private String getWeather() {
        return weatherTask.getWeather();
    }

    private String getTime() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    private String getDay() {
        return new SimpleDateFormat("EEEEEEEEEEEE").format(new Date());
    }
}
