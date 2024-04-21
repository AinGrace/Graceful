package aingrace.TGBot;

import aingrace.TGBot.Bot.Bot;
import aingrace.TGBot.HttpClients.WeatherHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
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
@EnableRedisRepositories
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public OkHttpTelegramClient telegramClient(Bot bot) {
        return new OkHttpTelegramClient(bot.getBotToken());
    }

    @Bean
    WeatherHttpClient weatherHttpClient(RestClient.Builder builder) {
        RestClient client = builder.baseUrl("http://api.weatherapi.com/v1").build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(client)).build();
        return factory.createClient(WeatherHttpClient.class);
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<?, ?> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }
}
