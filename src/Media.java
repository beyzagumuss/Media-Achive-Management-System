public class Media {
    private String url,
            title,
            type,
            platform,
            availableCountries,
            imdbId;
    private ListInterface<String> genresList = new AList<>();
    private int releaseYear;
    private double imdbAverageRating;
    private int imdbNumVotes;
    private ListInterface<MediaPlatform> mediaPlatformList = new AList<>();//MediaPlatform class holds media's available platform and countries. This list contains MediaPlatform's instances i.e multiple platforms and countries.

    public Media(String url, String title, String type, String genres, int releaseYear, String imdbId, double imdbAverageRating, int imdbNumVotes, String platform, String availableCountries){
        this.url = url;
        this.title = title;
        this.type = type;

        String[] words = genres.split(" ");
        for (String word : words) {
            genresList.add(word);
        }

        this.platform = platform;
        this.availableCountries = availableCountries;
        this.releaseYear = releaseYear;
        this.imdbId = imdbId;
        this.imdbAverageRating = imdbAverageRating;
        this.imdbNumVotes = imdbNumVotes;
        mediaPlatformList.add(new MediaPlatform(platform,availableCountries));
    }

    public double getImdbAverageRating() {
        return imdbAverageRating;
    }

    public String getPlatformsAsString(){
        return platform;
    }
    public String getAvailableCountriesAsString(){
        return availableCountries;
    }

    public String getTitle() {
        return title;
    }

    public int getImdbNumVotes() {
        return imdbNumVotes;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public String getImdbId() {
        return imdbId;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public ListInterface<MediaPlatform> getMediaPlatformList() {
        return mediaPlatformList;
    }

    public void addPlatform(String platform, String availableCountries){
        mediaPlatformList.add(new MediaPlatform(platform,availableCountries));
    }

    public void display(){
        System.out.println("Type: " + type);

        System.out.print("Genre: ");
        for (int i = 1; i < genresList.getLength(); i++) {
            System.out.print(genresList.getEntry(i)+", ");
        }
        System.out.println(genresList.getEntry(genresList.getLength()));

        System.out.println("Release Year: " + releaseYear);
        System.out.println("IMDb ID: " + imdbId);
        System.out.println("Rating: " + imdbAverageRating);
        System.out.println("Number of Votes: " + imdbNumVotes);
        System.out.println();

        int size = mediaPlatformList.getLength();
        System.out.println(size + " platforms found for " + title);
        System.out.println();

        for (int i = 1; i <= size; i++) {
            MediaPlatform temp = mediaPlatformList.getEntry(i);
            System.out.print(temp.platform + " - ");
            for (int j = 1; j < temp.availableCountriesList.getLength(); j++) {
                System.out.print(temp.availableCountriesList.getEntry(j)+", ");
            }
            System.out.println(temp.availableCountriesList.getEntry(temp.availableCountriesList.getLength()));
        }
        System.out.println();
    }

    public void displayForCountrySearch(int count){
        System.out.print(count+".");
        System.out.println(title.toUpperCase());
        System.out.println("Type: " + type);
        System.out.print("Genre: ");
        for (int i = 1; i < genresList.getLength(); i++) {
            System.out.print(genresList.getEntry(i)+", ");
        }
        System.out.println(genresList.getEntry(genresList.getLength()));

        System.out.println("Release Year: " + releaseYear);
        System.out.println("IMDb ID: " + imdbId);
        System.out.println("Rating: " + imdbAverageRating);
        System.out.println("Number of Votes: " + imdbNumVotes);
        System.out.println();

        int size = mediaPlatformList.getLength();
        System.out.println(size + " platform");
        for (int i = 1; i <= size; i++) {
            MediaPlatform temp = mediaPlatformList.getEntry(i);
            System.out.println(" - "+temp.platform );
        }
        System.out.println("----------------------");



    }

    public void displayForAllFivePlatformSearch(int count){
        System.out.print(count+".");
        System.out.println(title);
    }
    public class MediaPlatform {
        public String platform;
        public ListInterface<String> availableCountriesList = new AList<>();
        MediaPlatform(String platform, String availableCountries){
            this.platform = platform;
            String[] str = availableCountries.split(" ");
            for(String s : str){
                availableCountriesList.add(s);
            }
        }
    }
}
