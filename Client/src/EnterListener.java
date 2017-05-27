import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created by Денис on 09.05.2017.
 */
public class EnterListener implements KeyListener{

    JButton triggerButton;
    boolean flag=false;

    EnterListener(JButton button){
        triggerButton=button;
    }

    @Override
    public void keyPressed(KeyEvent e) {
       if(e.getKeyCode()==KeyEvent.VK_ENTER){
           triggerButton.doClick();
           flag=true;
       }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }
}
