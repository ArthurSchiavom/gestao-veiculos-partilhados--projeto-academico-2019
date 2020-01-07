package lapr.project.utils;

import lapr.project.data.AutoCloseableManager;
import lapr.project.data.SortByHeightDescending;
import lapr.project.model.point.of.interest.PointOfInterest;
import lapr.project.model.users.Client;
import lapr.project.model.vehicles.ElectricScooter;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

public class Utils {
    private static final Logger LOGGER = Logger.getLogger("UtilsLog");


    private Utils() {
    }

    public static List<String[]> parseDataFileAndValidateHeader(String filePath, String valueSeparator, String lineCommentTag, String header) throws FileNotFoundException, InvalidFileDataException {
        List<String[]> result = new ArrayList<>();
        try (Scanner scanner = new Scanner(new FileReader(filePath))) {
            boolean firstLine = true;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.startsWith(lineCommentTag)) {
                    if (firstLine) {
                        if (!line.equalsIgnoreCase(header))
                            throw new InvalidFileDataException("Header is different from expected.");
                        firstLine = false;
                    }

                    String[] lineValues = trimArrayElements(line.split(valueSeparator, -1));
                    result.add(lineValues);
                }
            }
        } catch (FileNotFoundException e) {
            throw e;
        }

        return result;
    }

    public static List<String[]> parseDataFile(String filePath, String valueSeparator, String lineCommentTag) throws FileNotFoundException {
        List<String[]> result = new ArrayList<>();
        try (Scanner scanner = new Scanner(new FileReader(filePath))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (!line.startsWith(lineCommentTag)) {
                    String[] lineValues = trimArrayElements(line.split(valueSeparator, -1));
                    result.add(lineValues);
                }
            }
        } catch (FileNotFoundException e) {
            throw e;
        }

        return result;
    }

    public static <T> boolean areArraysEqual(T[] a1, T[] a2) {
        if (a1.length != a2.length)
            return false;

        for (int i = 0; i < a1.length; i++) {
            if (!a1[i].equals(a2[i]))
                return false;
        }
        return true;
    }

    public static String[] trimArrayElements(String... elements) {
        String[] result = new String[elements.length];
        for (int i = 0; i < elements.length; i++) {
            result[i] = elements[i].trim();
        }
        return result;
    }

    public static void writeToFile(List<String> lines, String fileName) throws IOException {
        AutoCloseableManager autoCloseableManager = new AutoCloseableManager();
        try {
            FileWriter fileWriter = new FileWriter(fileName);
            autoCloseableManager.addAutoCloseable(fileWriter);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            autoCloseableManager.addAutoCloseable(printWriter);
            for (String line : lines) {
                printWriter.println(line);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            autoCloseableManager.closeAutoCloseables();
        }
    }

    /**
     * Orders ascending by numbers of poi's, and then descending by height
     * @param paths list that contains list of paths
     */
    public static void sort(List<LinkedList<PointOfInterest>> paths){
        Collections.sort(paths, new Comparator<LinkedList<PointOfInterest>>() {
            @Override
            public int compare(LinkedList<PointOfInterest> pointOfInterests, LinkedList<PointOfInterest> t1) {
                return pointOfInterests.size()-t1.size();
            }
        });
        for(LinkedList<PointOfInterest> p : paths){
            sortPois(p);
        }
    }

    /**
     * Orders the poi's according to the output/paths.csv file
     * @param path path containing the poi's
     */
    public static void sortPois(LinkedList<PointOfInterest> path){
        Collections.sort(path, new SortByHeightDescending());
    }

    /**
     * Puts the information of the parks and poi's in a list so it can be printed
     * @param paths all the possible shortest paths
     * @param distance overall distance
     * @param elevation overall elevation
     * @return list of strings ready to be outputed with the information given in the output/path.csv
     */
    public static List<String> getOutputPaths(List<LinkedList<PointOfInterest>> paths, long distance, long energy, int elevation){
        List<String> output = new LinkedList<>();
        int pathNumber = 1;
        for(LinkedList<PointOfInterest> path : paths){
            getOutputPath(path,output,distance, energy,elevation,pathNumber);
        }
        return output;
    }

    /**
     * Orders by individual path
     * @param path path to output
     * @param output list of strings containing the output
     * @param distance overall distance
     * @param energy overall energy
     * @param elevation overall elevation
     * @param pathNumber path number
     */
    public static void getOutputPath(LinkedList<PointOfInterest> path, List<String> output, long distance, long energy, int elevation,int pathNumber){
        String line= String.format("Path %03d", pathNumber)+"\n";
        line += "total distance: "+distance+"\ntotal energy: "+ energy +"\nelevation: "+ elevation+"\n";
        for(PointOfInterest poi : path) {
            line+=poi.getCoordinates().getLatitude()+";"+poi.getCoordinates().getLongitude()+"\n";
        }
        output.add(line);
    }

    /**
     * Calculates distance of a trip
     * @param path path taken
     * @return overall distance in meters
     */
    public static long calculateDistanceInMeters(LinkedList<PointOfInterest> path) {
        PointOfInterest previous = null;
        double distance = 0;
        for(PointOfInterest poi : path){
            if(previous!= null){
                distance+= previous.getCoordinates().distance(poi.getCoordinates());
            }
            previous = poi;
        }
        return Math.round(distance*1000); //km to meters
    }

    /**
     * Calculates the energy spent on the trip
     * @param path
     * @param dummyVehicle
     * @param dummyClient
     * @return
     */
    public static long calculateEnergy(LinkedList<PointOfInterest> path, ElectricScooter dummyVehicle, Client dummyClient) {
        //TODO: d
        return 3;
    }
}
