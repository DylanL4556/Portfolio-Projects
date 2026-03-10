import javax.swing.*;
import java.awt.*;

public class ErrorFrame extends JFrame {

    public ErrorFrame(String errorMessage) {
        super("Error");
        setSize(300, 200);
        setLayout(new FlowLayout());

        JLabel errorLabel = new JLabel(errorMessage);
        add(errorLabel);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> dispose());
        add(okButton);

        setVisible(true);
    }
}