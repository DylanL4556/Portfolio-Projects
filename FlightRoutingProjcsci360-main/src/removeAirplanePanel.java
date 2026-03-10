import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.lang.*;
public class removeAirplanePanel extends JPanel{
    public ArrayList<Airplane> airplanesSearch;
    public JPanel results;
    public Airplane selectedAirplane;

    public removeAirplanePanel(){
        setLayout(null);
        JLabel label = new JLabel("Remove Airplane");
        JTextField searchbar = new JTextField("");
        JLabel searchWarning = new JLabel("<html> Capital sensitive, search make, model, and fuel type, or search make and model for a specific option, <br> to search the specific plane include a comma, the syntax is Make,model (spaces do not matter)</html>");
        JLabel info = new JLabel("Simply click the airplane and confirm your selection to remove it.");
        results = new JPanel();
        searchbar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchText = searchbar.getText();
                DataBaseManager db = new DataBaseManager("src/dbDir/airports.txt", "src/dbDir/airplanes.txt");

                airplanesSearch = db.searchAirplanes(searchText);
                results.removeAll();
                results.setLayout(new GridLayout(airplanesSearch.size(), 1, 5, 5));
                for (Airplane aa: airplanesSearch){
                    JButton airplaneLabel = new JButton(aa.forPrint());
                    airplaneLabel.setSize(400,25);
                    airplaneLabel.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            // Set the selected airplane to the airplane associated with the button
                            selectedAirplane = aa;
                            int response = JOptionPane.showConfirmDialog(null,
                                    "Do you want to remove the airplane: \n" + selectedAirplane.forPrint() + "?",
                                    "Confirm",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.QUESTION_MESSAGE);

                            // If the user clicked "Yes", remove the airplane from the database
                            if (response == JOptionPane.YES_OPTION) {
                                db.removeAirplane(selectedAirplane);
                                System.out.println(selectedAirplane.forPrint() + " has been removed");
                                results.remove(airplaneLabel);
                                results.revalidate();
                                results.repaint();
                            }
                        }
                    });
                    results.add(airplaneLabel);
                }
                getParent().revalidate();
                getParent().repaint();
            }
        });

        JScrollPane scroll = new JScrollPane(results, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBounds(10, 105, 1000, 100);
        label.setBounds(10, 10, 100, 25);
        searchbar.setBounds(10, 40, 200, 25);
        searchWarning.setBounds(10, 60, 800, 50);
        info.setBounds(10, 205, 400, 25);

        add(label);
        add(searchbar);
        add(searchWarning);
        add(info);
        add(scroll);
    }

    public static void main(String[] args){
        JFrame frame = new JFrame("Remove Airplane");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 400);
        frame.add(new removeAirplanePanel());
        frame.setVisible(true);
    }
}
