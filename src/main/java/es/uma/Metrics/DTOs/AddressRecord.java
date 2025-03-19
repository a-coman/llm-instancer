package es.uma.Metrics.DTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AddressRecord(@JsonProperty("results") Result[] results) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Result(double lat, double lon, Rank rank){}
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Rank(double confidence){}

    // Only return the first result
    public double latitude() {
        return (results != null && results.length > 0) ? results[0].lat() : Double.NaN;
    }

    public double longitude() {
        return (results != null && results.length > 0) ? results[0].lon() : Double.NaN;
    }

    public double confidence() {
        return (results != null && results.length > 0) ? results[0].rank().confidence() : Double.NaN;
    }
}
