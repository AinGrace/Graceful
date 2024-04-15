package aingrace.TGBot.Data.Weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Day(@JsonProperty("maxtemp_c") int maxtempC,
                  @JsonProperty("mintemp_c") int mintempC,
                  @JsonProperty("avgtemp_c") int avgtempC,
                  @JsonProperty("maxwind_kph") int maxwindKph,
                  @JsonProperty("avghumidity") int avghumidity,
                  Condition condition) {}
