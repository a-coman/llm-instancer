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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.uma.Metrics.DTOs.AddressRecord; // Ensure this class exists in the specified package
import io.github.cdimascio.dotenv.Dotenv;

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
            System.out.println("Status " + statusCode);
            
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
    
    public static String formatMetricRow(String label, int valid, int total) {
        double percentage = total > 0 ? (valid * 100.0) / total : 0.0;
        return String.format("| %s | %d | %d | %.2f%% |\n", 
            label, valid, total, percentage);
    }

    private static void waitForMS(long miliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(miliseconds);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isValidAddress(String country, String city, String street) {
        Dotenv dotenv = Dotenv.load();
        String apiKey = dotenv.get("GEOAPIFY_KEY");
        String url = "https://api.geoapify.com/v1/geocode/search?lang=en&type=street&format=json&apiKey=" + apiKey + "&city=" + city.replace(" ", "%20") + "&country=" + country.replace(" ", "%20") + "&street=" + street.replace(" ", "%20");
        AddressRecord addressRecord = Utilities.getRequest(url, AddressRecord.class);
        
        System.out.println("GET Address: " + street + ", " + city + ", " + country);
        System.out.println("Latitude: " + addressRecord.latitude());
        System.out.println("Longitude: " + addressRecord.longitude());
        System.out.println("Confidence: " + addressRecord.confidence());

        if (addressRecord.confidence()  > 0.8) {
            return true;
        }
        
        waitForMS(250); // 0.25 seconds api rate limit
        return false;
    }

    public static boolean isValidAddress(String city, String street) {
        return isValidAddress("", city, street);
    }

    public static boolean isValidAddress(String cityCountry) {
        Dotenv dotenv = Dotenv.load();
        String apiKey = dotenv.get("GEOAPIFY_KEY");
        String url = "https://api.geoapify.com/v1/geocode/search?lang=en&type=city&format=json&apiKey=" + apiKey + "&text=" + cityCountry.replace(" ", "%20");
        AddressRecord addressRecord = Utilities.getRequest(url, AddressRecord.class);
        
        System.out.println("GET Address: " + cityCountry);
        System.out.println("Latitude: " + addressRecord.latitude());
        System.out.println("Longitude: " + addressRecord.longitude());
        System.out.println("Confidence: " + addressRecord.confidence());

        if (addressRecord.confidence()  > 0.8) {
            return true;
        }
        
        waitForMS(250); // 0.25 seconds api rate limit
        return false;
    }

    // Main for testing purposes
    public static void main(String[] args) {
        Boolean result = isValidAddress("Spain","Malaga","José Ortega y Gasset");
        System.out.println("Result: " + result);
    }
}
