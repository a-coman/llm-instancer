package es.uma.Metrics;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Utilities {

    private static final Map<Locale, Set<String>> COUNTRY_NAMES_CACHE = new HashMap<>();

    // Method to check if a country name is valid in any locale
    public static boolean isValidCountryName(String countryName) {
        // Iterate through all available locales
        for (Locale locale : Locale.getAvailableLocales()) {
            // Get the set of country names for the current locale (cached)
            Set<String> countryNames = getCountryNames(locale);

            // Check if the input country name exists in this locale
            if (countryNames.contains(countryName)) {
                return true; // Valid in at least one locale
            }
        }
        return false; // Not valid in any locale
    }

    // Helper method to get country names for a specific locale (with caching)
    private static Set<String> getCountryNames(Locale locale) {
        return COUNTRY_NAMES_CACHE.computeIfAbsent(locale, l ->
                Arrays.stream(Locale.getISOCountries())
                        .map(code -> new Locale("", code).getDisplayCountry(l))
                        .collect(Collectors.toSet()));
    }

    // Method to get request to URL and parse to DTO
    public static <T> T getRequest(String URL, Class<T> responseType) {
        ObjectMapper objectMapper = new ObjectMapper();
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create(URL)).GET().build();

        try {
            HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

            int statusCode = response.statusCode();
            System.out.println("GET" + URL + " : " + "Status " + statusCode);
            
            if (statusCode == 200) {
                return objectMapper.readValue(response.body(), responseType); 
            } else {
                System.err.println("API request failed with status code: " + statusCode); 
                return(null);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
