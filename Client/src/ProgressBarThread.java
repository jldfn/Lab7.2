import javax.swing.*;

/**
 * Created by Алексей on 08.05.2017.
 */
public class ProgressBarThread extends Thread {
    private JProgressBar jProgressBar = new JProgressBar();
    ProgressBarThread(JProgressBar jpb) {
        jProgressBar = jpb;
    }
    @Override
    public void run() {
       try{
           jProgressBar.setIndeterminate(true);
           sleep(100000);
       }catch (InterruptedException e1){
           try {
               sleep(100);
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
           jProgressBar.setIndeterminate(false);
       }
    }
}
