package es.uma.Metrics.Specific;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.uma.Utils;
import es.uma.Metrics.IMetrics;
import es.uma.Metrics.Utilities;
import es.uma.Metrics.DTOs.MovieRecord;
import io.github.cdimascio.dotenv.Dotenv;

public class VideoClub implements IMetrics {

    private int validYear, validTitle, validGenre, validActors, validType;
    private int totalYear, totalTitle, totalGenre, totalActors, totalType;
    private ArrayList<String> invalidYears, invalidTitles, invalidGenres, invalidActors, invalidTypes;

    public VideoClub() {
        validYear = 0;
        validTitle = 0;
        validGenre = 0;
        validActors = 0;
        validType = 0;

        totalYear = 0;
        totalTitle = 0;
        totalGenre = 0;
        totalActors = 0;
        totalType = 0;

        invalidYears = new ArrayList<>();
        invalidTitles = new ArrayList<>();
        invalidGenres = new ArrayList<>();
        invalidActors = new ArrayList<>();
        invalidTypes = new ArrayList<>();
    }

    private static List<MovieRecord> parseMovieRecords(String input) {
        Map<String, Map<String, String>> items = new HashMap<>();
        Map<String, List<String>> movieToActors = new HashMap<>();
        Map<String, String> rentalDates = new HashMap<>();
        Map<String, String> movieToRental = new HashMap<>();
        List<MovieRecord> results = new ArrayList<>();

        // Process each line
        for (String line : input.split("\n")) {
            line = line.trim();
            
            if (line.startsWith("!new ")) {
                processNewObject(line, items);
            } else if (line.startsWith("!") && line.contains(":=")) {
                processProperty(line, items, rentalDates);
            } else if (line.contains("into")) {
                processRelationship(line, movieToActors, movieToRental);
            }
        }

        // Create MovieRecords
        items.forEach((id, item) -> {
            if (isMovieOrSeries(item)) {
                MovieRecord record = createMovieRecord(id, item, items, movieToActors, movieToRental, rentalDates);
                if (record != null) {
                    results.add(record);
                }
            }
        });
        
        return results;
    }

    private static void processNewObject(String line, Map<String, Map<String, String>> items) {
        Matcher matcher = Pattern.compile("!new (Movie|Series|Actor|Rental)\\('(\\w+)'\\)").matcher(line);
        if (matcher.find()) {
            String type = matcher.group(1).toLowerCase();
            String id = matcher.group(2);
            items.put(id, new HashMap<>(Map.of("type", type)));
        }
    }

    private static void processProperty(String line, Map<String, Map<String, String>> items, 
                                        Map<String, String> rentalDates) {
        Matcher matcher = Pattern.compile("!(\\w+)\\.(\\w+) := (.+)").matcher(line);
        if (matcher.find()) {
            String id = matcher.group(1);
            String prop = matcher.group(2);
            String rawValue = matcher.group(3).trim().replaceAll("['\"]", "");
            
            // Process the value outside lambda to maintain effective finality
            final String processedValue = processValue(rawValue, id, rentalDates);
            
            items.computeIfPresent(id, (k, v) -> {
                v.put(prop, processedValue);
                return v;
            });
        }
    }

    private static String processValue(String value, String id, Map<String, String> rentalDates) {
        if (value.startsWith("Date(")) {
            String dateValue = value.substring(5, value.length() - 1);
            rentalDates.put(id, dateValue);
            return dateValue;
        } else if (value.startsWith("#")) {
            return value.substring(1);
        }
        return value;
    }

    private static void processRelationship(String line, Map<String, List<String>> movieToActors,
                                          Map<String, String> movieToRental) {
        if (line.contains("into CassetteActor")) {
            Matcher matcher = Pattern.compile("!insert \\((\\w+), (\\w+)\\) into CassetteActor").matcher(line);
            if (matcher.find()) {
                movieToActors.computeIfAbsent(matcher.group(1), k -> new ArrayList<>())
                            .add(matcher.group(2));
            }
        } else if (line.contains("into RentalCassette")) {
            Matcher matcher = Pattern.compile("!insert \\((\\w+), (\\w+)\\) into RentalCassette").matcher(line);
            if (matcher.find()) {
                String rentalId = matcher.group(1);
                String movieId = matcher.group(2);
                // Map from movie/series to rental
                movieToRental.put(movieId, rentalId);
            }
        }
    }

