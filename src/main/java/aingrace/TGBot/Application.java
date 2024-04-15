package aingrace.TGBot;

import aingrace.TGBot.Bot.Bot;
import aingrace.TGBot.HttpClients.WeatherHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.starter.TelegramBotStarterConfiguration;

@Slf4j
@Import(TelegramBotStarterConfiguration.class)
@EnableScheduling
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public OkHttpTelegramClient telegramClient(Bot bot) {
        return new OkHttpTelegramClient(bot.getBotToken());
    }

    @Bean
    WeatherHttpClient weatherHttpClient() {
        RestClient client = RestClient.builder().baseUrl("http://api.weatherapi.com/v1").build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(client)).build();
        return factory.createClient(WeatherHttpClient.class);
    }

}
