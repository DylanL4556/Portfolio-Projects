import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.lang.*;
import java.lang.String;
import java.text.*;

public class modifyAirplanePanel extends JPanel{
    public JButton selected;
    public Airplane selectedAirplane;
    public JPanel results;
    public ArrayList<Airplane> airplanesSearch;
    public JTextField searchbar;
    public JTextField make;
    public JTextField model;
    public JTextField fuel;
    public JTextField fuelCapacity;
    public JTextField fuelConsumption;
    public JTextField speed;
    public JButton modifyButton;
    public JPanel modifyPanel;
    public Airplane newAirplane;
    public String srcText;
    public DataBaseManager db = new DataBaseManager("src/dbDir/airports.txt", "src/dbDir/airplanes.txt");

    public modifyAirplanePanel(){
        setLayout(null);
        searchbar = new JTextField("");
        searchbar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchText = searchbar.getText();
                srcText = searchText;


                airplanesSearch = db.searchAirplanes(searchText);
                results.removeAll();
                results.setLayout(new GridLayout(airplanesSearch.size(), 1, 5, 5));
                for (Airplane aa: airplanesSearch){
                    JButton airplaneLabel = new JButton(aa.forPrint());
                    //airplaneLabel.setSize(400,25);
                    airplaneLabel.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            // Set the selected airplane to the airplane associated with the button when user clicks the button
                            selectedAirplane = aa;
                            selected = (JButton) e.getSource();
                            modifyPanel();
                            modifyPanel.revalidate();
                            modifyPanel.repaint();
                        }
                    });
                    results.add(airplaneLabel);
                }
                getParent().revalidate();
                getParent().repaint();
            }
        });
        searchbar.setBounds(10, 10, 200, 25);
        JLabel searchWarning = new JLabel("<html> Capital sensitive, search make, model, and fuel type, or search make and model for a specific option, <br> to search the specific plane include a comma, the syntax is Make,model (spaces do not matter) --decimals are cut for display</html>");
        searchWarning.setBounds(10, 40, 600, 80);
        results = new JPanel();
        JScrollPane scroll = new JScrollPane(results, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBounds(10, 130, 1000, 200);
        add(searchbar);
        add(searchWarning);
        add(scroll);
        validate();
        repaint();

    }
    public void refreshSearch(){
        results.removeAll();

        db.aplanes = db.readAirplanes();
        airplanesSearch = db.searchAirplanes(srcText);

        results.setLayout(new GridLayout(airplanesSearch.size(), 1, 5, 5));
        for (Airplane aa: airplanesSearch){
            JButton airplaneLabel = new JButton(aa.forPrint());
            //airplaneLabel.setSize(400,25);
            airplaneLabel.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Set the selected airplane to the airplane associated with the button when user clicks the button
                    selectedAirplane = aa;
                    selected = (JButton) e.getSource();
                    modifyPanel();
                    modifyPanel.revalidate();
                    modifyPanel.repaint();
                }
            });
            results.add(airplaneLabel);
        }
        getParent().revalidate();
        getParent().repaint();
    }
    public void modifyPanel(){
        if (modifyPanel != null){
            remove(modifyPanel);

        }
        //DataBaseManager db = new DataBaseManager("src/dbDir/airports.txt", "src/dbDir/airplanes.txt");
        db.aplanes = db.readAirplanes();
        make = new JTextField(selectedAirplane.make);
        model = new JTextField(selectedAirplane.model);
        fuel = new JTextField(selectedAirplane.fuel);
        fuelCapacity = new JTextField(Double.toString(selectedAirplane.fuelCapacity));
        fuelConsumption = new JTextField(Double.toString(selectedAirplane.fuelConsumption));
        speed = new JTextField(Double.toString(selectedAirplane.speed));


        modifyButton = new JButton("Modify");
        modifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (newAirplane != null){
                    newAirplane = null;

                }
                if (!isValidNumber(fuelCapacity.getText().trim()) || !isValidNumber(fuelConsumption.getText().trim()) || !isValidNumber(speed.getText().trim())) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid number");
                    return;
                }
                if (make.getText().equals("") || model.getText().equals("") || fuel.getText().equals("") || fuelCapacity.getText().equals("") || fuelConsumption.getText().equals("") || speed.getText().equals("")){
                    JOptionPane.showMessageDialog(null, "Please fill out all fields");
                    return;
                }
                if (Double.parseDouble(fuelCapacity.getText()) <= 0 || Double.parseDouble(fuelConsumption.getText()) <= 0 || Double.parseDouble(speed.getText()) <= 0){
                    JOptionPane.showMessageDialog(null, "Please enter a valid number");
                    return;
                }
                if (!Objects.equals(fuel.getText().toLowerCase().trim(), "jet") && (!Objects.equals(fuel.getText().toLowerCase().trim(), "turbofan")) && (!Objects.equals(fuel.getText().toLowerCase().trim(), "prop")) ){
                    JOptionPane.showMessageDialog(null, "Please enter a valid fuel type (jet, turbofan, or prop)");
                    return;
                }
                if ((Double.parseDouble(fuelCapacity.getText()) >= Double.MAX_VALUE) || (Double.parseDouble(fuelConsumption.getText()) >= Double.MAX_VALUE) || (Double.parseDouble(speed.getText()) >= Double.MAX_VALUE)){
                    JOptionPane.showMessageDialog(null, "Please enter a valid number");
                    return;
                }
                newAirplane = new Airplane(make.getText().toLowerCase().trim(), model.getText().trim(), fuel.getText().toLowerCase().trim(), Double.parseDouble(fuelCapacity.getText()), Double.parseDouble(fuelConsumption.getText()), Double.parseDouble(speed.getText()));
                if (db.containsAirplane(db.aplanes,newAirplane)){
                    JOptionPane.showMessageDialog(null, "This airplane already exists");
                    return;
                }
                if (decCheck(fuelCapacity.getText()) || decCheck(fuelConsumption.getText()) || decCheck(speed.getText())) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid number (limited to .#####)");
                    return;
                }

                db.modifyAirplane(selectedAirplane, newAirplane);

                JOptionPane.showMessageDialog(null, "Airplane has been modified");
                selected.setText(newAirplane.forPrint());
                selectedAirplane = newAirplane;
                refreshSearch();
                //refresh();
                getParent().revalidate();
                getParent().repaint();
                System.out.println(selectedAirplane.forPrint() + " has been modified to " + newAirplane.forPrint());
            }
        });

        modifyPanel = new JPanel();
        modifyPanel.setLayout(null);
        JLabel makeLabel = new JLabel("Make: ");
        JLabel modelLabel = new JLabel("Model: ");
        JLabel fuelLabel = new JLabel("Fuel Type: ");
        JLabel fuelCapacityLabel = new JLabel("Capacity(L): (limited to .#####)");
        JLabel fuelConsumptionLabel = new JLabel("Consumption(L/HR): (limited to .#####)");
        JLabel speedLabel = new JLabel("Speed: (limited to .#####)");
        makeLabel.setBounds(10, 10, 200, 25);
        make.setBounds(10, 40, 200, 25);
        modelLabel.setBounds(10, 70, 200, 25);
        model.setBounds(10, 100, 200, 25);
        fuelLabel.setBounds(10, 130, 200, 25);
        fuel.setBounds(10, 160, 200, 25);
        fuelCapacityLabel.setBounds(10, 190, 200, 25);
        fuelCapacity.setBounds(10, 220, 200, 25);
        fuelConsumptionLabel.setBounds(350, 10, 250, 25);
        fuelConsumption.setBounds(350, 40, 200, 25);
        speedLabel.setBounds(350, 70, 200, 25);
        speed.setBounds(350, 100, 200, 25);
        modifyButton.setBounds(350, 130, 200, 25);
        modifyPanel.add(makeLabel);
        modifyPanel.add(make);
        modifyPanel.add(modelLabel);
        modifyPanel.add(model);
        modifyPanel.add(fuelLabel);
        modifyPanel.add(fuel);
        modifyPanel.add(fuelCapacityLabel);
        modifyPanel.add(fuelCapacity);
        modifyPanel.add(fuelConsumptionLabel);
        modifyPanel.add(fuelConsumption);
        modifyPanel.add(speedLabel);
        modifyPanel.add(speed);
        modifyPanel.add(modifyButton);
        modifyPanel.setBounds(10, 330, 1000, 300);
        modifyPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        modifyPanel.revalidate();
        modifyPanel.repaint();
        add(modifyPanel);
        validate();
        repaint();
        }
        public boolean decCheck(String input) {
            return input.matches("^-?\\d*\\.\\d{6,}$");
        }
        public boolean isValidNumber(String input) {
            try {
                Double.parseDouble(input);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        public static void main(String[] args){
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 1000);
            frame.add(new modifyAirplanePanel());
            frame.setVisible(true);
        }
}
