package es.uma.Metrics;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.uma.Utils;
import es.uma.Metrics.DTOs.AddressRecord;
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

    public static void waitForMS(long miliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(miliseconds);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isValidAddress(String country, String city, String street) {
        Dotenv dotenv = Dotenv.load();
        String apiKey = dotenv.get("GEOAPIFY_KEY");
        
        String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
        String encodedStreet = URLEncoder.encode(street, StandardCharsets.UTF_8);
        String url = "https://api.geoapify.com/v1/geocode/search?lang=en&limit=1&type=street&format=json&apiKey=" + apiKey + "&city=" + encodedCity + "&street=" + encodedStreet;
        
        if (!country.isEmpty()) {
            String encodedCountry = URLEncoder.encode(country, StandardCharsets.UTF_8);
            url += "&country=" + encodedCountry;
        }
        
        AddressRecord addressRecord = Utilities.getRequest(url, AddressRecord.class);
        
        System.out.println("GET Address: " + street + ", " + city + ", " + country);
        System.out.println("Latitude: " + addressRecord.latitude());
        System.out.println("Longitude: " + addressRecord.longitude());
        System.out.println("Confidence: " + addressRecord.confidence());

        if (addressRecord.confidence()  > 0.8) {
            System.out.println("Return: true");
            return true;
        }
        
        waitForMS(250); // 0.25 seconds api rate limit
        System.out.println("Return: false");
        return false;
    }

    public static boolean isValidAddress(String city, String street) {
        return isValidAddress("", city, street);
    }

    public static boolean isValidAddress(String address) {
        Dotenv dotenv = Dotenv.load();
        String apiKey = dotenv.get("GEOAPIFY_KEY");
        String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);
        String url = "https://api.geoapify.com/v1/geocode/search?lang=en&limit=1&format=json&apiKey=" + apiKey + "&text=" + encodedAddress;
        AddressRecord addressRecord = Utilities.getRequest(url, AddressRecord.class);
        
        System.out.println("GET Address: " + address);
        System.out.println("Latitude: " + addressRecord.latitude());
        System.out.println("Longitude: " + addressRecord.longitude());
        System.out.println("Confidence: " + addressRecord.confidence());

        if (addressRecord.confidence()  > 0.8) {
            System.out.println("Return: true");
            return true;
        }
        
        waitForMS(250); // 0.25 seconds api rate limit
        System.out.println("Return: false");
        return false;
    }

    public static boolean isValidAddress(String address, Double latitude, Double longitude) {
        Dotenv dotenv = Dotenv.load();
        String apiKey = dotenv.get("GEOAPIFY_KEY");

        String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);
        String url = "https://api.geoapify.com/v1/geocode/search?lang=en&format=json&limit=1&apiKey=" + apiKey + "&text=" + encodedAddress;
        AddressRecord addressRecord = Utilities.getRequest(url, AddressRecord.class);
        
        System.out.println("GET Address: " + address);
        System.out.println("Latitude: " + addressRecord.latitude());
        System.out.println("Longitude: " + addressRecord.longitude());
        System.out.println("Confidence: " + addressRecord.confidence());

        // Permissive 1.0 error margin
        if (addressRecord.confidence() > 0.8 && Math.abs(addressRecord.latitude() - latitude) < 1 && Math.abs(addressRecord.longitude() - longitude) < 1) {
            System.out.println("Return: true");
            return true;
        }
        
        waitForMS(250); // 0.25 seconds api rate limit
        System.out.println("Return: false");
        return false;
    }

    public static boolean isValidPhone(String phone) {
        String pattern = "^(\\+\\d{1,3}\\s?)?[0-9\\(\\)-.\\s]{6,15}$";
        
        // (\\+\\d{1,3}\\s?)? : Optional country code (e.g., +1, +44) with 1-3 digits, optional space after
        // [0-9\\(\\)-.\\s]{6,15} : 6 to 15 characters that can be:
        // Digits (0-9)
        // Parentheses ()
        // Hyphen -
        // Dot .
        // Space \s

        return Utils.validMatch(phone, pattern);

    }

    public static boolean isValidWebsite(String website) {
        String urlPattern = "^(https?://)?([\\w-]+\\.)?[\\w-]+(\\.[a-z]{2,})(:\\d+)?(/[\\w-./?%&=]*)?$";

        // optional http/https protocol
        // domain name (e.g., www.example)
        // top-level domain (e.g., .com)
        // optional port number
        // optional path/parameters

        return Utils.validMatch(website, urlPattern);
    }

    public static boolean isValidEmail(String email) {
        String emailPattern = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[\\w-]+\\.)*[\\w]+\\.[a-zA-Z]{2,}$";
        // local part: word chars + specials
        // optional dot-separated parts
        // @ symbol
        // domain: subdomains + main domain
        // TLD

        return Utils.validMatch(email, emailPattern);
    }

    public static Map<String, Map<String, String>> getMap(String instance, String pattern) {
        Map<String, Map<String, String>> map = new HashMap<>();

        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(instance);
        while (m.find()) {
            String entity = m.group(1);
            String attribute = m.group(2);
            String value = m.group(3).replace("'", "");
            
            map.putIfAbsent(entity, new HashMap<>());
            map.get(entity).put(attribute, value);
        }
        
        return map;
    }

    public static Map<String, String> getPairs(String instance, String pattern) {
        Map<String, String> pairs = new HashMap<>();

        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(instance);

        while (m.find()) {
            String firstId = m.group(1);
            String secondId = m.group(2);
            pairs.put(firstId, secondId);
        }

        return pairs;
    }

    public static String getStringList(String label, ArrayList<String> list) {
        StringBuilder sb = new StringBuilder();
        if(list.isEmpty()){
            return "";
        }
        sb.append("\n| ").append(label).append(" | \n");
        sb.append("|---| \n");
        list.forEach(error -> sb.append("```\n").append(error).append("\n```\n"));
        return sb.toString();
    }

    // Main for testing purposes
    public static void main(String[] args) {
        Boolean result = isValidAddress("Spain","Malaga","José Ortega y Gasset");
        System.out.println("Result: " + result);
    }
}
