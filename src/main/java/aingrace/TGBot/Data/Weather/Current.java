package aingrace.TGBot.Data.Weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Current(@JsonProperty("last_updated") String lastUpdated,
               @JsonProperty("temp_c") int temperature,
               @JsonProperty("is_day") boolean isDay,
               @JsonProperty("condition") Condition condition,
               @JsonProperty("wind_kmp") int windKph,
               @JsonProperty("wind_degree") int windDegree,
               @JsonProperty("wind_dir") String windDirection,
               @JsonProperty("humidity") int humidity,
               @JsonProperty("feelslike_c") int feelsLikeC) implements Serializable {}
