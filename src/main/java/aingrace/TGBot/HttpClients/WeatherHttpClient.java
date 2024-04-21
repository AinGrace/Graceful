package aingrace.TGBot.HttpClients;

import aingrace.TGBot.Data.Weather.Location;
import aingrace.TGBot.Data.Weather.WeatherData;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

import java.util.Set;

public interface WeatherHttpClient {
    @GetExchange("/forecast.json?key={key}&lang={lang}&q={city}")
    WeatherData forecastForCity(@PathVariable String key, @PathVariable String lang, @PathVariable String city);

    @GetExchange("/search.json?key={key}&q={city}")
    Set<Location> findLocations(@PathVariable String key, @PathVariable String city);
}
