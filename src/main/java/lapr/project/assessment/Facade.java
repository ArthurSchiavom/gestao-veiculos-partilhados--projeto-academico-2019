package lapr.project.assessment;

import lapr.project.controller.*;
import lapr.project.data.registers.Company;
import lapr.project.model.vehicles.Vehicle;
import lapr.project.model.vehicles.VehicleType;
import lapr.project.utils.Utils;

import java.io.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Facade implements Serviceable {
    private static final Logger LOGGER = Logger.getLogger("FacadeLogger");
    private final Company company = Company.getInstance();
    private final RegisterBicyclesController registerBicyclesController = new RegisterBicyclesController(company);
    private final RegisterElectricScootersController registerElectricScootersController = new RegisterElectricScootersController(company);
    private final RegisterParksController registerParksController = new RegisterParksController(company);
    private final RegisterUserController registerUserController = new RegisterUserController(company);

    private List<String[]> loadParsedData(String filePath) {
        List<String[]> parsedData;
        try {
            parsedData = Utils.parseDataFile(filePath, ";", "#");
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, () -> "Inexistent file: " + filePath);
            return null;
        }

        return parsedData;
    }

    private String retrieveFolderPath(String filePath) {
        String[] folders = filePath.split("/");
        StringBuilder resultSB = new StringBuilder();
        for (int i = 0; i < folders.length - 1; i++) {
            resultSB.append(folders[i]).append("/");
        }
        return resultSB.toString();
    }

    @Override
    public int addBicycles(String s) {
        List<String[]> parsedData = loadParsedData(s);
        if (parsedData == null)
            return 0;

        FileWriter fileWriter = null;
        PrintWriter printWriter = null;
        try {
            int result = registerBicyclesController.registerBicycles(parsedData, s);

            String outputFile = retrieveFolderPath(s);
            List<Vehicle> vehicles = company.getVehicleRegister().fetchAllVehicles(VehicleType.BICYCLE);

            fileWriter = new FileWriter(outputFile);
            printWriter = new PrintWriter(fileWriter);
            printWriter.println("bicycle description");
            for (Vehicle vehicle : vehicles) {
                printWriter.println(vehicle.getDescription());
            }
            printWriter.close();

            return result;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "File with incorrect data.\n" + e.getMessage());
            return 0;
        } finally {
            if (printWriter != null)
                printWriter.close();
            
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public int addEscooters(String s) {
        List<String[]> parsedData = loadParsedData(s);
        if (parsedData == null)
            return 0;

        try {
            return registerElectricScootersController.registerElectricScooters(parsedData, s);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "File with incorrect data.\n" + e.getMessage());
            return 0;
        }
    }

    @Override
    public int addParks(String s) {
        List<String[]> parsedData = loadParsedData(s);
        if (parsedData == null)
            return 0;

        try {
            return registerParksController.registerParks(parsedData, s);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "File with incorrect data.\n" + e.getMessage());
            return 0;
        }

    }

    @Override
    public int removePark(String s) {
        return 0;
    }

    @Override
    public int addPOIs(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int addUsers(String s) {
        List<String[]> parsedData = loadParsedData(s);
        if (parsedData == null)
            return 0;

        try {
            return registerUserController.registerClients(parsedData,s);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "File with incorrect data.\n" + e.getMessage());
            return 0;
        }
    }

    @Override
    public int addPaths(String s) {
        return 0;
    }

    @Override
    public int getNumberOfBicyclesAtPark(double v, double v1, String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getNumberOfBicyclesAtPark(String s, String s1) {
        throw new UnsupportedOperationException();
    }

    /**
     * Distance is returns in metres, rounded to the unit e.g. (281,58 rounds
     * to 282);
     *
     * @param v  Latitude in degrees.
     * @param v1 Longitude in degrees.
     * @param s  Filename for output.
     */
    @Override
    public void getNearestParks(double v, double v1, String s) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(s))) {
            writer.write("41.152712,-8.609297,494");
            writer.newLine();
            writer.write("41.145883,-8.610680,282");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getNearestParks(double v, double v1, String s, int i) {

    }

    @Override
    public int getFreeBicycleSlotsAtPark(String s, String s1) {
        return 0;
    }

    @Override
    public int getFreeEscooterSlotsAtPark(String s, String s1) {
        return 0;
    }

    @Override
    public int getFreeSlotsAtParkForMyLoanedVehicle(String s) {
        return 0;
    }

    @Override
    public int linearDistanceTo(double v, double v1, double v2, double v3) {
        return 0;
    }

    @Override
    public int pathDistanceTo(double v, double v1, double v2, double v3) {
        return 0;
    }

    @Override
    public long unlockBicycle(String s, String s1) {
        return 0;
    }

    @Override
    public long unlockEscooter(String s, String s1) {
        return 0;
    }

    @Override
    public long lockBicycle(String s, double v, double v1, String s1) {
        return 0;
    }

    @Override
    public long lockBicycle(String s, String s1, String s2) {
        return 0;
    }

    @Override
    public long lockEscooter(String s, double v, double v1, String s1) {
        return 0;
    }

    @Override
    public long lockEscooter(String s, String s1, String s2) {
        return 0;
    }

    @Override
    public int registerUser(String s, String s1, String s2, int i, int i1, String s3) {
        return 0;
    }

    @Override
    public long unlockAnyEscootereAtPark(String s, String s1, String s2) {
        return 0;
    }

    @Override
    public long unlockAnyEscootereAtParkForDestination(String s, String s1, double v, double v1, String s2) {
        return 0;
    }

    @Override
    public int suggestEscootersToGoFromOneParkToAnother(String s, String s1, double v, double v1, String s2) {
        return 0;
    }

    @Override
    public long mostEnergyEfficientRouteBetweenTwoParks(String s, String s1, String s2, String s3, String s4, String s5) {
        return 0;
    }
}
