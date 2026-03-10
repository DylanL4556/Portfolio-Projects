import java.io.FileNotFoundException;
import java.io.File;
import java.io.IOException;
import java.io.*;
import java.util.*;
import java.lang.*;
import java.io.FileWriter;
import java.util.HashSet;
import java.text.DecimalFormat;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.*;
public class DataBaseManager {
    public static File airportDB;
    public static File airplaneDB;
    public static ArrayList<Airport> aprts;
    public static ArrayList<Airplane> aplanes;

    //init sets runtime arraylists, which in this final implementation are actually completely useless for their original purpose and extremely poorly optimized.. lol
    public DataBaseManager(String airportDBs, String airplaneDBs) {
        airportDB = new File(airportDBs);
        airplaneDB = new File(airplaneDBs);
        aprts = readAirports();
        aplanes = readAirplanes();
    }

    //reads the airports from the file, stores in arraylist for runtime use
    public static ArrayList<Airport> readAirports() {
        ArrayList<Airport> aprts = new ArrayList<>();
        try {
            boolean hasboth = true;
            String nxt = "";
            Scanner sc = new Scanner(airportDB);
            while (sc.hasNextLine()) {

                String ICAO;
                if (!hasboth) {
                    if (nxt == "") {
                        System.out.println("bug line 29");
                    }
                    ICAO = nxt;
                } else {
                    ICAO = sc.nextLine();
                }

                String Loc = sc.nextLine();

                double Long = Double.parseDouble(sc.nextLine());

                double Lat = Double.parseDouble(sc.nextLine());

                double freq = Double.parseDouble(sc.nextLine());

                String[] fts = new String[2];
                fts[0] = sc.nextLine();
                if (sc.hasNextLine()) {
                    nxt = sc.nextLine();
                } else {
                    fts[1] = null;
                    Airport a = new Airport(ICAO, Loc, fts, Long, Lat, freq);
                    aprts.add(a);
                    continue;
                }

                if (Objects.equals(nxt.trim(), "JA-a") || Objects.equals(nxt.trim(), "AVGAS")) {
                    fts[1] = nxt;
                    hasboth = true;
                } else {
                    hasboth = false;
                }
                Airport a = new Airport(ICAO, Loc, fts, Long, Lat, freq);
                aprts.add(a);
            }
            sc.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return aprts;
    }

    //reads airplanes from the file, stores as arraylist for runtime use
    public static ArrayList<Airplane> readAirplanes() {
        ArrayList<Airplane> aplanes = new ArrayList<>();
        try {
            Scanner sc = new Scanner(airplaneDB);
            while (sc.hasNextLine()) {
                String[] info = sc.nextLine().split(",");
                String make = info[0].trim();
                String model = info[1].trim();
                String fuel = info[2].trim();
                double fuelCapacity = Double.parseDouble(info[3].trim());
                double fuelConsumption = Double.parseDouble(info[4].trim());
                double speed = Double.parseDouble(info[5].trim());
                Airplane a = new Airplane(make, model, fuel, fuelCapacity, fuelConsumption, speed);
                a.setRange();
                aplanes.add(a);
            }
            sc.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return aplanes;
    }

    public static void addAirport(Airport a) { //epic logic to ensure no trailing spaces
        //data validation on the add buttons, not here
        //update arrlist
        aprts = readAirports();
        if (!containsAirport(aprts, a)) { //if we update synchronously, only this check is needed to prevent duplicates within the file as well
            aprts.add(a);
            try {
                FileWriter writer = new FileWriter(airportDB, true);
                writer.write("\n" + a.CAOid.trim() + "\n");
                writer.write(a.APTname.trim() + "\n");
                writer.write(a.APRTlongitude + "\n");
                writer.write(a.APRTlatitude + "\n");
                writer.write(a.freq + "\n");
                for (String ft : a.APRTfuelTypes) {
                    if (ft != null) {
                        if (ft != a.APRTfuelTypes[a.APRTfuelTypes.length - 1]) { // wait a second lol
                            writer.write(ft.trim() + "\n");
                        } else {
                            writer.write(ft.trim());
                        }
                    }
                }
                writer.close();
            } catch (IOException e) {
                System.err.println("IOException within addAirport(): " + e.getMessage());
            }
        } else {
            System.out.println("why would i let you do that");
        }
        //update file
    }

    public static void addAirplane(Airplane a) {
        //do data validation when creating airplane?
        //update arraylist
        //update file
        aplanes = readAirplanes();
        if (!containsAirplane(aplanes, a)) {
            aplanes.add(a);
            try {
                FileWriter writer = new FileWriter(airplaneDB, true);
                writer.write("\n" + a.make.trim() + "," + a.model.trim() + "," + a.fuel.trim() + "," + a.fuelCapacity + "," + a.fuelConsumption + "," + a.speed);
                writer.close();
            } catch (IOException e) {
                System.err.println("IOException within addAirplane(): " + e.getMessage());
            }
        } else {
            System.out.println("why would i let you do that");
        }
    }

    public static boolean containsAirport(ArrayList<Airport> aprtz, Airport a) {
        for (Airport aprt : aprtz) {
            if (aprt.CAOid.equals(a.CAOid)) {
                //System.out.println("ICAO matched");
                if (aprt.APTname.equals(a.APTname)) {
                    //System.out.println("Name matched");
                    if (aprt.APRTlatitude == a.APRTlatitude) {
                        //System.out.println("Lat matched");
                        if (aprt.APRTlongitude == a.APRTlongitude) {
                            //System.out.println("Long matched");
                            if (aprt.freq == a.freq) {
                                //System.out.println("Freq matched");
                                if (aprt.APRTfuelTypes[0].equals(a.APRTfuelTypes[0])) {
                                    //System.out.println("Fuel matched");
                                    //System.out.print
                                    // ++ln(aprt.APRTfuelTypes.length);
                                    //System.out.println(a.APRTfuelTypes.length);
                                    //aprtz from file reader always has 2 fuel types because of the reader logic.
                                    if (a.APRTfuelTypes.length == 1 || a.APRTfuelTypes[1] == null) {
                                        return true;
                                    } else if (a.APRTfuelTypes.length == 2) {
                                        if (aprt.APRTfuelTypes[1] == a.APRTfuelTypes[1]) {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean containsAirplane(ArrayList<Airplane> aplanes, Airplane a) {
        for (Airplane ap : aplanes) {
            if (ap.make.equals(a.make)) {
                if (ap.model.equals(a.model)) {
                    if (ap.fuel.equals(a.fuel)) {
                        if (ap.fuelCapacity == a.fuelCapacity) {
                            if (ap.fuelConsumption == a.fuelConsumption) {
                                if (ap.speed == a.speed) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public static void removeAirplane(Airplane a) {
        if (containsAirplane(aplanes, a)) {
            aplanes.remove(a);
            File orig = airplaneDB;
            File temp = new File("src/dbDir/temp.txt");
            try {
                BufferedReader reader = new BufferedReader(new FileReader(orig));
                BufferedWriter writer = new BufferedWriter(new FileWriter(temp));
                String lineToRemove = a.make.trim() + "," + a.model.trim() + "," + a.fuel.trim() + "," + a.fuelCapacity + "," + a.fuelConsumption + "," + a.speed;
                String currentLine;
                while ((currentLine = reader.readLine()) != null) {
                    reader.mark(1000);
                    if (reader.readLine() == null) {
                        writer.write(currentLine);
                        continue;
                    }
                    reader.reset();
                    reader.mark(1000);
                    //this case is to prevent error-prone formatting when the last line in the file is the target to remove
                    if (reader.readLine().equals(lineToRemove) && reader.readLine() == null) {
                        writer.write(currentLine);
                        continue;
                    }
                    reader.reset();

                    if (currentLine.equals(lineToRemove)) continue;
                    writer.write(currentLine + "\n");
                }
                writer.close();
                reader.close();

                if (!orig.delete()) {
                    System.out.println("File delete failed, attempting alternative.");
                    Files.move(temp.toPath(), orig.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                if (!temp.renameTo(orig)) {
                    System.out.println("File rename failed");
                }
            } catch (IOException e) {
                System.err.println("IOException within removeAirplane(): " + e.getMessage());
            }
        } else {
            System.out.println("You cant remove what is not there (subtract 10 iq points!)");
        }
    }

    public static void removeAirport(Airport a) {
        //update arraylist
        //update file
        if (containsAirport(aprts, a)) {
            aprts.remove(a);
            File orig = airportDB;
            File temp = new File("src/dbDir/tempp.txt");
            try {
                BufferedReader reader = new BufferedReader(new FileReader(orig));
                BufferedWriter writer = new BufferedWriter(new FileWriter(temp));
                String currentLine;
                while ((currentLine = reader.readLine()) != null) {
                    reader.mark(1000);
                    if (currentLine.trim().equals(a.CAOid.trim())) {
                        if (reader.readLine().trim().equals(a.APTname.trim())) {
                            if (Double.parseDouble(reader.readLine().trim()) == a.APRTlongitude) {
                                if (Double.parseDouble(reader.readLine().trim()) == a.APRTlatitude) {
                                    if (Double.parseDouble(reader.readLine().trim()) == a.freq) {
                                        if (reader.readLine().trim().equals(a.APRTfuelTypes[0].trim())) {

                                            reader.mark(1000);
                                            String next = reader.readLine();
                                            if (a.APRTfuelTypes.length == 1 || a.APRTfuelTypes[1] == "" || a.APRTfuelTypes[1] == null) {
                                                reader.reset();
                                                continue;
                                            } else if (next != null && next.equals(a.APRTfuelTypes[1].trim())) {
                                                continue;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    reader.reset();
                    reader.mark(1000);
                    if (reader.readLine() == null) {
                        writer.write(currentLine);
                        continue;
                    }
                    reader.reset();
                    reader.mark(1000);
                    if (Objects.equals(reader.readLine().trim(), a.CAOid.trim())) {
                        if (Objects.equals(reader.readLine().trim(), a.APTname.trim())) {
                            if (Double.parseDouble(reader.readLine().trim()) == a.APRTlongitude) {
                                if (Double.parseDouble(reader.readLine().trim()) == a.APRTlatitude) {
                                    if (Double.parseDouble(reader.readLine().trim()) == a.freq) {
                                        if (reader.readLine().trim().equals(a.APRTfuelTypes[0].trim())) {
                                            //reader.mark(1000);
                                            String next = reader.readLine();
                                            if (a.APRTfuelTypes.length == 1 || a.APRTfuelTypes[1] == null) {
                                                //reader.reset();
                                                if (next == null) {
                                                    writer.write(currentLine);
                                                    continue;
                                                }
                                            } else if (next != null && next.trim().equals(a.APRTfuelTypes[1].trim())) {
                                                if (reader.readLine() == null) {
                                                    writer.write(currentLine);
                                                    continue;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    reader.reset();
                    writer.write(currentLine + "\n");
                }
                writer.close();
                reader.close();
                if (!orig.delete()) {
                    System.out.println("File delete failed, attempting alternative.");
                    Files.move(temp.toPath(), orig.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                if (!temp.renameTo(orig)) {
                    System.out.println("File rename failed");
                }
            } catch (IOException e) {
                System.err.println("IOexception within removeAirport(): " + e.getMessage());
            }
        } else {
            System.out.println("You cant remove what is not there (subtract 10 iq points!)");
        }
    }

    public static void modifyAirplane(Airplane old, Airplane n3w) {
        //update arraylist
        //update file
        if (old == n3w) {
            return;
        }
        if (containsAirplane(aplanes, n3w)) {
            System.out.println("You cant modify an airplane to be the same as another airplane");
            return;
        }
        if (containsAirplane(aplanes, old)) {
            aplanes.remove(old);
            aplanes.add(n3w);
            removeAirplane(old);
            addAirplane(n3w);
        }
    }

    public static void modifyAirport(Airport old, Airport nw) {
        //update arraylist
        //update file
        if (old == nw) {
            return;
        }
        if (containsAirport(aprts, nw)) {
            System.out.println("You cant modify an airport to be the same as another airport");
            return;
        }
        if (containsAirport(aprts, old)) {

            removeAirport(old);
            addAirport(nw);
        }
    }

    public static ArrayList<Airport> searchAirports(String srch) {
        ArrayList<Airport> results = new ArrayList<>();
        for (Airport a : aprts) {
            String combined;
            if (a.APRTfuelTypes[1] != null) {
                combined = a.APRTfuelTypes[0].toLowerCase().trim() + a.APRTfuelTypes[1].toLowerCase().trim();
            } else {
                combined = a.APRTfuelTypes[0];
            }
            if (a.CAOid.toLowerCase().contains(srch.toLowerCase().trim()) || a.APTname.toLowerCase().contains(srch.toLowerCase().trim()) || combined.contains(srch.toLowerCase().trim())) {
                results.add(a);
            }
        }
        return results;
    }

    public static Airport searchICAO(String srch) {
        for (Airport a : aprts) {
            if (a.CAOid.toLowerCase().contains(srch.toLowerCase().trim())) {
                return a;
            }
        }
        return null;
    }

    public static ArrayList<Airplane> searchAirplanes(String srch) {
        ArrayList<Airplane> results = new ArrayList<>();
        if (srch.contains(",")) {
            ArrayList<String> specific = new ArrayList<>(Arrays.asList(srch.split(",")));
            for (Airplane a : aplanes) {
                if (a.make.toLowerCase().contains(specific.get(0).toLowerCase().trim()) && a.model.toLowerCase().contains(specific.get(1).toLowerCase().trim())) {
                    results.add(a);
                }
            }
        } else {
            for (Airplane a : aplanes) {
                if (a.make.toLowerCase().contains(srch.toLowerCase().trim()) || a.model.toLowerCase().contains(srch.toLowerCase().trim()) || a.fuel.toLowerCase().contains(srch.toLowerCase().trim())) {
                    results.add(a);
                }
            }
        }

        return results;
    }


    public static ArrayList<Airport> searchAirports(String srch, Airplane air) {
        air.setTrueFuelType();
        ArrayList<Airport> results = new ArrayList<>();
        for (Airport a : aprts) {
            String combined;

            String searchFuel;

            if (a.APRTfuelTypes[1] != null) {
                combined = a.APRTfuelTypes[0].toLowerCase().trim() + a.APRTfuelTypes[1].toLowerCase().trim();
            } else {
                combined = a.APRTfuelTypes[0];
            }

            if (Objects.equals(a.APRTfuelTypes[0], air.trueFuelType) || Objects.equals(a.APRTfuelTypes[1], air.trueFuelType)) {
                if (a.CAOid.toLowerCase().contains(srch.toLowerCase().trim()) || a.APTname.toLowerCase().contains(srch.toLowerCase().trim()) || combined.contains(srch.toLowerCase().trim())) {
                    results.add(a);
                }
            }
        }
        return results;
    }
}