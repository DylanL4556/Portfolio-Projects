import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.util.PriorityQueue;

public class flightPlan {
    //back-end
    public ArrayList<Airport> inputApts;
    public ArrayList<AirportInfo> optimalPath;
    public Airport start;
    public Airport end;
    public DataBaseManager DB = new DataBaseManager("./src/dbDir/airports.txt", "./src/dbDir/airplanes.txt");

    //for display
    public JPanel graphDisplay = new JPanel();
    public JPanel pathDisplay = new JPanel();

    //more back end
    public double totalDistance;
    public double totalFuelCost;
    public double totalTimeCost;
    public int totalStops;

    //public ArrayList<point> bestPathpts = new ArrayList<>();
    //priorqueue?

    //custom class for algorithmic use
    public class point {
        public String name;
        public double x;
        public double y;
        public double weight;
        public double distance;
        public boolean isStart;
        public boolean isEnd;
        public boolean hasAVGAS;
        public boolean hasJAa;
        public boolean pathless;
        public ArrayList<point> neighbors = new ArrayList<>();

        public point(double x, double y, String name){
            this.x = x;
            this.y = y;
            this.name = name;
            pathless = false;
        }
        public point(double x, double y, String name, boolean isStart, boolean isEnd, boolean hasAVGAS, boolean hasJAa){
            this.x = x;
            this.y = y;
            this.name = name;
            if (isStart && isEnd){
                throw new IllegalArgumentException("point cannot be both start and end");
            }
            this.isStart = isStart;
            this.isEnd = isEnd;
            this.hasAVGAS = hasAVGAS;
            this.hasJAa = hasJAa;
            pathless = false;
        }
        public point(){
            this.x = 0;
            this.y = 0;
            this.name = "tmp";
            pathless = false;
        }
        public point(Airport a){
            this.x = a.APRTlatitude;
            this.y = a.APRTlongitude;
            this.name = a.CAOid;
            this.hasAVGAS = Arrays.asList(a.APRTfuelTypes).contains("AVGAS");
            this.hasJAa = Arrays.asList(a.APRTfuelTypes).contains("JA-a");
        }
        public ArrayList<point> convert(ArrayList<Airport> apts){
            ArrayList<point> pts = new ArrayList<>();
            for (Airport a : apts){
                point p = new point(a);
                //System.out.println(p.name);
                if (!pts.contains(p)){
                    pts.add(p);
                }
            }
            return pts;
        }
        public point getPoint(String name, ArrayList<point> pts){
            for (point p : pts){
                if (p.name.equals(name)){
                    return p;
                }
            }
            return new point();
        }
        public double[] deriveWeight(point start, point end, point p) {

            point intercept = new point();
            if (end.y == start.y){
                end.y += 0.0000001;
            }
            if (end.x == start.x){
                end.x += 0.0000001;
            }
            double slopeOrig = ((end.y - start.y) / (end.x - start.x));

            //System.out.println("Slope: " + slopeOrig);
            double slopePerp = -1 / slopeOrig;
            //System.out.println("Perp Slope: " + slopePerp);
            intercept.name = "intercept";
            intercept.x = (-1*slopeOrig*(end.x)+end.y+slopePerp*(p.x)-p.y)/(slopePerp-slopeOrig);
            //System.out.println("Intercept x: " + intercept.x);
            intercept.y = slopePerp*(intercept.x-p.x)+p.y;
            //System.out.println("Intercept y: " + intercept.y);
            double dToIntercept = Math.sqrt(Math.pow(intercept.x - p.x, 2) + Math.pow(intercept.y - p.y, 2));

            //System.out.println("Distance to intercept: " + dToIntercept);
            double dToEnd = Math.sqrt(((end.x-p.x)*(end.x-p.x)) + ((end.y-p.y)*(end.y-p.y)));
            return new double[]{dToIntercept, dToEnd};
        }
        public void setWeight(point start, point end, point p) {
            double[] weights = deriveWeight(start,end, p);
            //System.out.println("Weights: " + weights[0] + " " + weights[1]);
            p.weight = weights[1]+weights[0];//weights[1]-weights[0];//Math.sqrt(Math.pow(weights[0],2)+Math.pow(weights[1],2)); //weights[1]-weights[0];//
        }

