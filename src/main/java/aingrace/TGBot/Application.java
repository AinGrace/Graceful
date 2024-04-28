package aingrace.TGBot;

import aingrace.TGBot.Bot.Bot;
import aingrace.TGBot.HttpClients.WeatherHttpClient;
import com.apptasticsoftware.rssreader.RssReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.starter.TelegramBotStarterConfiguration;

@Slf4j
@Import(TelegramBotStarterConfiguration.class)
@EnableAsync
@EnableScheduling
@SpringBootApplication
//@EnableRedisRepositories
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public OkHttpTelegramClient telegramClient(Bot bot) {
        return new OkHttpTelegramClient(bot.getBotToken());
    }

    @Bean
    public WeatherHttpClient weatherHttpClient(RestClient.Builder builder) {
        RestClient client = builder.baseUrl("https://api.weatherapi.com/v1").build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(client)).build();
        return factory.createClient(WeatherHttpClient.class);
    }

    @Bean
    public RssReader rssReader() {
        return new RssReader();
    }

    @Bean
    public RedisTemplate<String, Object> commonTemplate(RedisConnectionFactory redisConnectionFactory) {
        final RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setValueSerializer(new JdkSerializationRedisSerializer());
        return template;
    }
}
