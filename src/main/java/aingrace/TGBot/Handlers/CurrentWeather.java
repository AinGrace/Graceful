package aingrace.TGBot.Handlers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


public record CurrentWeather(Location location, Current current) { }

@JsonIgnoreProperties(ignoreUnknown = true)
record Location(@JsonProperty("name") String name,
                @JsonProperty("country") String country,
                @JsonProperty("lat") double latency,
                @JsonProperty("lon") double longitude) { }

@JsonIgnoreProperties(ignoreUnknown = true)
record Current(@JsonProperty("last_updated") String lastUpdated,
               @JsonProperty("temp_c") double temperature,
               @JsonProperty("is_day") boolean isDay,
               @JsonProperty("condition") Condition condition,
               @JsonProperty("wind_kmp") double windKph,
               @JsonProperty("wind_degree") double windDegree,
               @JsonProperty("wind_dir") String windDirection,
               @JsonProperty("humidity") int humidity,
               @JsonProperty("feelslike_c") double feelsLikeC) { }

record Condition(@JsonProperty("text") String text,
                 @JsonProperty("icon") String icon,
                 @JsonProperty("code") int code) { }