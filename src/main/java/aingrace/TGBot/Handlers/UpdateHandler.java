package aingrace.TGBot.Handlers;

import aingrace.TGBot.Bot.UpdateReceivedEvent;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.context.event.EventListener;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Optional;

public interface UpdateHandler {

    void handle(Update update, TelegramClient client);

    @EventListener
    default void process(UpdateReceivedEvent event) {
        handle(event.update, client());
    }

    @Lookup
    default TelegramClient client() {
        return null;
    }

    default Optional<Message> getMessage(Update update) {
        return Optional.ofNullable(update.getMessage());
    }

    default Optional<String> getText(Update update) {
        return getMessage(update).map(Message::getText);
    }

    default Optional<Long> getChatId(Update update) {
        return getMessage(update).map(Message::getChatId);
    }
}