        public point getMin(){
            point p = new point(0,0,"tmp");
            p.weight =  Integer.MAX_VALUE;
            if (neighbors.size() == 0){
                return this;
            }
            for (point pt : neighbors){
                if (pt.weight < p.weight){
                    if (!pt.pathless){
                        p = pt;
                    }
                }
            }

            return p;
        }
        public boolean isInRange(double r, point Base, point Jump){
            return getDistance(Base,Jump) <= r;
        }
        public void associateNbs(point[] pts, double range){
            for (point p: pts){
                if (this != p){
                    if (isInRange(range, this, p)){
                        if (!neighbors.contains(p)) {
                            neighbors.add(p);
                        }
                    }
                }
            }
        }
        public void associateNbs(point[] pts,point base, double range){
            for (point p: pts){
                if (base != p){
                    if (isInRange(range, base, p)){
                        if (!neighbors.contains(p)) {
                            neighbors.add(p);
                        }
                    }
                }
            }
        }
        public void setDistance(point base,point jump){
            distance = getDistance(base,jump);
        }
        public double getDistance(point base,point jump) {
            final int R = 6371; // Radius of the earth in km

            double lat1 = Math.toRadians(base.x);
            double lon1 = Math.toRadians(base.y);
            double lat2 = Math.toRadians(jump.x);
            double lon2 = Math.toRadians(jump.y);

            double dLat = lat2 - lat1;
            double dLon = lon2 - lon1;

            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(lat1) * Math.cos(lat2) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

            return R * c;
        }
        public static double calculateHeading(double lat1, double log1, double lat2, double log2){
            double t1 = Math.toRadians(lat1);
            double t2 = Math.toRadians(lat2);
            double delta = Math.toRadians(log2 - log1);

            double y = Math.sin(delta) * Math.cos(t2);
            double x = Math.cos(t1) * Math.sin(t2) - Math.sin(t1) * Math.cos(t2) * Math.cos(delta);
            double theta = Math.atan2(y, x);
            double brng = Math.toDegrees(theta);

            if (brng < 0) {
                brng += 360;
                brng = brng%360;
            }

            return brng;

        }
        public ArrayList<AirportInfo> getBestPath(ArrayList<Airport> pts, Airport start, Airport end, Airplane ap) throws Exception {
            ap.setRange();
            ap.setTrueFuelType();
            //System.out.println(ap.trueFuelType);
            ArrayList<point> ptz = new ArrayList<>();
            for (Airport a : pts){
                if (Arrays.asList(a.APRTfuelTypes).contains(ap.trueFuelType)) {
                    ptz.add(new point(a));
                    //System.out.println(a.CAOid);
                }
            }
            ArrayList<AirportInfo> path = new ArrayList<>();


            point startpt = new point(start);
            point endpt = new point(end);
            ptz.get(ptz.indexOf(startpt.getPoint(startpt.name,ptz))).isStart = true;
            ptz.get(ptz.indexOf(endpt.getPoint(endpt.name,ptz))).isEnd = true;
            point cpyStr = ptz.get(ptz.indexOf(startpt.getPoint(startpt.name,ptz)));
            point cpyEnd = ptz.get(ptz.indexOf(endpt.getPoint(endpt.name,ptz)));
            point[] ptsArr = ptz.toArray(new point[ptz.size()]);
            System.out.println("diss?: " + cpyStr.getDistance(cpyStr, cpyEnd));
            for (point p : ptz){
                p.setWeight(cpyStr, cpyEnd, p);
            }
            for (point p : ptz){
                p.associateNbs(ptsArr, ap.range);
            }
            boolean finished = false;
            point min = cpyStr;
            point current = cpyStr;
            point prev = cpyStr;
            ArrayList<point> pathpts = new ArrayList<>();
            System.out.println(min.getDistance(startpt,endpt));
            for (point p : cpyStr.neighbors){
                System.out.println(p.name);
            }
            int cycleCT = 0;
            while (!finished) {
                if (cpyStr.neighbors.size() == 0){
                    throw new Exception("No path found");
                }
                if (prev == current.getMin()){
                    cycleCT++;
                }
                if (cycleCT > 5){
                    throw new Exception("No path found");
                }
                //current.associateNbs(ptsArr, current, ap.range);

                //System.out.println("Weight: " + current.getMin().weight);
                //System.out.println("Prev: " + prev.name);
                //System.out.println("Current: " + current.name);
                //System.out.println("Min: " + current.getMin().name);
                if (current.neighbors.contains(cpyEnd)){
                    if (!pathpts.contains(current)){
                        pathpts.add(current);
                    }
                    if (!pathpts.contains(cpyEnd)){
                        pathpts.add(cpyEnd);
                    }
                    System.out.println("Path found");
                    finished = true;
                    break;
                }
                if (ptz.get(ptz.indexOf(current.getPoint(current.name,ptz))).isEnd) {
                    finished = true;
                }
                if (current.name == cpyStr.name && current.neighbors.size() == 0){
                    System.out.println("No path found");
                    throw new Exception("No path found");
                }
                //System.out.println("Range: " + isInRange(100, current, min));
                //System.out.println(min.neighbors.get(0).name);
                if (current.neighbors.size() == 0){
                    //current.pathless = true;
                    System.out.println("Pathless: " + current.name);
                    prev.neighbors.remove(current); //removes neighbors that are pathless
                    if (pathpts.contains(current)){
                        pathpts.remove(current);
                    }
                    current = prev;
                    min = current.getMin(); //returns lowest besides the pathless
                    continue;
                }

                //System.out.println(min.name);
                prev = current;
                min = current.getMin();
                pathpts.add(current);
                current = min;


            }


            for (point p : pathpts){
                path.add(new AirportInfo(DB.searchICAO(p.name)));
            }
            return path;
        }
    }
    public void setDisplay() {
        //this will be the display for the path
        //this panel must be 1080 by 540
        graphDisplay = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.RED);
                setLayout(null);
                setBackground(Color.BLACK);
                g.drawString("The graph will not draw offscreen paths (I.E paths off map), so heading will look incorrect",10,10);
                for (AirportInfo a : optimalPath){
                    g.drawLine((int) (a.thisAirport.APRTlongitude+180)*3, (int) (540-((a.thisAirport.APRTlatitude + 90) * 3)), (int) (optimalPath.get(optimalPath.size()-1).thisAirport.APRTlongitude+180)*3, (int) (540-((optimalPath.get(optimalPath.size()-1).thisAirport.APRTlatitude + 90) * 3)));
                }
            }

        };

        graphDisplay.setSize(1080, 540);

        graphDisplay.setVisible(true);
        for (Airport ai : inputApts){

            JLabel lbl = new JLabel(ai.CAOid);
            lbl.setBounds((int) (ai.APRTlongitude+180)*3, (int) (graphDisplay.getHeight()-((ai.APRTlatitude + 90) * 3))+5, 25, 25);
            lbl.setForeground(Color.WHITE);
            graphDisplay.add(lbl);
            smallbtn btn = new smallbtn(" ");
            btn.setBounds((int) (ai.APRTlongitude+180)*3, (int) (graphDisplay.getHeight()-((ai.APRTlatitude + 90) * 3)), 5, 5);
            btn.addActionListener(e -> {
                JPanel panel = new JPanel();
                panel.setSize(200, 300);
                panel.setLayout(new GridLayout(8, 1));
                panel.add(new JLabel("Airport Info: "));
                panel.add(new JLabel("ICAO: " + ai.CAOid));
                panel.add(new JLabel("Name: " + ai.APTname));
                panel.add(new JLabel("Fuel Types: " + ai.APRTfuelTypes[0] + ", " + ai.APRTfuelTypes[1]));
                panel.add(new JLabel("Longitude: " + ai.APRTlongitude));
                panel.add(new JLabel("Latitude: " + ai.APRTlatitude));
                panel.add(new JLabel("Frequency: " + ai.freq));
                JButton close = new JButton("Close");
                close.addActionListener(e1 -> {
                    graphDisplay.remove(panel);
                    graphDisplay.revalidate();
                    graphDisplay.repaint();
                });
                panel.add(close);
                panel.setVisible(true);
                graphDisplay.add(panel);
                graphDisplay.revalidate();
                graphDisplay.repaint();
            });
            graphDisplay.add(btn);
        }
        pathDisplay = new JPanel();
        pathDisplay.setLayout(new GridLayout(1, optimalPath.size()));
        for (AirportInfo ai : optimalPath){
            JPanel pathCard = new JPanel();
            pathCard.setLayout(new GridLayout(6, 1));
            pathCard.add(new JLabel("Airport: " + ai.thisAirport.CAOid));
            pathCard.add(new JLabel("Fuel Types: " + ai.thisAirport.APRTfuelTypes[0] + ", " + ai.thisAirport.APRTfuelTypes[1]));
            pathCard.add(new JLabel("Longitude: " + ai.thisAirport.APRTlongitude));
            pathCard.add(new JLabel("Latitude: " + ai.thisAirport.APRTlatitude));
            pathCard.add(new JLabel("Frequency: " + ai.thisAirport.freq));
            pathCard.add(new JLabel("Heading: " + ai.Heading));
            pathCard.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            pathDisplay.add(pathCard);
        }
        pathDisplay.setVisible(true);
        pathDisplay.revalidate();
        pathDisplay.repaint();
    }
    public flightPlan(ArrayList<Airport> inputApts, Airport start, Airport end, Airplane ap) throws Exception {
        point p = new point(0,0,"tmp");
        this.inputApts = inputApts;
        this.start = start;
        this.end = end;
        try {
            this.optimalPath = p.getBestPath(inputApts, start, end, ap);
            this.totalDistance = 0;
            this.totalFuelCost = 0;
            this.totalTimeCost = 0;
            this.totalStops = 0;
            for (AirportInfo ai : optimalPath){
                ai.distance = p.getDistance(new point(ai.thisAirport), new point(optimalPath.get(optimalPath.indexOf(ai)+1).thisAirport));
                double time = ai.distance/ap.speed; //this assumes km and km/hr (and L/hr and L)
                ai.timeCost = time;
                ai.fuelCost = time*ap.fuelConsumption; // Liters (assumes L/Hr)
                totalDistance += ai.distance;
                totalFuelCost += ai.fuelCost;
                totalTimeCost += ai.timeCost;

                optimalPath.get(optimalPath.indexOf(ai)).Heading = p.calculateHeading(ai.thisAirport.APRTlatitude, ai.thisAirport.APRTlongitude, optimalPath.get(optimalPath.indexOf(ai)+1).thisAirport.APRTlatitude, optimalPath.get(optimalPath.indexOf(ai)+1).thisAirport.APRTlongitude);
                if (optimalPath.indexOf(ai)+1 == optimalPath.size()-1){
                    System.out.println("End of path");
                    break;
                }
                totalStops++;
            }
        } catch (Exception e) {
            System.out.println("No path found");
            this.optimalPath = new ArrayList<>();
            throw new Exception("No path found");
        }
        setDisplay();
    }
    public class smallbtn extends JButton {

        public smallbtn(String text) {
            //super(text);
            setPreferredSize(new Dimension(5, 5));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            int width = getWidth();
            int height = getHeight();

            g.setColor(Color.RED);

            g.fillOval(0, 0, width - 1, height - 1);
        }
    }
    public flightPlan(){
        //empty constructor
    }
    public static void main(String[] args){
        DataBaseManager db = new DataBaseManager("./src/dbDir/airports.txt", "./src/dbDir/airplanes.txt");
        ArrayList<Airplane> src = db.searchAirplanes("lowrang");
        Airplane ap = src.get(0);
        System.out.println(ap.forPrint());
        Airport start = db.searchICAO("NNNZ");
        Airport end = db.searchICAO("AAAA");

        //System.out.println(start.forPrint());
        try {
            flightPlan fp = new flightPlan(db.aprts, start,end, ap);
            for (AirportInfo ai : fp.optimalPath){
                System.out.println(ai.thisAirport.forPrint());
                System.out.println("Heading: " + ai.Heading);
            }
            System.out.println("Fuel cost " + fp.totalFuelCost);
            System.out.println("Total Distance " + fp.totalDistance);
            System.out.println("d2: " + fp.optimalPath.get(0).distance);
            System.out.println("Total Time " + fp.totalTimeCost);
            System.out.println("Total Stops " + fp.totalStops);
            JFrame frame = new JFrame();
            frame.setSize(540, 270);
            frame.setVisible(true);
            //frame.setLayout(new GridLayout(2,1));
            fp.setDisplay();
            //frame.add(fp.graphDisplay);
            frame.add(fp.pathDisplay);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}