    private static boolean isMovieOrSeries(Map<String, String> item) {
        String type = item.get("type");
        return type != null && (type.equals("movie") || type.equals("series"));
    }

    private static MovieRecord createMovieRecord(String id, Map<String, String> item, Map<String, Map<String, String>> items, Map<String, List<String>> movieToActors, Map<String, String> movieToRental,Map<String, String> rentalDates) {
        String title = item.get("title");
        String genre = item.get("genre");
        String type = item.get("type");
        
        // Get all actors if they exist
        List<String> actorNames = new ArrayList<>();
        if (movieToActors.containsKey(id)) {
            for (String actorId : movieToActors.get(id)) {
                String actorName = Optional.ofNullable(items.get(actorId))
                                         .map(actor -> actor.get("name"))
                                         .orElse(null);
                if (actorName != null) {
                    actorNames.add(actorName);
                }
            }
        }

        // Get year from rental date if it exists
        String year = Optional.ofNullable(movieToRental.get(id))
                             .map(rentalDates::get)
                             .map(date -> date.substring(0, 4))
                             .orElse(null);

        return new MovieRecord(
            title,
            year,
            genre,
            actorNames.isEmpty() ? null : String.join(", ", actorNames),
            type
        );
    }

    private MovieRecord getMovieRecord(String movieTitle) {
        Dotenv dotenv = Dotenv.load();
        String apiKey = dotenv.get("OMDB_KEY");
        String encodedTitle = URLEncoder.encode(movieTitle, StandardCharsets.UTF_8);
        String url = "http://www.omdbapi.com/?apikey=" + apiKey + "&t=" + encodedTitle;
        return Utilities.getRequest(url, MovieRecord.class);
    }

    @Override
    public void calculate(String diagramPath, String instancePath) {
        String instance = Utils.readFile(instancePath);
        List<MovieRecord> movieRecords = parseMovieRecords(instance);

        for (MovieRecord movieRecord : movieRecords) {
            String title = movieRecord.Title();
            totalTitle++;
            MovieRecord apiMovieRecord = getMovieRecord(title);
            Utilities.waitForMS(100); // To avoid rate limiting
    
            System.out.println("API   : " + apiMovieRecord);
            System.out.println("Parsed: " + movieRecord);
    
            if (apiMovieRecord != null && apiMovieRecord.Title() != null) {
                validTitle++;
                // Compare Year if both exist
                if (movieRecord.Year() != null && apiMovieRecord.Year() != null) {
                    totalYear++;
                    try {
                        // Rental year > movie release year
                        if (Integer.parseInt(movieRecord.Year()) >= Integer.parseInt(apiMovieRecord.Year())) {
                            validYear++;
                        } else {
                            invalidYears.add("Rental year: " + movieRecord.Year() + " < " + "Release year: " + apiMovieRecord.Year()); 
                        }
                    } catch (NumberFormatException e) {
                        // Skip invalid year formats
                    }
                }
    
                // Compare Type if both exist
                if (movieRecord.Type() != null && apiMovieRecord.Type() != null) {
                    totalType++;
                    if (apiMovieRecord.Type().equalsIgnoreCase(movieRecord.Type())) {
                        validType++;
                    } else {
                        invalidTypes.add("Parsed type: " + movieRecord.Type() + " != " + "API type: " + apiMovieRecord.Type());
                    }
                }
    
                // Compare Actors if both exist
                if (movieRecord.getActorsList() != null && apiMovieRecord.getActorsList() != null) {
                    for (String actor : movieRecord.getActorsList()) {
                        totalActors++;
                        if (apiMovieRecord.getActorsList().stream()
                                .anyMatch(apiActor -> apiActor.equalsIgnoreCase(actor))) {
                            validActors++;
                        } else {
                            invalidActors.add(actor + " for movie: " + movieRecord.Title());
                        }
                    }
                }
    
                // Compare Genres if both exist
                if (movieRecord.getGenreList() != null && apiMovieRecord.getGenreList() != null) {
                    for (String genre : movieRecord.getGenreList()) {
                        totalGenre++;
                        if (apiMovieRecord.getGenreList().stream()
                                .anyMatch(apiGenre -> apiGenre.equalsIgnoreCase(genre))) {
                            validGenre++;
                        } else {
                            invalidGenres.add("Parsed genre: " + genre + " != " + "API genre: " + apiMovieRecord.getGenreList());
                        }
                    }
                }
            } else {
                invalidTitles.add(movieRecord.Title());
            }
            
        }

        System.out.println("Valid Title: " + validTitle + "/" + totalTitle); 
        System.out.println("Valid Year: " + validYear + "/" + totalYear);
        System.out.println("Valid Genre: " + validGenre + "/" + totalGenre);
        System.out.println("Valid Actors: " + validActors + "/" + totalActors);
        System.out.println("Valid Type: " + validType + "/" + totalType);

    }

