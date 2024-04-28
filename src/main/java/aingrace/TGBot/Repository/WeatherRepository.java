package aingrace.TGBot.Repository;

import aingrace.TGBot.Data.Weather.Location;
import aingrace.TGBot.Data.Weather.WeatherData;
import aingrace.TGBot.HttpClients.WeatherHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
public class WeatherRepository {

    private final WeatherHttpClient client;
    private final SetOperations<String, Location> setOperations;
    private final ValueOperations<String, WeatherData> valueOperations;
    private final HashOperations<String, String, String> hashOperations;

    @Value("${weatherApi.Key}")
    private String KEY;

    public WeatherRepository(WeatherHttpClient client, RedisTemplate commonTemplate) {
        this.client = client;
        this.setOperations = commonTemplate.opsForSet();
        this.valueOperations = commonTemplate.opsForValue();
        this.hashOperations = commonTemplate.opsForHash();
    }

    public Set<Location> findLocations(String city) {
        Set<Location> locations = setOperations.members(city.concat("Locations"));
        log.debug("Fetching db for {} locations", city);

        if (locations != null && !locations.isEmpty()) {
            log.debug("Returning locations");
            return locations;
        }

        log.debug("Fetching resulted in empty list, getting data from WeatherClient");
        locations = client.findLocations(KEY, city);

        if (locations.isEmpty()) {
            log.error("Can't get the data from WeatherClient for {}, returning empty SET", city);
            return Collections.emptySet();
        }

        locations.forEach(location -> {
            setOperations.add(city.concat("Locations"), location);
            hashOperations.putIfAbsent("CoordsToName",location.latitude() + " " + location.longitude(), location.name());
        });

        log.debug("Setting locations in db and returning locations");
        return locations;
    }

    public WeatherData forecastForCity(String city) {
        if (city.matches("(\\d+[.]\\d+ \\d+[.]\\d+)")) {
            city = hashOperations.get("CoordsToName", city);
        }

        WeatherData data = valueOperations.get(city);
        log.debug("Fetching db for {} forecast", city);

        if (data != null) {
            log.debug("Returning weather data");
            return data;
        }

        log.debug("Fetching resulted in null, getting data from WeatherClient");
        data = client.forecastForCity(KEY, "ru", city);

        if (data == null) {
            log.error("Can't get the data from WeatherClient, returning null");
            return null;
        }

        valueOperations.set(city, data, 10, TimeUnit.MINUTES);
        log.debug("Setting weather data in db and returning weather data");
        return data;
    }
}
