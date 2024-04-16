package aingrace.TGBot.Handlers;

import aingrace.TGBot.Data.Weather.Location;
import aingrace.TGBot.Data.Weather.WeatherData;
import aingrace.TGBot.HttpClients.WeatherHttpClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherHandler implements UpdateHandler {

    private final WeatherHttpClient weatherClient;
    private final TelegramClient client;

    private WeatherData weather;

    @Value("${weatherApi.Key}")
    private String key;

    @Override
    public void handle(Update update) {
        getCallbackQuery(update).filter(query -> query.getData().startsWith("current")).ifPresent(this::handleQuery);
        getCallbackQuery(update).filter(query -> query.getData().startsWith("today")).ifPresent(this::handleQuery);
        getCallbackQuery(update).filter(query -> query.getData().startsWith("WH")).ifPresent(this::handleQuery);

        if (getText(update).filter(text -> text.matches("/погода \\w+")).isEmpty()) return;

        String city = getText(update).orElseThrow().substring(8);
        long chatId = getChatId(update).orElseThrow();
        var messageBuilder = SendMessage.builder()
                .chatId(chatId)
                .parseMode("HTML");

        Set<Location> locations = weatherClient.findLocations(key, city);
        String forecast;
        if (locations.isEmpty()) forecast = "<b>can't get the weather data for " + city + "</b>";
        else if (locations.size() == 1) {
            this.weather = weatherClient.forecastForCity(key, "ru", city);
            forecast = this.weather.currentWeather();
            messageBuilder.replyMarkup(getMarkups("current"));
        }
        else {
            forecast = String.format(
                    "<b>Было найдено несколько городов с именем %s \nПожалуйста выберите одну из них</b>", city);
            messageBuilder.replyMarkup(getMarkupForLocations(locations));
        }

        SendMessage sendMessage = messageBuilder.text(forecast).build();
        send(client, sendMessage);
    }

    private void handleQuery(CallbackQuery query) {
        Long callbackChatId = query.getMessage().getChatId();
        Integer callbackMessageId = query.getMessage().getMessageId();
        String weatherText = "";
        if (query.getData().equals("today")) weatherText = this.weather.todayWeather();
        else if (query.getData().equals("current"))weatherText = this.weather.currentWeather();
        else if (query.getData().startsWith("WH")) {
            String coordinates = query.getData().substring(2);
            query.setData("current");
            this.weather = weatherClient.forecastForCity(key, "ru", coordinates);
            weatherText = this.weather.currentWeather();
        }

        var editMessageText = EditMessageText.builder()
                .chatId(callbackChatId)
                .messageId(callbackMessageId)
                .text(weatherText)
                .replyMarkup(getMarkups(query.getData()))
                .parseMode("HTML")
                .build();

        send(client, editMessageText);
    }

    private InlineKeyboardMarkup getMarkups(String markup) {
        var currentButton = InlineKeyboardButton.builder()
                .text("Погода на сегодня")
                .callbackData("today")
                .build();

        var todayButton = InlineKeyboardButton.builder()
                .text("Погода на данный момент")
                .callbackData("current")
                .build();

        var currentRows = List.of(new InlineKeyboardRow(currentButton));
        var todayRows = List.of(new InlineKeyboardRow(todayButton));

        var currentMarkup= new InlineKeyboardMarkup(currentRows);
        var todayMarkup = new InlineKeyboardMarkup(todayRows);

        if (markup.equals("today")) return todayMarkup;
        else if (markup.equals("current")) return currentMarkup;
        else return null;
    }

    private @NotNull InlineKeyboardMarkup getMarkupForLocations(Set<Location> locations) {
        var buttons = locations.stream().map(location -> InlineKeyboardButton.builder()
                .text(location.country() + "-" + location.name())
                .callbackData("WH" + location.coords())
                .build()).toList();

        var rows = buttons.stream()
                .map(InlineKeyboardRow::new)
                .toList();

        return new InlineKeyboardMarkup(rows);
    }

//    @Scheduled(cron = "3 * * * * *") TODO
//    private void sendWeatherAtMidnight() {
//        String weather = weatherClient.forecastForCity(key, "ru", "Baku").todayWeather();
//        var sendMessage = SendMessage.builder().chatId(5012958501L).text(weather).parseMode("HTML").build();
//
//        send(client, sendMessage);
//    }

    private void send(TelegramClient client, SendMessage sendMessage) {
        try {
            client.execute(sendMessage);
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
}
