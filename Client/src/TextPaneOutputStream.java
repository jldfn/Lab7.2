import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Денис on 06.05.2017.
 */
public class TextPaneOutputStream extends OutputStream {

    final JTextPane ConsolePane;
    TextPaneOutputStream(JTextPane pane){
        ConsolePane=pane;
    }

    @Override
    public void write(int b) throws IOException
    {
        write (new byte [] {(byte)b}, 0, 1);
    }

    @Override
    public void write(@NotNull byte[] b, int off, int len) throws IOException {
        final String text = new String (b, off, len);
        SwingUtilities.invokeLater(new Runnable ()
        {
            @Override
            public void run()
            {
                ConsolePane.setText(ConsolePane.getText()+System.getProperty("line.separator")+text);
            }
        });
    }
}
