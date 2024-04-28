package aingrace.TGBot.News;

import aingrace.TGBot.Handlers.UpdateHandler;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class Habr implements UpdateHandler {

    private final OkHttpTelegramClient client;
    private final Map<Long, UserState> states = new HashMap<>();
    private final HabrCommandHandler commandHandler;

    static InlineKeyboardMarkup getMarkup(Integer pivot) {
        var bb = InlineKeyboardButton.builder();

        var prevButton = bb.text("<-").callbackData("prev").build();
        var nextButton = bb.text("->").callbackData("next").build();

        var countButton = bb.text(String.valueOf(pivot + 1)).callbackData("count").build();

        var startButton = bb.text("<<<").callbackData("start").build();
        var endButton = bb.text(">>>").callbackData("end").build();

        var firstRow = new InlineKeyboardRow(prevButton, countButton, nextButton);
        var secondRow = new InlineKeyboardRow(startButton, endButton);

        var rows = List.of(firstRow, secondRow);

        return new InlineKeyboardMarkup(rows);
    }

    @Override
    public void handle(Update update) {
        commandHandler.setStates(states);
        ifCallbackQueryPresent(this::handleQuery, update);
        getMessage(update).filter(message -> message.getText().matches("/habr (daily|weekly|monthly|yearly)( \\d)?$"))
                .ifPresent(message -> {
                    SendMessage sendMessage = commandHandler.handle(message);
                    send(sendMessage, getChatId(update).orElseThrow());
                });
    }

//    private void handleCommand(Update update) {
//        Long chatId = getChatId(update).orElseThrow();
//        String command = getText(update).orElseThrow().substring(6);
//        String rssLink = switch (command) {
//            case "daily" -> DAILY_TOP_POSTS;
//            case "weekly" -> WEEKLY_TOP_POSTS;
//            case "monthly" -> MONTHLY_TOP_POSTS;
//            case "yearly" -> YEARLY_TOP_POSTS;
//            default -> throw new IllegalStateException("Unexpected value: " + command);
//        };
//
//        try (var feed = rssReader.read(rssLink)) {
//            List<String> links = feed.map(item -> item.getLink().orElseThrow()).toList();
//
//            UserState state = states.getOrDefault(chatId, new UserState());
//            state.setLinks(links);
//            state.setPivot(0);
//            states.put(chatId, state);
//
//            var message = SendMessage.builder()
//                    .chatId(chatId)
//                    .text(links.getFirst())
//                    .replyMarkup(getMarkup(state.getPivot()))
//                    .build();
//
//            send(message, chatId);
//        } catch (IOException e) {
//            System.out.println(e.getLocalizedMessage());
//        }
//    }
//TODO
    private void handleQuery(CallbackQuery query) {
        Long chatId = query.getMessage().getChatId();
        Integer callbackMessageId = query.getMessage().getMessageId();
        UserState state = states.get(chatId);
        var answer = AnswerCallbackQuery.builder()
                .callbackQueryId(query.getId())
                .build();

        if (state == null || !state.getMessageId().equals(callbackMessageId)) {
            send(answer);
            return;
        }

        final Integer pivot = state.getPivot();
        final List<String> links = state.getLinks();

        switch (query.getData()) {
            case String data when data.equals("next") && pivot < links.size() - 1 -> state.setPivot(pivot + 1);
            case String data when data.equals("prev") && pivot > 0 -> state.setPivot(pivot - 1);
            case String data when data.equals("start") -> state.setPivot(0);
            case String data when data.equals("end") -> state.setPivot(links.size() - 1);
            default -> {
                send(answer);
                return;
            }
        }
        send(chatId, links.get(state.getPivot()), callbackMessageId, pivot);
    }

    private void send(SendMessage sendMessage, Long chatId) {
        try {
           states.get(chatId).messageId = client.execute(sendMessage).getMessageId();
        } catch (TelegramApiException e) {
            log.error(e.getLocalizedMessage());
        }
    }

    private void send(AnswerCallbackQuery answerCallbackQuery) {
        try {
            client.execute(answerCallbackQuery);
        } catch (TelegramApiException e) {
            log.error(e.getLocalizedMessage());
        }
    }

    private void send(Long chatId, String messageText, Integer messageId, Integer pivot) {
        try {
            var message = EditMessageText.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .text(messageText)
                    .replyMarkup(getMarkup(pivot))
                    .build();

            client.execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getLocalizedMessage());
        }
    }

    @Data
    static class UserState {
        private List<String> links;
        private Integer pivot;
        private Integer messageId;
    }
}
