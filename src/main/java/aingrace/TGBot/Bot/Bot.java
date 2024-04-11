package aingrace.TGBot.Bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;


@Slf4j
@Component
@RequiredArgsConstructor
@PropertySource("classpath:bot.properties")
public class Bot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final ApplicationEventPublisher publisher;

    @Value("${bot.key}")
    private String key;

    @Override
    public void consume(Update update) {
        log.info("Update received");
        publisher.publishEvent(new UpdateReceivedEvent(this, update));
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public String getBotToken() {
        return key;
    }
}
