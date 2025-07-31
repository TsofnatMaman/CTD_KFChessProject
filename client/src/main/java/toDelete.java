import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.awt.Frame;

public class toDelete extends JFrame{
    public static void main(String[] args){
        JOptionPane optionPane = new JOptionPane("aaaa", JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION);
        JDialog waitDialog = new JDialog((Frame) null, "Waiting for opponent", false);
        waitDialog.setContentPane(optionPane);
        waitDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        waitDialog.pack();
        waitDialog.setLocationRelativeTo(null);
        waitDialog.setVisible(true);
    }
    
}
