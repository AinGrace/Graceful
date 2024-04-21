package aingrace.TGBot.Data.Weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ForecastDay(Day day) implements Serializable {}
