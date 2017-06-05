import java.net.*;

/**
 * Created by Денис on 05.06.2017.
 */
public class ReceiveThread extends Thread {

    SocketAddress serverAddress;
    LabTable table;

    ReceiveThread(SocketAddress serverAddress, LabTable table){
        this.serverAddress=serverAddress;
        this.table=table;
    }

    @Override
    public void run() {
        try {
            DatagramSocket serverSocket = ConsoleApp.serverSocket;
            while (true) {
                byte[] receivedBytes = new byte[1024];
                DatagramPacket receivedPacket = new DatagramPacket(receivedBytes, receivedBytes.length, serverAddress);
                serverSocket.receive(receivedPacket);
                LabCollection collection=LabCollection.deserialize(receivedBytes);
                if(collection!=null){
                    table.getHumans().clear();
                    table.getHumans().addAll(collection.getUselessData());
                    table.fireTableDataChanged();
                }else{System.out.print(new String(receivedBytes));}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
