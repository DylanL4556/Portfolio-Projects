import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class addAirportPanel extends JPanel{
	private JPanel panIcao = new JPanel();
	private JLabel lblIcao = new JLabel("ICAO:");
	private JTextField txtIcao = new JTextField("Please enter ICAO");
	
	private JPanel panName = new JPanel();
	private JLabel lblName = new JLabel("Name of the Airport:");
	private JTextField txtName = new JTextField("Please enter the Name");
	
	private JPanel panLatLong = new JPanel();
	private JLabel lblLatLong = new JLabel("Please enter longitude, then enter latitude");
	private JTextField txtLat = new JTextField("Latitude");
	private JTextField txtLong = new JTextField("Longitude");
	
	private JPanel panComm = new JPanel();
	private JLabel lblComm = new JLabel("Please enter radio freqency of comms");
	private JTextField txtComm = new JTextField("Comms Frequency");
	
	private JPanel panFuel = new JPanel();
	private JLabel lblFuel = new JLabel("Please enter fuel type (AVGAS, JA-a, or both");
	//private ButtonGroup btnFuel = new ButtonGroup();
	private JRadioButton rdFuel1 = new JRadioButton("AVGAS");
	private JRadioButton rdFuel2 = new JRadioButton("JA-a");
	
	JButton jbtAdd = new JButton("Add");
	JPanel panAdd = new JPanel();
	JLabel lblError = new JLabel("Please report this for $1000*");
	
	private String airportICAO;
	private String airportName;
	private double airportLong;
	private double airportLat;
	private double airportFreq;
	private String[] airportFuel;
	
	private DataBaseManager DB = new DataBaseManager("./src/dbDir/airports.txt", "./src/dbDir/airplanes.txt");
	
	public addAirportPanel() {
		setLayout(new GridLayout(7,1,5,5));
		
		panIcao.add(lblIcao);
		ICAOVerifier vIcao = new ICAOVerifier();
		txtIcao.setInputVerifier(vIcao);
		panIcao.add(txtIcao);
		add(panIcao);
		
		panName.add(lblName);
		NameVerifier vName = new NameVerifier();
		txtName.setInputVerifier(vName);
		panName.add(txtName);
		add(panName);
		
		panLatLong.add(lblLatLong);
		LongVerifier vLong = new LongVerifier();
		txtLong.setInputVerifier(vLong);
		panLatLong.add(txtLong);
		LatVerifier vLat = new LatVerifier();
		txtLat.setInputVerifier(vLat);
		panLatLong.add(txtLat);
		add(panLatLong);
		
		panComm.add(lblComm);
		CommVerifier vComm = new CommVerifier();
		txtComm.setInputVerifier(vComm);
		panComm.add(txtComm);
		add(panComm);
		
		panFuel.add(lblFuel);
		FuelVerifier vFuel1 = new FuelVerifier();
		rdFuel1.setInputVerifier(vFuel1);
		panFuel.add(rdFuel1);
		FuelVerifier vFuel2 = new FuelVerifier();
		rdFuel2.setInputVerifier(vFuel2);
		panFuel.add(rdFuel2);
		add(panFuel);
		
		panAdd.add(jbtAdd);
		lblError.setVisible(false);
		panAdd.add(lblError);
		add(panAdd);
		
		jbtAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	System.out.println("try add");
	            	if (vIcao.verify(txtIcao)) {
	            		if(vName.verify(txtName)) {
	            			if(vLong.verify(txtLong)) {
	            				if(vLat.verify(txtLat)) {
	            					if(vComm.verify(txtComm)) {
	            						if(vFuel1.verify(rdFuel1)) {
	            							if(vFuel2.verify(rdFuel2)) {
	            								System.out.println("Verified");	
	            								Airport nw = new Airport(airportICAO, airportName,airportFuel,airportLong,airportLat,airportFreq);
	            								DB.addAirport(nw);
	            								DB.readAirports();
	            								System.out.println("added");
	            							}
	            						}
	            					}
	            				}
	            			}
	            		}
	            	}
            	
            	
;            }
        });
		
	}
	class ICAOVerifier extends InputVerifier{
		public boolean verify(JComponent input) {
			revalidate();
			repaint();
			System.out.println("ICAO Verify");
			try {
				airportICAO = txtIcao.getText();
				//String error = lblError.getText();
				if(airportICAO.matches("^[A-Z]{3,4}$")) {
					if(airportICAO.length() == 3 || airportICAO.length() == 4) {
						if(airportICAO.toUpperCase().equals(airportICAO)) {
							lblError.setVisible(false);
							jbtAdd.setEnabled(true);
							return true;
						}
					}
				}
				lblError.setText("\n ICAO must be 3 or 4 letters and all caps");
				lblError.setVisible(true);
				jbtAdd.setEnabled(false);
				return false;
			}
			catch(Exception e) {
				lblError.setText(e.getMessage());
				lblError.setVisible(true);
			}
			jbtAdd.setEnabled(false);
			return false;
		}
	
	}
	class NameVerifier extends InputVerifier{
		public boolean verify(JComponent input) {
			revalidate();
			repaint();
			//System.out.println("Name Verify");
			try {
				airportName = txtName.getText();
				if(airportName.matches(".+") && !(airportName.equals("Please enter the Name"))) { //1+ of any char
					lblError.setVisible(false);
					jbtAdd.setEnabled(true);
					return true;
				}
				lblError.setText("Name cannot be blank or default text");
				lblError.setVisible(true);
				jbtAdd.setEnabled(false);
				return false;
			}
			catch(Exception e) {
				lblError.setText(e.getMessage());
				lblError.setVisible(true);
			}
			jbtAdd.setEnabled(false);
			return false;
		}
	}
	class LongVerifier extends InputVerifier{
		public boolean verify(JComponent input) {
			//System.out.println("Long Verify");
			revalidate();
			repaint();
			try {
				//System.out.println(txtLong.getText());
				airportLong  = Double.valueOf(txtLong.getText());
				if(airportLong >= -180.0 && airportLong <= 180.0) {
						lblError.setVisible(false);
						jbtAdd.setEnabled(true);
						return true;
					}
				lblError.setText("Longitude must be within -180 and 180");
				lblError.setVisible(true);
				jbtAdd.setEnabled(false);
				return false;
				}
			catch (Exception e) {
				lblError.setText("Longitude must be a number");
				lblError.setVisible(true);
			}
			jbtAdd.setEnabled(false);
			return false;
		}
	}
	class LatVerifier extends InputVerifier{
		public boolean verify(JComponent input) {
			revalidate();
			repaint();
			//System.out.println("Lat Verify");
			try {
				airportLat = Double.valueOf(txtLat.getText());
				if(airportLat >= -90.0 && airportLat <= 90.0) {
					lblError.setVisible(false);
					jbtAdd.setEnabled(true);
					return true;
				}
				lblError.setText("Latitude must be within -90.0 and 90.0");
				lblError.setVisible(true);
				jbtAdd.setEnabled(false);
				return false;
			}
			catch(Exception e) {
				lblError.setText("Latitude must be a number");
				lblError.setVisible(true);
			}
			jbtAdd.setEnabled(false);
			return false;
		}
	}
	class CommVerifier extends InputVerifier{
		public boolean verify(JComponent input) {
			revalidate();
			repaint();
			//System.out.println("Comm Verify");
			try {
				airportFreq = Double.valueOf(txtComm.getText());
				if(airportFreq > 0.0) {
					lblError.setVisible(false);
					jbtAdd.setEnabled(true);
					return true;
				}
				lblError.setText("Comms Frequency must be > 0");
				lblError.setVisible(true);
				jbtAdd.setEnabled(false);
				return false;
			}
			catch(Exception e){
				lblError.setText("Comms Frequency must be a number");
				lblError.setVisible(true);
			}
			jbtAdd.setEnabled(false);
			return false;
		}
	}
	class FuelVerifier extends InputVerifier{
		public boolean verify(JComponent input) {
			System.out.println("Fuel Verify");
			try {
				if(!rdFuel1.isSelected() && !rdFuel2.isSelected()) {
					lblError.setText("Must select 1 or both fuel types");
					lblError.setVisible(true);
					jbtAdd.setEnabled(false);
					return false;
				}
				else if(rdFuel1.isSelected() && rdFuel2.isSelected()) {
					airportFuel = new String[2];
					airportFuel[0] = "AVGAS";
					airportFuel[1] = "JA-a";
				}
				else if(rdFuel1.isSelected() ^ rdFuel2.isSelected()) {
					airportFuel = new String[1];
					if(rdFuel1.isSelected()) {
						airportFuel[0]="AVGAS";
					}
					else {
						airportFuel[0]="JA-a";
					}
				}
				lblError.setVisible(false);
				jbtAdd.setEnabled(true);
				return true;
			}
			catch(Exception e){
				lblError.setText(e.getLocalizedMessage());
				lblError.setVisible(true);
				jbtAdd.setEnabled(false);
			}
			return false;
		}
	}
}
