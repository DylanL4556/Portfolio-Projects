import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.lang.*;
import java.lang.String;
import java.text.*;
public class modifyAirportPanel extends JPanel{
    public Airport selected;
    public ArrayList<Airport> airportsSearch;
    public JPanel results;
    public JPanel modifyPanel;
    public ArrayList<JButton> airportButtons;
    public JButton selectedAirport;
    public JTextField searchbar;
    public String srcText;
    public JTextField icao;
    public JTextField name;
    public JTextField Longitude;
    public JTextField Latitude;
    public JTextField frequency;
    public JRadioButton JA;
    public JRadioButton AVGAS;
    public DataBaseManager db = new DataBaseManager("src/dbDir/airports.txt", "src/dbDir/airplanes.txt");

    public JScrollPane scrollPane;
    public modifyAirportPanel(){
        setLayout(null);
        searchbar = new JTextField("");
        searchbar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                airportButtons = new ArrayList<JButton>();
                srcText = searchbar.getText();
                String searchText = searchbar.getText();
                db.aprts = db.readAirports();
                airportsSearch = db.searchAirports(searchText);
                results.removeAll();
                results.setLayout(new GridLayout(airportsSearch.size(), 1, 5, 5));

                //airportButtons = new ArrayList<JButton>();
                for (Airport aa: airportsSearch){
                    JButton airportLabel = new JButton(aa.forPrint());
                    airportLabel.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            selected = aa;
                            selectedAirport = (JButton) e.getSource();
                            modifyPanel();
                            modifyPanel.revalidate();
                            modifyPanel.repaint();
                        }
                    });
                    results.add(airportLabel);
                    airportButtons.add(airportLabel);
                }
                getParent().revalidate();
                getParent().repaint();
            }
        });
        searchbar.setBounds(10, 10, 200, 25);
        JLabel searchLabel = new JLabel("Search Airports by Name or ICAO, or fuel type (JA-a) or (AVGAS)");
        searchLabel.setBounds(10, 40, 400, 25);
        results = new JPanel();

        scrollPane = new JScrollPane(results, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBounds(10, 70, 1000, 200);
        add(searchbar);
        add(searchLabel);
        add(scrollPane);
        revalidate();
        repaint();
    }
    public void modifyPanel(){
        if (modifyPanel != null){
            remove(modifyPanel);
        }
        modifyPanel = new JPanel();
        modifyPanel.setLayout(null);

        icao = new JTextField(selected.CAOid);
        name = new JTextField(selected.APTname);
        Longitude = new JTextField(Double.toString(selected.APRTlongitude));
        Latitude = new JTextField(Double.toString(selected.APRTlatitude));
        frequency = new JTextField(Double.toString(selected.freq));
        JA = new JRadioButton("JA-a");
        AVGAS = new JRadioButton("AVGAS");
        JA.setSelected(false);
        AVGAS.setSelected(false);
        if (selected.APRTfuelTypes[0].equals("AVGAS")){
            AVGAS.setSelected(true);
        } else if (selected.APRTfuelTypes[0].equals("JA-a")){
            JA.setSelected(true);
        }
        if (selected.APRTfuelTypes.length > 1 && selected.APRTfuelTypes[1] != null){
            if (selected.APRTfuelTypes[1].equals("AVGAS")){
                AVGAS.setSelected(true);
            } else if (selected.APRTfuelTypes[1].equals("JA-a")){
                JA.setSelected(true);
            }
        }
        JButton modifyButton = new JButton("Modify");
        modifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isValidICAO(icao.getText())){
                    JOptionPane.showMessageDialog(null, "Invalid ICAO");
                    return;
                }
                if (icao.getText().equals("") || name.getText().equals("") || Longitude.getText().equals("") || Latitude.getText().equals("") || frequency.getText().equals("")){
                    JOptionPane.showMessageDialog(null, "Please fill out all fields");
                    return;
                }
                if (!isValidNumber(Longitude.getText()) || !isValidNumber(Latitude.getText()) || !isValidNumber(frequency.getText())){
                    JOptionPane.showMessageDialog(null, "Longitude, Latitude, and Frequency must be numbers");
                    return;
                }
                if (!JA.isSelected() && !AVGAS.isSelected()){
                    JOptionPane.showMessageDialog(null, "Please select a fuel type");
                    return;
                }
                if (Double.parseDouble(Longitude.getText()) > 180 || Double.parseDouble(Longitude.getText()) < -180){
                    JOptionPane.showMessageDialog(null, "Longitude must be between -180 and 180");
                    return;
                }
                if (Double.parseDouble(Latitude.getText()) > 90 || Double.parseDouble(Latitude.getText()) < -90){
                    JOptionPane.showMessageDialog(null, "Latitude must be between -90 and 90");
                    return;
                }
                if (decCheck(Longitude.getText()) || decCheck(Latitude.getText()) || decCheck(frequency.getText())){
                    JOptionPane.showMessageDialog(null, "Longitude, Latitude, and Frequency must have at most 8 decimal places");
                    return;
                }
                if (name.getText().length() > 50){
                    JOptionPane.showMessageDialog(null, "Name must be less than 50 characters");
                    return;
                }
                if (Double.parseDouble(frequency.getText()) < 0){
                    JOptionPane.showMessageDialog(null, "Frequency must be positive");
                    return;
                }

                ArrayList<String> fts = new ArrayList<>();
                if (AVGAS.isSelected()){
                    fts.add("AVGAS");
                }
                if (JA.isSelected()){
                    fts.add("JA-a");
                }
                String[] fuelTypes = new String[fts.size()];
                for (int i = 0; i < fts.size(); i++){
                    fuelTypes[i] = fts.get(i);
                }
                Airport newAirport = new Airport(icao.getText().trim(), name.getText().trim(), fuelTypes, Double.parseDouble(Longitude.getText()), Double.parseDouble(Latitude.getText()), Double.parseDouble(frequency.getText()));
                if (newAirport.equals(selected)){
                    JOptionPane.showMessageDialog(null, "No changes made");
                    return;
                }
                if (db.containsAirport(db.aprts,newAirport)){
                    JOptionPane.showMessageDialog(null, "Airport already exists");
                    return;
                }

                db.modifyAirport(selected, newAirport);



                JOptionPane.showMessageDialog(null, "Airport modified");
                selected = newAirport;
                refreshSrch();
                getParent().revalidate();
                getParent().repaint();
            }
        });
        JLabel icaoLabel = new JLabel("ICAO: ");
        JLabel nameLabel = new JLabel("Name: ");
        JLabel LongitudeLabel = new JLabel("Longitude: ");
        JLabel LatitudeLabel = new JLabel("Latitude: ");
        JLabel frequencyLabel = new JLabel("Frequency: ");
        icaoLabel.setBounds(10, 10, 100, 25);
        icao.setBounds(10,40,100,25);
        nameLabel.setBounds(10, 70, 100, 25);
        name.setBounds(10, 100, 200, 25);
        LongitudeLabel.setBounds(10, 130, 200, 25);
        Longitude.setBounds(10, 160, 200, 25);
        LatitudeLabel.setBounds(10, 190, 200, 25);
        Latitude.setBounds(10, 220, 200, 25);
        frequencyLabel.setBounds(350, 10, 200, 25);
        frequency.setBounds(350, 40, 200, 25);
        JA.setBounds(350, 70, 100, 25);
        AVGAS.setBounds(350, 100, 100, 25);
        modifyButton.setBounds(350, 130, 100, 25);

        modifyPanel.add(icaoLabel);
        modifyPanel.add(icao);
        modifyPanel.add(nameLabel);
        modifyPanel.add(name);
        modifyPanel.add(LongitudeLabel);
        modifyPanel.add(Longitude);
        modifyPanel.add(LatitudeLabel);
        modifyPanel.add(Latitude);
        modifyPanel.add(frequencyLabel);
        modifyPanel.add(frequency);
        modifyPanel.add(JA);
        modifyPanel.add(AVGAS);
        modifyPanel.add(modifyButton);
        modifyPanel.setBounds(10, 300, 1000, 300);
        modifyPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        modifyPanel.revalidate();
        modifyPanel.repaint();
        add(modifyPanel);
        validate();
        repaint();
    }
    public boolean isValidICAO(String input) {
        return input.matches("^[A-Z]{3,4}$");
    }
    public boolean isValidNumber(String input) {
        try {
            Double.parseDouble(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public boolean decCheck(String input) {
        return input.matches("^-?\\d*\\.\\d{9,}$");
    }
    public void refreshSrch(){
        results.removeAll();
        results.setLayout(new GridLayout(airportsSearch.size(), 1, 5, 5));
        //scrollPane.removeAll();

        airportButtons.removeAll(airportButtons);
        //airportButtons = new ArrayList<JButton>();
        db.aprts = db.readAirports();
        airportsSearch = db.searchAirports(srcText);

        for (Airport aa: airportsSearch){
            JButton airportLabel = new JButton(aa.forPrint());
            airportLabel.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    selected = aa;
                    selectedAirport = (JButton) e.getSource();
                    modifyPanel();
                    modifyPanel.revalidate();
                    modifyPanel.repaint();
                }
            });
            results.add(airportLabel);
            airportButtons.add(airportLabel);
        }
        //scrollPane = new JScrollPane(results, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.revalidate();
        scrollPane.repaint();
        getParent().revalidate();
        getParent().repaint();
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.add(new modifyAirportPanel());
        frame.setVisible(true);
    }

}
