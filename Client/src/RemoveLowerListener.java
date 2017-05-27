import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * Created by Денис on 25.04.2017.
 */
public class RemoveLowerListener extends LabListener {
    JProgressBar jpb1;

    RemoveLowerListener(JTextField nameField, JSpinner ageSpinner, JTextField locField, TreeSet<Human> col, LabTable colTable, JProgressBar jpb) {
        super(nameField, ageSpinner, locField, col, colTable, jpb);
        jpb1 = jpb;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ((int) getAgeSpinner().getValue() >= 0 && (int) getAgeSpinner().getValue() <= 120) {
            if (Pattern.compile("[A-zА-я']+").matcher(getNameField().getText()).matches()) {
                if (Pattern.compile("[A-zА-я0-9\\-_]+").matcher(getLocField().getText()).matches()) {
                    Human consoleArgument = new Human(getNameField().getText(), (int) getAgeSpinner().getValue(), getLocField().getText());
                    ProgressBarThread jPBarThread = new ProgressBarThread(jpb1);
                    jPBarThread.start();
                    Iterator iter = getCollection().iterator();
                    /*while (iter.hasNext()) {
                        Human a = (Human) iter.next();
                        if (consoleArgument.compareTo(a) > 0) {
                            System.out.print(a.toString() + " был удалён из коллекции");
                            iter.remove();
                            getTable().fireTableDataChanged();
                        }
                    }*/
                    getCollection().clear();
                    getCollection().addAll(makeCall("remove_lower",consoleArgument).getUselessData());
                    getNameField().setText("");
                    getTable().fireTableDataChanged();
                    getAgeSpinner().setValue(0);
                    getLocField().setText("");
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    jPBarThread.interrupt();
                } else {
                    System.out.print("Поле \"Локация\" не может являться пустым. В локации могут содержаться лишь символы кириллицы, латинского алфавита, цифры, \"-\" и \"_\"");
                }
            } else {
                System.out.print("Поле \"Имя\" не может являться пустым.В имени могут содержаться только символы кириллицы и латинского алфавита");
            }
        } else {
            System.out.print("Возраст может быть только в пределах от 0 до 120 лет");
        }
    }
}
