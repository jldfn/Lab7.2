import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TreeSet;

import static java.lang.Thread.sleep;

/**
 * Created by Алексей on 27.04.2017.
 */
public class AddingListener implements ActionListener {
    private TreeSet<Human> collection;
    private LabTable colTable;
    private JSpinner JSP;
    private JProgressBar progress;

    AddingListener(JSpinner spinner, TreeSet<Human> col,LabTable table,JProgressBar jpb){
        super();
        collection=col;
        this.colTable=table;
        JSP=spinner;
        progress = jpb;
    }
    public TreeSet<Human> getCollection() {
        return collection;
    }

    public LabTable getColTable() {
        return colTable;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ProgressBarThread jPBarThread = new ProgressBarThread(progress);
        jPBarThread.start();
        int i= (int) JSP.getValue();
        while (i>0) {
            int total = i;
            float percent1 = (total-i)*100/total;
            int percent = (int) percent1;
            int random_number1 = 0 + (int) (Math.random() * 50);
            Names name = Names.fromId(random_number1);
            String humanName = name.toString();
            int random_number2 = 0 + (int) (Math.random() * 100);
            int ageOfHuman = random_number2;
            int random_number3 = 0 + (int) (Math.random() * 50);
            Cities city = Cities.fromId(random_number3);
            String location = city.toString();
            Human RandomMan = new Human(humanName, ageOfHuman, location);
            collection.add(RandomMan);
            getColTable().fireTableDataChanged();
            i--;
        }
        try {
            Thread.sleep(1);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        jPBarThread.interrupt();
    }
}
