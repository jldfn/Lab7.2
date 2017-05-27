import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.TreeSet;

/**
 * Created by Денис on 25.04.2017.
 */
public class SaveListener implements ActionListener{
    LabCollection collection;
    JTextPane OutputPanel;

    SaveListener(LabCollection collection){
        super();
        this.collection=collection;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ConsoleApp.SaveCollection(collection);
            }
        }).start();
    }

    public LabCollection getCollection() {
        return collection;
    }

    public void setCollection(LabCollection collection) {
        this.collection = collection;
    }

    public JTextPane getOutputPanel() {
        return OutputPanel;
    }

    public void setOutputPanel(JTextPane outputPanel) {
        OutputPanel = outputPanel;
    }
}
