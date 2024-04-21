package aingrace.TGBot.Data.Weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
//@RedisHash
public record Location(@Id String id,
                       @JsonProperty("name") String name,
                       @JsonProperty("region") String region,
                       @JsonProperty("country") String country,
                       @JsonProperty("lat") double latitude,
                       @JsonProperty("lon") double longitude) implements Serializable {

    public String coords() {
        return latitude + " " + longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Objects.equals(name, location.name) && Objects.equals(region, location.region) && Objects.equals(country, location.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, country, region);
    }
}