    @Override
    public void aggregate(IMetrics otherMetrics) {
        if (!(otherMetrics instanceof VideoClub)) {
            return;
        }
        
        VideoClub other = (VideoClub) otherMetrics; 
        this.validYear += other.validYear;
        this.validTitle += other.validTitle;
        this.validGenre += other.validGenre;
        this.validActors += other.validActors;
        this.validType += other.validType;

        this.totalYear += other.totalYear;
        this.totalTitle += other.totalTitle;
        this.totalGenre += other.totalGenre;
        this.totalActors += other.totalActors;
        this.totalType += other.totalType;

        this.invalidYears.addAll(other.invalidYears);
        this.invalidTitles.addAll(other.invalidTitles);
        this.invalidGenres.addAll(other.invalidGenres);
        this.invalidActors.addAll(other.invalidActors);
        this.invalidTypes.addAll(other.invalidTypes);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("| Videoclub | Invalid | Total | Failure (%) | \n");
        sb.append("|---|---|---|---| \n");
        sb.append(Utilities.formatMetricRow("Titles", totalTitle - validTitle, totalTitle))
          .append(Utilities.formatMetricRow("Types (out of valid Titles)", totalType - validType, totalType))
          .append(Utilities.formatMetricRow("Genres (out of valid Titles)", totalGenre - validGenre, totalGenre))
          .append(Utilities.formatMetricRow("Actors (out of valid Titles)", totalActors - validActors, totalActors))
          .append(Utilities.formatMetricRow("Release year > Rental year (out of valid Titles)", totalYear - validYear, totalYear));

        sb.append(Utilities.getStringList("Failed Titles", invalidTitles))
          .append(Utilities.getStringList("Failed Types", invalidTypes))
          .append(Utilities.getStringList("Failed Genres", invalidGenres))
          .append(Utilities.getStringList("Failed Actors", invalidActors))
          .append(Utilities.getStringList("Failed Years", invalidYears));

        return sb.toString();
    }
    

    // Main for testing purposes
    public static void main(String[] args) {
        String instancePath = "./src/main/resources/instances/CoT/videoclub/GPT_4O/16-03-2025--12-22-36/gen1/baseline.soil";
        // Dotenv dotenv = Dotenv.load();
        // String apiKey = dotenv.get("OMDB_KEY");
        // String movieTitle = "Guardians of the Galaxy Vol. 2";
        // String url = "http://www.omdbapi.com/?apikey=" + apiKey + "&t=" + movieTitle.replace(" ", "%20");

        // MovieRecord movie = Utilities.getRequest(url, MovieRecord.class);
        // if (movie != null) {
        //     System.out.println("Title: " + movie.Title());
        //     System.out.println("Year: " + movie.Year());
        //     System.out.println("Genres: " + movie.getGenreList());
        //     System.out.println("Actors: " + movie.getActorsList());
        //     System.out.println("Type: " + movie.Type());
        // }

        VideoClub videoClub = new VideoClub();
        videoClub.calculate("diagramPath", instancePath);
        System.out.println("\n\n"+videoClub);


    }
}
