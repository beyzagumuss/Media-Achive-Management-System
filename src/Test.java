import hash.HashFunction;
import hash.PAFHashFunction;
import hash.SSFHashFunction;
import probing.DoubleHashing;
import probing.LinearProbing;
import probing.ProbingStrategy;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
        HashedDictionary<String, Media> moviesDictionary = null;
        Scanner scanner = new Scanner(System.in);
        String input;
        boolean isLoaded = false;
        System.out.println("Welcome to the Media Archive Management System");
        do {
            System.out.println("\n1. Load dataset\n" +
                    "2. Run 1000 search test\n" +
                    "3. Search for a media item with the ImdbId.\n" +
                    "4. List the top 10 media according to user votes\n" +
                    "5. List all the media streams in a given country\n" +
                    "6. List the media items that are streaming on all 5 platforms\n");
            input = scanner.nextLine();
            if (input.equals("1")) {
                if (moviesDictionary != null && moviesDictionary.getSize() > 0)
                    moviesDictionary.clear();

                System.out.println("Choose Hash Function:\n1. Simple Summation Function (SSF)\n2. Polynomial Accumulation Function (PAF)");
                int hashChoice = Integer.parseInt(scanner.nextLine());
                HashFunction<String> hashFunction = (hashChoice == 1) ? new SSFHashFunction() : new PAFHashFunction();

                System.out.println("Choose Probing Strategy:\n1. Linear Probing\n2. Double Hashing");
                int probeChoice = Integer.parseInt(scanner.nextLine());
                ProbingStrategy probingStrategy = (probeChoice == 1) ? new LinearProbing() : new DoubleHashing();

                System.out.println("Choose Load Factor:\n1. 0.50\n2. 0.80");
                int loadFactorChoice = Integer.parseInt(scanner.nextLine());
                double loadFactor = (loadFactorChoice == 1) ? 0.50 : 0.80;

                moviesDictionary = new HashedDictionary<>(hashFunction, probingStrategy, loadFactor);
                System.out.println("Dataset is loading");
                loadDataset(moviesDictionary);

                isLoaded = true;
                System.out.println(moviesDictionary.getSize() + " elements have been added to the table.\n" +
                        "Collision Count: " + moviesDictionary.collisionCount + "\n");
            } else if (input.equals("2")) {
                if (isLoaded)
                    searchTest(moviesDictionary);
                else
                    System.out.println("Please load the dataset first");

            } else if (input.equals("3")) {
                if (isLoaded) {
                    System.out.print("Please enter the ImdbID: ");
                    String str = scanner.next();
                    searchItem(moviesDictionary, str);
                } else
                    System.out.println("Please load the dataset first");

            } else if (input.equals("4")) {
                if (isLoaded)
                    menu4(moviesDictionary);
                else
                    System.out.println("Please load the dataset first");
            } else if (input.equals("5")) {
                if (isLoaded) {
                    System.out.print("Please enter the country: ");
                    String str = scanner.next().toUpperCase();
                    menu5(moviesDictionary, str);
                } else
                    System.out.println("Please load the dataset first");
            } else if (input.equals("6")) {
                if (isLoaded)
                    menu6(moviesDictionary);
                else
                    System.out.println("Please load the dataset first");
            }
            System.out.print("Do you want to return the menu? Please enter Y or N: ");
            input = scanner.nextLine();
            System.out.print("----------------------");
        } while (!input.equals("N"));

    }

    public static void loadDataset(HashedDictionary<String, Media> moviesDictionary) {
        ListInterface<String> mediaDetails = new AList<>();
        try (Scanner scanner = new Scanner(new File("movies_dataset.csv"))) {
            scanner.nextLine();
            long startTime = System.nanoTime();
            while (scanner.hasNextLine()) {
                String url = "",
                        title = "",
                        type = "",
                        genres = "",
                        imdbId = "",
                        platform = "",
                        availableCountries = "";
                int releaseYear = 0;
                double imdbAverageRating = 0.0;
                int imdbNumVotes = 0;
                String temp = scanner.nextLine();
                if (temp != null) {
                    String[] row = temp.split(",");
                    for (int j = 0; j < row.length; j++) {
                        if (row[j].contains("\"")) {
                            String countries = "";
                            do {
                                countries += row[j];
                            }
                            while (!row[j++].endsWith("\""));
                            j--;
                            countries = countries.replace("\"", "");
                            mediaDetails.add(countries);
                        } else {
                            mediaDetails.add(row[j]);
                        }
                    }

                    url = mediaDetails.getEntry(1);
                    title = mediaDetails.getEntry(2);
                    type = mediaDetails.getEntry(3);
                    genres = mediaDetails.getEntry(4);
                    if (!mediaDetails.getEntry(5).equals("")) {
                        releaseYear = Integer.parseInt(mediaDetails.getEntry(5));
                    }
                    imdbId = mediaDetails.getEntry(6);
                    if (!mediaDetails.getEntry(7).equals("")) {
                        imdbAverageRating = Double.parseDouble(mediaDetails.getEntry(7));
                    }
                    if (!mediaDetails.getEntry(8).equals("")) {
                        imdbNumVotes = Integer.parseInt(mediaDetails.getEntry(8));
                    }
                    platform = mediaDetails.getEntry(9);
                    availableCountries = mediaDetails.getEntry(10);

                    Media media = new Media(url, title, type, genres, releaseYear, imdbId, imdbAverageRating, imdbNumVotes, platform, availableCountries);
                    moviesDictionary.add(imdbId, media);
                    mediaDetails.clear();
                }
            }
            long endTime = System.nanoTime();
            System.out.println("Time Elapsed for indexing: " + (endTime - startTime) / 1000000.0 + " ms");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void searchTest(HashedDictionary<String, Media> moviesDictionary) {
        try (Scanner scanner = new Scanner(new File("search.txt"))) {
            long startTime = System.nanoTime();
            while (scanner.hasNextLine()) {
                String imdbID = scanner.nextLine();
                if (imdbID != null && moviesDictionary.contains(imdbID)) {
                    Media foundMedia = moviesDictionary.getValue(imdbID);
                    //foundMedia.display();
                }
            }
            long endTime = System.nanoTime();
            System.out.println("Time elapsed for searching: " + (endTime - startTime) / 1000 + " ns");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void searchItem(HashedDictionary<String, Media> moviesDictionary, String imdbID) {
        Media media = moviesDictionary.getValue(imdbID);
        if (media != null)
            media.display();
        else
            System.out.println("\nMedia not found for id: " + imdbID + "\n");
    }

    public static void menu4(HashedDictionary<String, Media> moviesDictionary) {
        DictionaryInterface<Integer, Media> topMedias = new SortedArrayDictionary<>();
//        System.out.println(moviesDictionary.getSize());
        Iterator<Media> valueIterator = moviesDictionary.getValueIterator();
        while (valueIterator.hasNext()) {
            Media tempMedia = valueIterator.next();
            if (tempMedia.getImdbNumVotes() != 0)
                topMedias.add(tempMedia.getImdbNumVotes(), tempMedia);
        }
        Iterator<Media> mediaIterator = topMedias.getValueIterator();
        for (int i = 0; i < 10; i++) {
            Media temp = mediaIterator.next();
            System.out.println((i+1)+"."+temp.getTitle() + " : " + temp.getImdbNumVotes());
        }
        //System.out.println(topMedias.getSize());
    }

    public static void menu5(HashedDictionary<String, Media> moviesDictionary, String country) {
        int count = 0;
        Iterator<String> keyIterator = moviesDictionary.getKeyIterator();
        Iterator<Media> valueIterator = moviesDictionary.getValueIterator();
        System.out.println();
        System.out.println("All the media streams in  "+country);
        while (keyIterator.hasNext() && valueIterator.hasNext()) {
            String tempKey = keyIterator.next();
            Media tempMedia = valueIterator.next();
            ListInterface<Media.MediaPlatform> platformList = tempMedia.getMediaPlatformList();
            for (int i = 1; i <= platformList.getLength(); i++) {
                ListInterface<String> countriesList = tempMedia.getMediaPlatformList().getEntry(i).availableCountriesList;
                for (int j = 1; j <= countriesList.getLength(); j++) {
                    if (countriesList.getEntry(j).equals(country)) {
                        count++;
                        tempMedia.displayForCountrySearch(count);
                        break;
                    }
                }
            }
        }

    }

    public static void menu6(HashedDictionary<String, Media> moviesDictionary) {
        int count = 0;
        Iterator<Media> valueIterator = moviesDictionary.getValueIterator();
        System.out.println();
        System.out.println("MEDIA ITEMS  STREAMING ON ALL 5 PLATFORMS");
        while (valueIterator.hasNext()) {
            Media tempMedia = valueIterator.next();
            if (tempMedia.getMediaPlatformList().getLength() == 5) {
                count++;
                tempMedia.displayForAllFivePlatformSearch(count);
            }
        }
        System.out.println();
    }
}

