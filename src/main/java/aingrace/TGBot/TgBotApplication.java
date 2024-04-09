package aingrace.TGBot;

import aingrace.TGBot.Bot.Bot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@SpringBootApplication
public class TgBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(TgBotApplication.class, args);
    }

    @Bean
    public TelegramBotsLongPollingApplication telegramBotsLongPollingApplication(Bot bot) {
        TelegramBotsLongPollingApplication application = new TelegramBotsLongPollingApplication();
        try {
            application.registerBot(bot.getBotToken(), bot);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        return application;
    }

    @Bean
    public OkHttpTelegramClient telegramClient(Bot bot) {
        return new OkHttpTelegramClient(bot.getBotToken());
    }
}
