package aingrace.TGBot.Handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.time.LocalDateTime;

@Slf4j
@Component
public class GreetingHandler implements UpdateHandler {

    @Override
    public void handle(Update update, TelegramClient client) {
        getText(update).ifPresent(_ -> {
            String timeOfDay = LocalDateTime.now().getHour() < 18 ? "День" : "Вечер";

            String greetings = "Добрый " + timeOfDay + " я бот созданный на спринге";
            SendMessage sendMessage = SendMessage.builder()
                    .text(greetings)
                    .chatId(getChatId(update).orElseThrow())
                    .build();

            try {
                client.execute(sendMessage);
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
        });
    }
}
