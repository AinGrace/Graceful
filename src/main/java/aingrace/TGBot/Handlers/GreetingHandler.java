package aingrace.TGBot.Handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
@Lazy
public class GreetingHandler implements UpdateHandler {

    private final TelegramClient client;

    @Override
    public void handle(Update update) {
        if (getText(update).filter(text -> text.matches("[Бб]от")).isEmpty()) return;

        String timeOfDay = LocalDateTime.now().getHour() < 18 ? "День" : "Вечер";
        String greetings = "Добрый " + timeOfDay + " я бот созданный на спринге";

        var sendMessage = SendMessage.builder()
                .text(greetings)
                .chatId(getChatId(update).orElseThrow())
                .build();

        try {
            client.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}
