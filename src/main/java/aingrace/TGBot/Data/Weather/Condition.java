package aingrace.TGBot.Data.Weather;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public record Condition(@JsonProperty("text") String text,
                        @JsonProperty("icon") String icon,
                        @JsonProperty("code") int code) implements Serializable { }
