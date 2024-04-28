package aingrace.TGBot.Handlers;

import aingrace.TGBot.Bot.UpdateReceivedEvent;
import org.springframework.context.event.EventListener;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.Optional;
import java.util.function.Consumer;

public interface UpdateHandler {

    void handle(Update update);

    @EventListener(UpdateReceivedEvent.class)
    default void process(UpdateReceivedEvent event) {
        handle(event.update);
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

    default Optional<CallbackQuery> getCallbackQuery(Update update) {
        return Optional.ofNullable(update.getCallbackQuery());
    }

    default void ifCallbackQueryPresent(Consumer<CallbackQuery> queryConsumer, Update update) {
        getCallbackQuery(update).ifPresent(queryConsumer);
    }
}
