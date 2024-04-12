package aingrace.TGBot.Handlers;

import com.fasterxml.jackson.core.type.TypeReference;
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
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class WeatherHandler implements UpdateHandler {

    private final String lang = "&lang=ru";
    @Value("${weatherApi.Key")
    private String key;

//TODO AAAAAAA
    @Override
    public void handle(Update update, TelegramClient client) {
        if (getText(update).filter(text -> text.matches("/погода \\w+")).isEmpty()) return;

        String arg = getText(update).get().substring(8);

        getCurrentForecast(arg);
    }

    private void getCurrentForecast(String arg) {

        URL url = UrlResource.from("http://api.weatherapi.com/v1/current.json?key=&lang=ru&q=" + arg).getURL();

        try {
            URLConnection connection = url.openConnection();

            connection.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String responce = reader.lines().reduce(String::concat).get();
            ObjectMapper objectMapper = new ObjectMapper();

            Map<String, Object> objectMap = objectMapper.readValue(responce, new TypeReference<>(){});

            Set<String> strings = objectMap.keySet();


            System.out.println("blah");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
