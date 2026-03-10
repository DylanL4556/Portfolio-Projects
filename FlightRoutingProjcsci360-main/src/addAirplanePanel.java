import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class addAirplanePanel extends JPanel{
	//Gui Elements
	//Two parents panels, panInput and panBtm
	//panInput holds the input elemetns and panBtm holds the Add btn and Error label
	private JPanel panInput = new JPanel();
	
	private JPanel panMake = new JPanel();
	private JLabel lblMake = new JLabel("Please enter the Make");
	private JTextField txtMake = new JTextField("Make");
	private MakeVerifier vMake = new MakeVerifier();
	
	private JPanel panModel = new JPanel();
	private JLabel lblModel = new JLabel("Please enter the Model");
	private JTextField txtModel = new JTextField("Model");
	private ModelVerifier vModel = new ModelVerifier();
	
	private JPanel panType = new JPanel();
	private JLabel lblType = new JLabel("Please select the type of plane");
	private ButtonGroup bg = new ButtonGroup();
	private JRadioButton rbtnType1 = new JRadioButton("Prop");
	private JRadioButton rbtnType2= new JRadioButton("Turbo-prop");
	private JRadioButton rbtnType3 = new JRadioButton("Jet");
	private TypeVerifier vType1 = new TypeVerifier();
	private TypeVerifier vType2 = new TypeVerifier();
	private TypeVerifier vType3 = new TypeVerifier();
	
	private JPanel panCap = new JPanel();
	private JLabel lblCap = new JLabel("Please enter Fuel Capacity (Liters)");
	private JTextField txtCap = new JTextField("Capacity");
	private CapVerifier vCap = new CapVerifier();
	
	private JPanel panBurn = new JPanel();
	private JLabel lblBurn = new JLabel("Please enter fuel burn rate at cruise (Liters/Hour)");
	private JTextField txtBurn = new JTextField("Burn Rate");
	private BurnVerifier vBurn = new BurnVerifier();
	
	private JPanel panSpeed = new JPanel();
	private JLabel lblSpeed = new JLabel("Please enter cruise speed (knots)");
	private JTextField txtSpeed = new JTextField("Speed");
	private SpeedVerifier vSpeed = new SpeedVerifier();
	
	private JPanel panBtm = new JPanel();
	private JButton btnAdd = new JButton("Add");
	private JLabel lblError = new JLabel("Secret Error unlocked; +10 pnts");
	
	//Set for creation of new Airplane object
	private String airplaneMake;
	private String airplaneModel;
	private String airplaneType;
	private double airplaneCap;
	private double airplaneBurn;
	private double airplaneSpeed;
	
	private DataBaseManager DB = new DataBaseManager("./src/dbDir/airports.txt", "./src/dbDir/airplanes.txt");
	
	public addAirplanePanel() {
		setLayout(new BorderLayout());
		panMake.add(lblMake);
		panMake.add(txtMake);
		panInput.add(panMake);
		txtMake.setInputVerifier(vMake);
		
		panModel.add(lblModel);
		panModel.add(txtModel);
		panInput.add(panModel);
		txtModel.setInputVerifier(vModel);
		
		panType.add(lblType);
		bg.add(rbtnType1);
		bg.add(rbtnType2);
		bg.add(rbtnType3);
		panType.add(rbtnType1);
		panType.add(rbtnType2);
		panType.add(rbtnType3);
		panInput.add(panType);
		rbtnType1.setInputVerifier(vType1);
		rbtnType2.setInputVerifier(vType2);
		rbtnType3.setInputVerifier(vType3);
		
		
		panCap.add(lblCap);
		panCap.add(txtCap);
		panInput.add(panCap);
		txtCap.setInputVerifier(vCap);
		
		panBurn.add(lblBurn);
		panBurn.add(txtBurn);
		panInput.add(panBurn);
		txtBurn.setInputVerifier(vBurn);
		
		panSpeed.add(lblSpeed);
		panSpeed.add(txtSpeed);
		panInput.add(panSpeed);
		txtSpeed.setInputVerifier(vSpeed);
		
		panInput.setLayout(new GridLayout(6,1,5,5));
		add(panInput,BorderLayout.CENTER);
		
		
		panBtm.add(btnAdd);
		lblError.setVisible(false);
		panBtm.add(lblError);
		add(panBtm,BorderLayout.SOUTH);
		
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("add");
				
				if (vMake.verify(txtMake)) {
					if(vModel.verify(txtModel)) {
						if(vType1.verify(rbtnType1)) {
							if(vType2.verify(rbtnType2)) {
								if(vType3.verify(rbtnType3)) {
									if(vCap.verify(txtCap)) {
										if(vBurn.verify(txtBurn)) {
											if(vSpeed.verify(txtSpeed)) {
												System.out.println("Verified");
												Airplane ar = new Airplane(airplaneMake, airplaneModel, airplaneType,airplaneCap, airplaneBurn, airplaneSpeed);
												DB.addAirplane(ar);
												System.out.println("added");
											}
										}
									}
								}
							}
						}
					}
				}
				
			}
		});
	}
	class MakeVerifier extends InputVerifier{
		public boolean verify(JComponent input) {
			revalidate();
			repaint();
			try {
				String make = txtMake.getText();
				if(make.equals("") || make.length()>20) {
					lblError.setText("Make cannot be blank or over length 20");
					lblError.setVisible(true);
					btnAdd.setEnabled(false);
					return false;
				}
			}
			catch(Exception e) {
				lblError.setText(e.getMessage());
				lblError.setVisible(true);
				btnAdd.setEnabled(false);
				return false;
			}
			lblError.setVisible(false);
			airplaneMake = txtMake.getText();
			btnAdd.setEnabled(true);
			return true;
		}
	}
	class ModelVerifier extends InputVerifier{
		public boolean verify(JComponent input) {
			revalidate();
			repaint();
			try {
				String model = txtModel.getText();
				if(model.equals("") || model.length() > 20) {
					lblError.setText("Model cannotbe blank or over length 20");
					lblError.setVisible(true);
					btnAdd.setEnabled(false);
					return false;
				}
			}
			catch(Exception e) {
				lblError.setText(e.getMessage());
				lblError.setVisible(true);
				System.out.println("meow; ilmgf"); //can safely ignore
				btnAdd.setEnabled(false);
				return false;
			}
			lblError.setVisible(false);
			airplaneModel = txtModel.getText();
			btnAdd.setEnabled(true);
			return true;
		}
	}
	class TypeVerifier extends InputVerifier{
		public boolean verify(JComponent input) {
			revalidate();
			repaint();
			try {
				if(rbtnType1.isSelected() || rbtnType2.isSelected() || rbtnType3.isSelected()) {
					lblError.setVisible(false);
					if(rbtnType1.isSelected()) {
						airplaneType = "prop";
					}
					else if(rbtnType2.isSelected()){
						airplaneType = "turboprop";
					}
					else if(rbtnType3.isSelected()) {
						airplaneType = "jet";
					}
					btnAdd.setEnabled(true);
					return true;
				}
			}
			catch(Exception e) {
				lblError.setText(e.getMessage());
				lblError.setVisible(true);
			}
			lblError.setText("Must select a type");
			lblError.setVisible(true);
			btnAdd.setEnabled(false);
			return false;
		}
	}
	class CapVerifier extends InputVerifier{
		public boolean verify(JComponent input) {
			revalidate();
			repaint();
			try {
				double cap = Double.valueOf(txtCap.getText());
				String capText = txtCap.getText();
				//System.out.println(capText);
				double sciCap = Double.valueOf(String.format("%.5E", cap));
				if(sciCap <= Double.MAX_VALUE) {
					if(cap >= 0) {
						if(capText.matches("\\d+\\.\\d{0,5}$") || capText.matches("\\d+")) {
							lblError.setVisible(false);
							airplaneCap = cap;
							btnAdd.setEnabled(true);
							return true;
						}
						else {
							lblError.setText("Cap cannot be blank or more than 5 decimal places");
							lblError.setVisible(true);
							btnAdd.setEnabled(false);
							return false;
						}
					}
					else {
						lblError.setText("Capacity must be > 0");
						lblError.setVisible(true);
						btnAdd.setEnabled(false);
						return false;
					}
				}
				else{
					lblError.setText("Capacity is too large");
					lblError.setVisible(true);
					btnAdd.setEnabled(false);
					return false;
				}
			}
			catch(Exception e) {
				lblError.setText(e.getMessage());
				lblError.setVisible(true);
				
			}
			btnAdd.setEnabled(false);
			return false;
		}
	}
	class BurnVerifier extends InputVerifier{
		public boolean verify(JComponent input) {
			revalidate();
			repaint();
			try {
				double burn = Double.valueOf(txtBurn.getText());
				String burnText = txtBurn.getText();
				double sciBurn = Double.valueOf(String.format("%.5E",burn));
				if(sciBurn <= Double.MAX_VALUE) {
					if(burn >= 0) {
						if(burnText.matches("\\d+\\.\\d{0,5}$") || burnText.matches("\\d+")) {
							lblError.setVisible(false);
							airplaneBurn = burn;
							btnAdd.setEnabled(true);
							return true;
						}
						else {
							lblError.setText("Burn cannot be blank or more than 5 decimal places");
							lblError.setVisible(true);
							btnAdd.setEnabled(false);
							return false;
						}
					}
					else {
						lblError.setText("Burn must be > 0");
						lblError.setVisible(true);
						btnAdd.setEnabled(false);
						return false;
					}
				}
				else{
					lblError.setText("Burn Rate is too large");
					lblError.setVisible(true);
					btnAdd.setEnabled(false);
					return false;
				}
					
			}
			catch(Exception e) {
				lblError.setText(e.getMessage());
				lblError.setVisible(true);
			}
			btnAdd.setEnabled(false);
			return false;
		}
	}
	class SpeedVerifier extends InputVerifier{
		public boolean verify(JComponent input) {
			revalidate();
			repaint();
			try {
				double speed = Double.valueOf(txtSpeed.getText());
				String speedText = txtSpeed.getText();
				double sciSpeed = Double.valueOf(String.format("%.5E",speed));
				if(sciSpeed <= Double.MAX_VALUE) {
					if(speed >= 0) {
						if(speedText.matches("\\d+\\.\\d{0,5}$") || speedText.matches("\\d+")) {
							lblError.setVisible(false);
							airplaneSpeed = speed;
							btnAdd.setEnabled(true);
							return true;
						}
						else {
							lblError.setText("Speed cannot be blank or more than 5 decimal places");
							lblError.setVisible(true);
							btnAdd.setEnabled(false);
							return false;
						}
					}
					else {
						lblError.setText("Speed must be > 0");
						lblError.setVisible(true);
						btnAdd.setEnabled(false);
						return false;
					}

				}
				else{
					lblError.setText("Speed is too large");
					lblError.setVisible(true);
					btnAdd.setEnabled(false);
					return false;
				}
			}
			catch(Exception e) {
				lblError.setText(e.getMessage());
				lblError.setVisible(true);
				btnAdd.setEnabled(false);
				return false;
			}
		}
	}
	

}