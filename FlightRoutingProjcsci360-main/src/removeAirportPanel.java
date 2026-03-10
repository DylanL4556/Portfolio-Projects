import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.lang.*;
public class removeAirportPanel extends JPanel {
    public ArrayList<Airport> airportsSearch;
    public JPanel results;
    public Airport selectedAirport;
    public removeAirportPanel() {
        setLayout(null);
        JLabel label = new JLabel("Remove Airport");
        JTextField searchbar = new JTextField("");
        JLabel searchWarning = new JLabel("Capital sensitive, ICAO or Name only, partial search is supported");
        results = new JPanel();
        JLabel info = new JLabel("Simply click the airport and confirm your selection to remove it.");

        searchbar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchText = searchbar.getText();
                DataBaseManager db = new DataBaseManager("src/dbDir/airports.txt", "src/dbDir/airplanes.txt");

                airportsSearch = db.searchAirports(searchText);
                results.removeAll();
                results.setLayout(new GridLayout(airportsSearch.size(), 1, 5, 5));
                for (Airport aa: airportsSearch){
                    JButton airportLabel = new JButton(aa.forPrint());
                    airportLabel.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            // Set the selected airport to the airport associated with the button
                            selectedAirport = aa;
                            int response = JOptionPane.showConfirmDialog(null,
                                    "Do you want to remove the airport: \n" + selectedAirport.forPrint() + "?",
                                    "Confirm",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.QUESTION_MESSAGE);

                            // If the user clicked "Yes", remove the airport from the database
                            if (response == JOptionPane.YES_OPTION) {
                                db.removeAirport(selectedAirport);
                                System.out.println(selectedAirport.forPrint() + " has been removed");
                                results.remove(airportLabel);
                                results.revalidate();
                                results.repaint();
                            }
                        }
                    });
                    results.add(airportLabel);
                }
                getParent().revalidate();
                getParent().repaint();
            }
        });


        JScrollPane scrollPane = new JScrollPane(results, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBounds(10, 100, 1000, 100);
        searchbar.setBounds(10, 40, 150, 25);
        info.setBounds(170, 40, 400, 25);
        label.setBounds(10, 10, 100,25);
        searchWarning.setBounds(10, 70, 400, 25);

        add(info);
        add(scrollPane);
        add(label);
        add(searchWarning);
        add(searchbar);
    }

    public static void main(String[] args){
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.add(new removeAirportPanel());
        frame.setVisible(true);
    }
}
