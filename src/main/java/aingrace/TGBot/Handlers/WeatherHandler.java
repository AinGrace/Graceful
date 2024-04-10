package aingrace.TGBot.Handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
@Service
public class WeatherHandler implements UpdateHandler {

    @Override
    public void handle(Update update, TelegramClient client) {
        if (getText(update).filter(text -> text.matches("/weather \\w+")).isEmpty()) return;

        String arg = getText(update).get().substring(8);

        //TODO implement weather api
    }
}
