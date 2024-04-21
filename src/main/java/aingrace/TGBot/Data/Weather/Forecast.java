package aingrace.TGBot.Data.Weather;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public record Forecast(@JsonProperty("forecastday") ForecastDay[] forecastday) implements Serializable {}
