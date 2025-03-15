package es.uma.Metrics;

import es.uma.Utils;
import es.uma.Metrics.DTOs.MovieRecord;
import io.github.cdimascio.dotenv.Dotenv;

public class VideoClub implements IMetrics {

    private int validYear, validTitle, validGenre, validActors, validType;
    private int totalYear, totalTitle, totalGenre, totalActors, totalType;

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
    }


    private MovieRecord getMovie(String movieTitle) {
        Dotenv dotenv = Dotenv.load();
        String apiKey = dotenv.get("OMDB_KEY");
        String url = "http://www.omdbapi.com/?apikey=" + apiKey + "&t=" + movieTitle.replace(" ", "%20");
        return Utilities.getRequest(url, MovieRecord.class);
    }

    @Override
    public void calculate(String diagramPath, String instancePath) {
        String instance = Utils.readFile(instancePath);
        


    }

    @Override
    public void calculateInvalid(String diagramPath, String instancePath) {
        calculate(diagramPath, instancePath);
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
    }
    

    // Main for testing purposes
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        String apiKey = dotenv.get("OMDB_KEY");
        String movieTitle = "Guardians of the Galaxy Vol. 2";
        String url = "http://www.omdbapi.com/?apikey=" + apiKey + "&t=" + movieTitle.replace(" ", "%20");

        MovieRecord movie = Utilities.getRequest(url, MovieRecord.class);
        if (movie != null) {
            System.out.println("Title: " + movie.Title());
            System.out.println("Year: " + movie.Year());
            System.out.println("Genres: " + movie.getGenreList());
            System.out.println("Actors: " + movie.getActorsList());
            System.out.println("Type: " + movie.Type());
        }
    }
}
