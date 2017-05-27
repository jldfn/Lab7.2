import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Денис on 25.04.2017.
 */
public class ImportListener extends LabListener {
    JProgressBar jpb1;
    ImportListener(JTextField field, TreeSet<Human> col, LabTable colTable, JProgressBar jpb){
        super(field,col,colTable,jpb);
        jpb1=jpb;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        ProgressBarThread jPBarThread = new ProgressBarThread(jpb1);
        jPBarThread.start();
        new Thread(new Runnable(){
            @Override
            public void run() {
                Matcher m = Pattern.compile("\\{([^}]+)\\}").matcher(getNameField().getText());
                    while (m.find()) {
                        getCollection().clear();
                        getCollection().addAll(makeCall(m.group().substring(1, m.group().length() - 1)).getUselessData());
                        getTable().fireTableDataChanged();
                    }
                    getNameField().setText("");
            }
        }).start();
        try {
            Thread.sleep(1);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        jPBarThread.interrupt();
    }

    protected LabCollection makeCall(String filepath){
        LabCollection ImportCollection=ConsoleApp.ImportFrom(filepath);
        try {
            SocketAddress address = new InetSocketAddress(ConsoleApp.HOSTNAME, 8885);
            DatagramSocket clientSocket = new DatagramSocket();
            byte[] sendData;
            byte[] receiveData = new byte[1024];
            String sentence = "Import";
            sendData = sentence.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address);
            clientSocket.send(sendPacket);
            clientSocket.send(new DatagramPacket(ImportCollection.serialize(),ImportCollection.serialize().length,address));
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            LabCollection receivedCollection=LabCollection.deserialize(receivePacket.getData());
            clientSocket.close();
            return receivedCollection;
        }catch(Exception e){e.printStackTrace();}
        return null;
    }
}
