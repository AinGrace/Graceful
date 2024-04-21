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
        log.info(updateInfo(update));
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

    private String updateInfo(Update update) {
        if (update.hasMessage()) {
            String chatName = update.getMessage().getChat().getFirstName();
            String from = update.getMessage().getFrom().getUserName();
            long chatId = update.getMessage().getChatId();
            return String.format("Message received in %s, from %s, ID -> %d", chatName, from, chatId);
        } else if (update.hasCallbackQuery()) {
            var query = update.getCallbackQuery();
            String chat = query.getMessage().getChat().getFirstName();
            String from = query.getFrom().getUserName();
            Long chatId = query.getMessage().getChatId();
            return String.format("Callback query received in %s, from %s, ID -> %d", chat, from, chatId);
        }

        //TODO
        String info = "";
        info += switch (update) {
            case Update upd when upd.hasBusinessConnection() -> "Business connection,";
            case Update upd when upd.hasBusinessMessage() -> "Business message,";
            case Update upd when upd.hasChannelPost() -> "Channel post,";
            case Update upd when upd.hasChatJoinRequest() -> "Chat join request,";
            case Update upd when upd.hasChatMember() -> "Chat member,";
            default -> "";
        };
        return info;
    }
}
