import javax.swing.*;
import java.util.TreeSet;
/**
 * Created by Алексей on 27.04.2017.
 */
public class AddingButton extends JButton{
    AddingButton(JSpinner JSP, TreeSet<Human> col,LabTable table,JProgressBar jpb){
    addActionListener(new AddingListener(JSP,col,table,jpb));
    }
}
