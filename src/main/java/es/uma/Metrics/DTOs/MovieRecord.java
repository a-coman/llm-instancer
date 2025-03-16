package es.uma.Metrics.DTOs;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MovieRecord(
    String Title, 
    String Year, 
    String Genre, 
    String Actors, 
    String Type) {

    public String Year() {
        return Year != null ? Year.substring(0, 4) : null; // Use only the first year encountered 
    }

    public List<String> getGenreList() {
        if (Genre == null || Genre.isEmpty()) {
            return List.of();
        }
        return Arrays.asList(Genre.split(", "));
    }

    public List<String> getActorsList() {
        if (Actors == null || Actors.isEmpty()) {
            return List.of();
        }
        return Arrays.asList(Actors.split(", "));
    }
}
