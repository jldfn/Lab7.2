import javax.swing.*;

/**
 * Created by Алексей on 01.05.2017.
 */
public class TaskControl {
    public TaskControl(Task T){
        Thread t1 = new Thread(T);
        t1.start();
    }
}
