package aingrace.TGBot.Handlers;

import aingrace.TGBot.Data.Weather.Location;
import aingrace.TGBot.Data.Weather.WeatherData;
import aingrace.TGBot.Repository.WeatherRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Lazy
@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherHandler implements UpdateHandler {

    private final Map<String, InlineKeyboardMarkup> markups = new HashMap<>();
    private final WeatherRepository repository;
    private final TelegramClient client;

    private WeatherData weather;
    private int messageId;

    @Override
    public void handle(Update update) {
        getCallbackQuery(update).ifPresent(this::handleQuery);

        getText(update).filter(text -> text.matches("/weather \\w+"))
                .ifPresent(_ -> handleUpdate(update));
    }

    private void handleUpdate(Update update) {
        markups.remove("Locations");

        String city = getText(update).orElseThrow().split(" ")[1].toLowerCase();
        long chatId = getChatId(update).orElseThrow();
        var messageBuilder = SendMessage.builder()
                .chatId(chatId)
                .parseMode("HTML");

        var locations = repository.findLocations(city);
        String forecast;
        if (locations.isEmpty()) {
            forecast = "<b>can't get the weather data for " + city + "</b>";
        } else if (locations.size() == 1) {
            weather = repository.forecastForCity(city);
            forecast = weather.currentWeather();
            messageBuilder.replyMarkup(markups.get("current"));
        } else {
            forecast = String.format(
                    "<b>Found multiple cities with name %s</b>", city);
            markups.putIfAbsent("Locations", getMarkupForLocations(locations));
            messageBuilder.replyMarkup(markups.get("Locations"));
        }

        SendMessage sendMessage = messageBuilder.text(forecast).build();
        send(client, sendMessage);
    }

    private void handleQuery(CallbackQuery query) {
        Long callbackChatId = query.getMessage().getChatId();
        Integer callbackMessageId = query.getMessage().getMessageId();
        InlineKeyboardMarkup markup;
        String weatherText;

        if (callbackMessageId != messageId) {
            AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery(query.getId());
            answerCallbackQuery.setText("Сообщение устарело");
            send(client, answerCallbackQuery);
            return;
        }

        switch (query.getData()) {
            case String data when data.equals("today") -> {
                markup = markups.get(data);
                weatherText = weather.todayWeather();
            }
            case String data when data.equals("current") -> {
                markup = markups.get(data);
                weatherText = weather.currentWeather();
            }
            case String data when data.equals("return") -> {
                markup = markups.get("Locations");
                weatherText = "<b>Выберите город</b>";
            }
            case String data when data.equals("todayReturn") -> {
                markup = markups.get(data);
                weatherText = weather.todayWeather();
            }
            case String data when data.equals("currentReturn") -> {
                markup = markups.get(data);
                weatherText = weather.currentWeather();
            }
            case String data when data.startsWith("WH") -> {
                String coordinates = data.substring(3);
                weather = repository.forecastForCity(coordinates);
                weatherText = weather.currentWeather();
                markup = markups.get("currentReturn");
            }
            default -> {
                return;
            }
        }

        var editMessageText = EditMessageText.builder()
                .chatId(callbackChatId)
                .messageId(callbackMessageId)
                .text(weatherText)
                .replyMarkup(markup)
                .parseMode("HTML")
                .build();

        send(client, editMessageText);
    }

    @PostConstruct
    private void initMarkups() {
        var currentButton = InlineKeyboardButton.builder()
                .text("Погода на сегодня")
                .callbackData("today")
                .build();

        var todayButton = InlineKeyboardButton.builder()
                .text("Погода на данный момент")
                .callbackData("current")
                .build();

        var currentReturnButton = InlineKeyboardButton.builder()
                .text("Погода на сегодня")
                .callbackData("todayReturn")
                .build();

        var todayReturnButton = InlineKeyboardButton.builder()
                .text("Погода на данный момент")
                .callbackData("currentReturn")
                .build();

        var returnButton = InlineKeyboardButton.builder()
                .text("<--")
                .callbackData("return")
                .build();

        var currentRow = new InlineKeyboardRow(currentButton);
        var currentReturnRow = new InlineKeyboardRow(currentReturnButton, returnButton);

        var todayRow = new InlineKeyboardRow(todayButton);
        var todayReturnRow = new InlineKeyboardRow(todayReturnButton, returnButton);

        var currentRows = List.of(new InlineKeyboardRow(currentRow));
        var currentReturnRows = List.of(new InlineKeyboardRow(currentReturnRow));

        var todayRows = List.of(new InlineKeyboardRow(todayRow));
        var todayReturnRows = List.of(new InlineKeyboardRow(todayReturnRow));

        var currentMarkup = new InlineKeyboardMarkup(currentRows);
        var currentReturnMarkup = new InlineKeyboardMarkup(currentReturnRows);

        var todayMarkup = new InlineKeyboardMarkup(todayRows);
        var todayReturnMarkup = new InlineKeyboardMarkup(todayReturnRows);

        markups.put("current", currentMarkup);
        markups.put("currentReturn", currentReturnMarkup);
        markups.put("today", todayMarkup);
        markups.put("todayReturn", todayReturnMarkup);
    }

    private InlineKeyboardMarkup getMarkupForLocations(Collection<Location> locations) {
        var buttons = locations.stream()
                .map(location -> InlineKeyboardButton.builder()
                        .text(location.country() + "-" + location.name())
                        .callbackData("WH " + location.coords())
                        .build())
                .toList();

        var rows = buttons.stream()
                .map(InlineKeyboardRow::new)
                .toList();

        return new InlineKeyboardMarkup(rows);
    }

    private void send(TelegramClient client, SendMessage sendMessage) {
        try {
            Message execute = client.execute(sendMessage);
            messageId = execute.getMessageId();
        } catch (TelegramApiException e) {
            log.error(e.getLocalizedMessage());
        }
    }

    private void send(TelegramClient client, EditMessageText editMessageText) {
        try {
            client.execute(editMessageText);
        } catch (TelegramApiException e) {
            log.error(e.getLocalizedMessage());
        }
    }

    private void send(TelegramClient client, AnswerCallbackQuery answerCallbackQuery) {
        try {
            client.execute(answerCallbackQuery);
        } catch (TelegramApiException e) {
            log.error(e.getLocalizedMessage());
        }
    }
}