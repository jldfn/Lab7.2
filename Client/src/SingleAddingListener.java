import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * Created by Денис on 03.05.2017.
 */
public class SingleAddingListener extends LabListener {

    SingleAddingListener(JTextField nameField1, JSpinner ageSpinner1, JTextField locField1, TreeSet<Human> col1, LabTable colTable1, JProgressBar jpb1) {
        super(nameField1, ageSpinner1, locField1, col1, colTable1, jpb1);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ((int) getAgeSpinner().getValue() >= 0 && (int) getAgeSpinner().getValue() <= 120) {
            if (Pattern.compile("[A-zА-я']+").matcher(getNameField().getText()).matches()) {
                if (Pattern.compile("[A-zА-я0-9\\-_]+").matcher(getLocField().getText()).matches()) {
                    Human Person = new Human(getNameField().getText(), (int) getAgeSpinner().getValue(), getLocField().getText());
                    /*
                    getCollection().add(Person);
                    getNameField().setText("");
                    getAgeSpinner().setValue(0);
                    getLocField().setText("");
                    getTable().fireTableDataChanged();
                    System.out.print("Объект " + Person.toString() + " был успешно занесен в коллекцию");*/
                    makeCall("add",Person);
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
