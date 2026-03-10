public class Airport {
    String CAOid = "";
    String APTname = "";
    String[] APRTfuelTypes;
    double APRTlongitude;
    double APRTlatitude;
    double freq;

    public Airport(String CAO, String Name, String[] fuelTypes, double Longitude, double Latitude, double fr) {
        CAOid = CAO;
        APTname = Name;
        APRTfuelTypes = fuelTypes;
        APRTlongitude = Longitude;
        APRTlatitude = Latitude;
        freq = fr;
    }
    public String forPrint() {
        if (APRTfuelTypes.length > 1){
            return "ICAO: " + CAOid + " Name: " + APTname + " Fuel Types: " + APRTfuelTypes[0] + ", " + APRTfuelTypes[1] + " Longitude: " + APRTlongitude + " Latitude: " + APRTlatitude + " Frequency: " + freq;
        }
        return "ICAO: " + CAOid + " Name: " + APTname + " Fuel Types: " + APRTfuelTypes[0] + " Longitude: " + APRTlongitude + " Latitude: " + APRTlatitude + " Frequency: " + freq;
    }

    @Override
    public String toString() {
        return CAOid + " " + APTname;
    }




}
