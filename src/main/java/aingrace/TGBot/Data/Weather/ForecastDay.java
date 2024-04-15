package aingrace.TGBot.Data.Weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown = true)
public record ForecastDay(Day day) {}
