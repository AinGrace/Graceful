package aingrace.TGBot.Data.Weather;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Forecast(@JsonProperty("forecastday") ForecastDay[] forecastday) {}
