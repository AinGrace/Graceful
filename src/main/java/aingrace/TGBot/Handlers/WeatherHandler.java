package aingrace.TGBot.Handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

@Slf4j
@Service
public class WeatherHandler implements UpdateHandler {

    private final String lang = "lang=ru";

    @Value("${weatherApi.Key}")
    private String key;

//TODO AAAAAAA
    @Override
    public void handle(Update update, TelegramClient client) {
        if (getText(update).filter(text -> text.matches("/погода \\w+")).isEmpty()) return;

        String arg = getText(update).get().substring(8);

        getCurrentForecast(arg);
    }

    private void getCurrentForecast(String arg) {

        String formatted = String.format("http://api.weatherapi.com/v1/current.json?key=%s&lang=ru&q=%s", key, arg);
        URL url = UrlResource.from(formatted).getURL();

        try {
            URLConnection connection = url.openConnection();

            connection.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String responce = reader.lines().reduce(String::concat).get();
            ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

            CurrentWeather currentWeather = objectMapper.readValue(responce, CurrentWeather.class);




            System.out.println("blah");

        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }
}
