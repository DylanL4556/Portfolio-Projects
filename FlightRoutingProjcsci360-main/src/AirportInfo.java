public class AirportInfo {
    public Airport thisAirport;
    public Airport nextAirport;
    public double Heading;
    public double distance;
    public double fuelCost;
    public double timeCost;

    public AirportInfo(Airport thisAirport, Airport nextAirport, double Heading, double distance, double fuelCost, double timeCost){
        this.thisAirport = thisAirport;
        this.nextAirport = nextAirport;
        this.Heading = Heading;
        this.distance = distance;
        this.fuelCost = fuelCost;
        this.timeCost = timeCost;
    }
    public AirportInfo(Airport thisAirport){
        this.thisAirport = thisAirport;
    }
}
