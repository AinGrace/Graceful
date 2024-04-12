package aingrace.TGBot.Handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

@Slf4j
@Service
public class WeatherHandler implements UpdateHandler {

    private final String ENDPOINT = "https://api.weatherapi.com/v1/current.json?";
    private final String KEY;
    private final String LANG = "&lang=ru";


    public WeatherHandler(@Value("${weatherApi.Key}") String key) {
        this.KEY = "key=" + key;
    }

    @Override
    public void handle(Update update, TelegramClient client) {
        if (getText(update).filter(text -> text.matches("/погода \\w+")).isEmpty()) return;

        String forCity = getText(update).get().substring(8);
        long chatId = getChatId(update).get();

        String currentForecast = getCurrentForecast("&q=" + forCity);

        var builder = SendMessage.builder().chatId(chatId).parseMode("HTML");

        if (currentForecast.isBlank()) {
            currentForecast = "can't get the weather data for " + forCity;
            SendMessage sendMessage = builder.text(currentForecast).build();
            send(client, sendMessage);
        } else {
            SendMessage sendMessage = builder.text(currentForecast).build();
            send(client, sendMessage);
        }
    }

    private void send(TelegramClient client, SendMessage sendMessage) {
        try {
            client.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.getLocalizedMessage());
        }
    }

    private String getCurrentForecast(String arg) {
        String formatted = String.format(ENDPOINT + KEY + LANG + arg);
        URL url = UrlResource.from(formatted).getURL();
        try {
            URLConnection connection = url.openConnection();

            connection.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String responce = reader.lines().reduce(String::concat).get();
            ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

            CurrentWeather currentWeather = objectMapper.readValue(responce, CurrentWeather.class);

            return currentWeather.getWeather();

        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
            return "";
        }
    }
}
