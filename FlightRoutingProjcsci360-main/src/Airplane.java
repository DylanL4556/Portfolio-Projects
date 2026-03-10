import java.text.DecimalFormat;
public class Airplane {
    public String make;
    public String model;
    public String fuel;
    public String trueFuelType;
    public double fuelCapacity;
    public double fuelConsumption;
    public double speed;
    public double range;

    public Airplane(String make, String model, String fuel, double fuelCapacity, double fuelConsumption, double speed) {
        this.make = make;
        this.model = model;
        this.fuel = fuel;
        this.fuelCapacity = fuelCapacity;
        this.fuelConsumption = fuelConsumption;
        this.speed = speed;
        setRange();
    }

    public void setRange() {
        this.range = fuelCapacity / fuelConsumption * (speed*1.852); //converts speed from knots to km/hr
    }
    public void setTrueFuelType(){
        if (fuel.toLowerCase().trim().equals("prop")){
            trueFuelType = "AVGAS";
        }
        if (fuel.toLowerCase().trim().equals("jet")){
            trueFuelType = "JA-a";
        }
        if (fuel.toLowerCase().trim().equals("turboprop")){
            trueFuelType = "JA-a";
        }
        if (fuel.toLowerCase().trim().equals("turbofan")){
            trueFuelType = "JA-a";
        }
    }
    public String forPrint(){
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        return "Make: " + make + ", Model: " + model + ", Fuel Type: " + fuel + ", Capacity(L): " + decimalFormat.format(fuelCapacity) + ", Consumption(L/HR): " + decimalFormat.format(fuelConsumption) + ", Speed: " + decimalFormat.format(speed) + ", Range: " + decimalFormat.format(range);
    }

    @Override
    public String toString() {
        return make + " " + model;
    }

        public void copyFrom(Airplane other) {
            this.make = other.make;
            this.model = other.model;
            this.fuel = other.fuel;
            this.fuelCapacity = other.fuelCapacity;
            this.speed = other.speed;
            this.fuelConsumption = other.fuelConsumption;
            this.range = other.range;
            this.setTrueFuelType();
        }
}
