import javax.xml.soap.SAAJMetaFactory;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.DatagramChannel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.TreeSet;
import javax.sql.rowset.CachedRowSet;
import org.postgresql.ds.PGSimpleDataSource;
import org.postgresql.ds.PGSimpleDataSource.*;
import com.sun.rowset.CachedRowSetImpl;
/**
 * Created by Денис on 21.05.2017.
 */
public class Server {
    private static final int SSH_PORT = 22;
    private static final String HOSTNAME = "52.174.16.235";
    private static final String USERNAME = "kjkszpj361";
    private static final String PASSWORD = "B9zbYEl*dj}6";

    public static void main(String args[]) throws Exception {
        for (int i = 8880; i <= 8890; i++) {
            monitorPort(i);
        }
    }

    public static void monitorPort(int port) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PGSimpleDataSource source1 = new PGSimpleDataSource();
                    source1.setDatabaseName("Collection");
                    source1.setPortNumber(5432);
                    source1.setServerName("localhost");
                    source1.setUser("kjkszpj361");
                    source1.setPassword("jmd821");
                                        DatagramChannel serverChannel = DatagramChannel.open();
                    final SocketAddress clientAddress;
                    serverChannel.bind(new InetSocketAddress(port));
                    byte[] receiveData = new byte[1024];
                    ByteBuffer receiveBuffer = ByteBuffer.wrap(receiveData);
                    receiveBuffer.clear();
                    ByteBuffer sendBuffer = ByteBuffer.wrap(("Connected to port " + port).getBytes());
                    sendBuffer.clear();
                    clientAddress = serverChannel.receive(receiveBuffer);
                    serverChannel.send(sendBuffer, clientAddress);
                    Connection connection1=null;
                    try{
                        connection1 = source1.getConnection();
                        System.out.println("NORM");
                    } catch (SQLException e){System.out.println("BAGA");}
                    try{PreparedStatement st1 = connection1.prepareStatement("select * from Humans;");
                        System.out.println(st1+" remove");
                        ResultSet rs = st1.executeQuery();
                        CachedRowSet cs = new CachedRowSetImpl();
                        cs.populate(rs);
                        TreeSet<Human> col = new TreeSet<>();
                        while (cs.next()){
                            Human random = new Human();
                            random.setName(cs.getString("name"));
                            random.setLocation(cs.getString("location"));
                            random.setAge(cs.getInt("age"));
                            col.add(random);
                        }
                        LabCollection kkk = new LabCollection();
                        kkk.setUselessData(col);
                        serverChannel.send(ByteBuffer.wrap(kkk.serialize()),clientAddress);
                    }
                    catch(SQLException ee){}

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            servePort(serverChannel, clientAddress, port);
                        }
                    }).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void servePort(DatagramChannel serverChannel1, SocketAddress clientAddress1, int port) {
        Thread serveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                PGSimpleDataSource source = new PGSimpleDataSource();
                source.setDatabaseName("Collection");
                source.setPortNumber(5432);
                source.setServerName("localhost");
                source.setUser("kjkszpj361");
                source.setPassword("jmd821");
                Connection connection = null;
                LabCollection returnCollection=new LabCollection();
                try{
                    connection = source.getConnection();
                    System.out.println("NORM");
                } catch (SQLException e){System.out.println("BAGA");}
                DatagramChannel serverChannel = serverChannel1;
                TimeoutThread receiveTimeout = new TimeoutThread(Thread.currentThread(), 120000);
                SocketAddress clientAddress;
                receiveTimeout.interrupt();
                try {
                    receiveTimeout.start();
                    while (receiveTimeout.getState() != Thread.State.WAITING) {
                        byte[] receiveData = new byte[1024];
                        ByteBuffer receiveBuffer = ByteBuffer.wrap(receiveData);
                        receiveBuffer.clear();
                        byte[] sendData = new byte[1024];
                        ByteBuffer sendBuffer = ByteBuffer.wrap(sendData);
                        sendBuffer.clear();
                        clientAddress = serverChannel.receive(receiveBuffer);
                        if (!getHostname(clientAddress1).contains(getHostname(clientAddress))) {
                            continue;
                        }
                        receiveTimeout.sleepTime = 120000;
                        receiveTimeout.interrupt();
                        receiveBuffer.flip();
                        byte[] bytes = new byte[receiveBuffer.remaining()];
                        receiveBuffer.get(bytes);
                        String sentence = new String(bytes);
                        receiveBuffer.clear();
                        serverChannel.receive(receiveBuffer);
                        receiveBuffer.flip();
                        byte[] humanBytes = new byte[receiveBuffer.remaining()];
                        receiveBuffer.get(humanBytes);
                        Human receivedHuman = Human.deserialize(humanBytes);
                        System.out.println(receivedHuman.toString());
                        switch (sentence){
                            case "disconnect":{throw(new ClosedByInterruptException());}
                            case "collection":{
                                try {
                                    PreparedStatement st1 = connection.prepareStatement("select * from Humans;");
                                System.out.println(st1+" remove");
                                ResultSet rs = st1.executeQuery();
                                CachedRowSet cs = new CachedRowSetImpl();
                                cs.populate(rs);
                                TreeSet<Human> col = new TreeSet<>();
                                while (cs.next()){
                                    Human random = new Human();
                                    random.setName(cs.getString("name"));
                                    random.setLocation(cs.getString("location"));
                                    random.setAge(cs.getInt("age"));
                                    col.add(random);}
                                    returnCollection.setUselessData(col);
                                }catch (SQLException e){e.printStackTrace();}
                            }break;
                            case "remove":{
                                try {
                                    PreparedStatement st = connection.prepareStatement("delete from Humans where (name = ?) and (age = ?) and (location = ?);");
                                    st.setString(1, receivedHuman.getName());
                                    st.setInt(2, receivedHuman.getAge());
                                    st.setString(3, receivedHuman.getLocation());
                                    System.out.println(st);
                                    st.execute();
                                    PreparedStatement st1 = connection.prepareStatement("select * from Humans;");
                                    System.out.println(st1+" remove");
                                    ResultSet rs = st1.executeQuery();
                                    CachedRowSet cs = new CachedRowSetImpl();
                                    cs.populate(rs);
                                    TreeSet<Human> col = new TreeSet<>();
                                    while (cs.next()){
                                        Human random = new Human();
                                        random.setName(cs.getString("name"));
                                        random.setLocation(cs.getString("location"));
                                        random.setAge(cs.getInt("age"));
                                        col.add(random);
                                    }
                                    returnCollection.setUselessData(col);
                                }catch (SQLException e){}
                                break;
                            }
                            case "remove_lower":{
                                try {
                                    PreparedStatement st1 = connection.prepareStatement("select * from Humans;");
                                    System.out.println(st1+" remove lower");
                                    ResultSet rs = st1.executeQuery();
                                    CachedRowSet cs = new CachedRowSetImpl();
                                    cs.populate(rs);
                                    TreeSet<Human> col = new TreeSet<>();
                                    while (cs.next()){
                                        Human random = new Human();
                                        random.setName(cs.getString("name"));
                                        random.setLocation(cs.getString("location"));
                                        random.setAge(cs.getInt("age"));
                                        col.add(random);
                                    }
                                    Iterator<Human> iterator = col.iterator();
                                    while (iterator.hasNext()){
                                        Human A = iterator.next();
                                        if (A.compareTo(receivedHuman)<0){
                                        iterator.remove();
                                        PreparedStatement st = connection.prepareStatement("delete from Humans where (name = ?) and (age = ?) and (location = ?);");
                                        System.out.println(st);
                                        st.setString(1,A.getName());
                                        st.setInt(2, A.getAge());
                                        st.setString(3, A.getLocation());
                                        st.execute();}
                                    }
                                    returnCollection.setUselessData(col);
                                }catch (SQLException e){}
                                break;
                            }
                            case "update":{
                                try{
                                    boolean received=false;
                                    receiveBuffer.clear();
                                    while(!received) {
                                        clientAddress = serverChannel.receive(receiveBuffer);
                                        if (!getHostname(clientAddress1).contains(getHostname(clientAddress))) {
                                            receiveBuffer.clear();
                                            continue;
                                        }
                                        received=true;
                                    }
                                    receiveTimeout.sleepTime=120000;
                                    receiveTimeout.interrupt();
                                    receiveBuffer.flip();
                                    int attributeNumber=receiveBuffer.getInt();
                                    System.out.println(attributeNumber);
                                    receiveBuffer.clear();
                                    received=false;
                                    while(!received) {
                                        clientAddress = serverChannel.receive(receiveBuffer);
                                        if(!getHostname(clientAddress1).contains(getHostname(clientAddress))){
                                            receiveBuffer.clear();
                                            continue;
                                        }
                                        received=true;
                                    }
                                    receiveTimeout.sleepTime=120000;
                                    receiveTimeout.interrupt();
                                    receiveBuffer.flip();
                                    byte[] serNewValue=new byte[receiveBuffer.remaining()];
                                    receiveBuffer.get(serNewValue);
                                    String newValue=new String(serNewValue);
                                    System.out.println(newValue);
                                    PreparedStatement st;
                                    switch (attributeNumber){
                                        case 1:{st=connection.prepareStatement("update Humans set name=? where (name=?) and (age=?) and (location=?);");}break;
                                        case 2:{st=connection.prepareStatement("update Humans set age=? where (name=?) and (age=?) and (location=?);");}break;
                                        case 3:{st=connection.prepareStatement("update Humans set location=? where (name=?) and (age=?) and (location=?);");}break;
                                        default:{st=connection.prepareStatement("update Humans set ?=? where (name=?) and (age=?) and (location=?);");}
                                    }
                                    System.out.print(st);
                                    System.out.println(receivedHuman);
                                    if (attributeNumber==2){ st.setInt(1, Integer.parseInt(newValue));}
                                    else{st.setString(1,newValue);}
                                    st.setString(2, receivedHuman.getName());
                                    st.setInt(3, receivedHuman.getAge());
                                    st.setString(4, receivedHuman.getLocation());
                                    st.execute();
                                    PreparedStatement st1 = connection.prepareStatement("select * from Humans;");
                                    System.out.println(st+" update");
                                    ResultSet rs = st1.executeQuery();
                                    CachedRowSet cs = new CachedRowSetImpl();
                                    cs.populate(rs);
                                    TreeSet<Human> col = new TreeSet<>();
                                    while (cs.next()){
                                        Human random = new Human();
                                        random.setName(cs.getString("name"));
                                        random.setLocation(cs.getString("location"));
                                        random.setAge(cs.getInt("age"));
                                        col.add(random);
                                        returnCollection.setUselessData(col);
                                    }
                                }catch (SQLException e) {System.out.println("snafibu");}
                                break;
                            }
                            case "add":{
                                try {
                                    PreparedStatement st = connection.prepareStatement("insert into Humans (name,age,location) values(?,?,?)");
                                    st.setString(1, receivedHuman.getName());
                                    st.setInt(2, receivedHuman.getAge());
                                    st.setString(3, receivedHuman.getLocation());
                                    System.out.println(st);
                                    st.executeUpdate();
                                    PreparedStatement st1 = connection.prepareStatement("select * from Humans;");
                                    System.out.println(st1+" add");
                                    ResultSet rs = st1.executeQuery();
                                    CachedRowSet cs = new CachedRowSetImpl();
                                    cs.populate(rs);
                                    TreeSet<Human> col = new TreeSet<>();
                                    while (cs.next()){
                                        Human random = new Human();
                                        random.setName(cs.getString("name"));
                                        random.setLocation(cs.getString("location"));
                                        random.setAge(cs.getInt("age"));
                                        col.add(random);
                                    }
                                    returnCollection.setUselessData(col);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                break;
                            }
                            case "import":{}
                        }
                        System.out.println(returnCollection);
                        for(Human i:returnCollection.getUselessData()){
                            System.out.println(i.toString());
                        }
                        sendBuffer = ByteBuffer.wrap(returnCollection.serialize());
                        serverChannel.send(sendBuffer, clientAddress);
                        receivedHuman.setAge(receivedHuman.getAge() + 10);
                        sendBuffer = ByteBuffer.wrap(receivedHuman.serialize());
                        serverChannel.send(sendBuffer, clientAddress);
                        receiveBuffer.clear();
                    }
                } catch (ClosedByInterruptException e) {
                    try {
                        System.out.println("Port "+port+" is free now");
                        serverChannel.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    monitorPort(port);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        serveThread.start();
    }

    public static String getHostname(SocketAddress address) {
        return ((InetSocketAddress) address).getHostName();
    }
}