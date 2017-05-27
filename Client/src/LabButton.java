import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.TreeSet;

/**
 * Created by Денис on 25.04.2017.
 */
public class LabButton extends JButton{
    private JPanel ButtonPanel;

    LabButton(String title, LabListener listener){
        super(title);
        addActionListener(listener);
        setPreferredSize(new Dimension(125,20));
        listener.getNameField().setColumns(22);
        listener.getNameField().addKeyListener(new EnterListener(this));
        listener.getLocField().setColumns(22);
        listener.getLocField().addKeyListener(new EnterListener(this));
        listener.getAgeSpinner().setPreferredSize(new Dimension(52,20));
        ButtonPanel=new JPanel();
        ButtonPanel.setOpaque(false);
        ButtonPanel.add(listener.getNameField());
        ButtonPanel.add(listener.getAgeSpinner());
        ButtonPanel.add(listener.getLocField());
        ButtonPanel.add(this);
    }

    LabButton( TreeSet<Human> col,LabTable table,JTextField pathField, JProgressBar jPBar){
        super("Import");
        setPreferredSize(new Dimension(125,20));
        addActionListener(new ImportListener(pathField,col,table,jPBar));
        pathField.setColumns(50);
        pathField.addKeyListener(new EnterListener(this));
        ButtonPanel=new JPanel();
        ButtonPanel.setOpaque(false);
        ButtonPanel.add(pathField);
        ButtonPanel.add(this);
    }

    public JPanel getButtonPanel() {
        return ButtonPanel;
    }


}
