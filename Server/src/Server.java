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
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.sql.rowset.CachedRowSet;
import org.postgresql.ds.PGSimpleDataSource;
import org.postgresql.ds.PGSimpleDataSource.*;
import com.sun.rowset.CachedRowSetImpl;
/**
 * Created by Денис on 21.05.2017.
 */
public class Server {
    private static final int SSH_PORT = 22;
    private static boolean[] needsRefreshing=new boolean[11];
    private static ConcurrentLinkedQueue<Integer> portQueue=new ConcurrentLinkedQueue<Integer>();
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
                    System.out.println("Starting to monitor port "+port);
                    needsRefreshing[port-8880]=false;
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
                    System.out.println("Someone connected to port "+port+" from "+getHostname(clientAddress).toString());
                    serverChannel.send(sendBuffer, clientAddress);
                    Connection connection1=null;
                    try{
                        connection1 = source1.getConnection();
                    } catch (SQLException e){System.out.println("BAGA");}
                    try{PreparedStatement st1 = connection1.prepareStatement("select * from Humans;");
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
                        System.out.println("Starting to serve port "+ port);
                        System.out.println();
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
                SocketAddress clientAddress;
                try{
                    connection = source.getConnection();
                    System.out.println("Connected to database");
                } catch (SQLException e){System.out.println("Cannot connect to database");}
                DatagramChannel serverChannel = serverChannel1;
                TimeoutThread receiveTimeout = new TimeoutThread(Thread.currentThread(), 120000);
                receiveTimeout.interrupt();
                try {
                    receiveTimeout.start();
                    System.out.println("Timer for client on port "+port+" started");
                    while (receiveTimeout.getState() != Thread.State.WAITING) {
                        byte[] receiveData = new byte[1024];
                        ByteBuffer receiveBuffer = ByteBuffer.wrap(receiveData);
                        receiveBuffer.clear();
                        byte[] sendData = new byte[1024];
                        ByteBuffer sendBuffer = ByteBuffer.wrap(sendData);
                        sendBuffer.clear();
                        receiveFromAddress(serverChannel,clientAddress1,receiveBuffer);
                        receiveTimeout.sleepTime = 120000;
                        receiveTimeout.interrupt();
                        receiveBuffer.flip();
                        byte[] bytes = new byte[receiveBuffer.remaining()];
                        receiveBuffer.get(bytes);
                        String sentence = new String(bytes);
                        System.out.println("Client send command "+sentence+" on port "+port);
                        receiveBuffer.clear();
                        clientAddress=receiveFromAddress(serverChannel,clientAddress1,receiveBuffer);
                        portQueue.add(port);
                        while (true){
                            if (portQueue.peek()==port) break;
                            }
                        receiveBuffer.flip();
                        byte[] humanBytes = new byte[receiveBuffer.remaining()];
                        receiveBuffer.get(humanBytes);
                        Human receivedHuman = Human.deserialize(humanBytes);
                        System.out.println("Received Human from client on port"+port);
                        if (needsRefreshing[port-8880]){
                            System.out.println("But Client's data needs refreshing, so command will be ignored");
                            serverChannel.send(ByteBuffer.wrap("true".getBytes()),clientAddress);
                            if (sentence.contains("update")){
                                serverChannel.receive(receiveBuffer);
                                receiveBuffer.clear();
                                serverChannel.receive(receiveBuffer);
                                receiveBuffer.clear();
                            }
                            sentence="collection";
                        }else{serverChannel.send(ByteBuffer.wrap("false".getBytes()),clientAddress);}
                        switch (sentence){
                            case "disconnect":{System.out.println("Disconnecting port"+port);
                            throw(new ClosedByInterruptException());}
                            case "collection": {
                                System.out.println("Refreshing data on port "+ port);
                                needsRefreshing[port-8880]=false;
                                try {
                                    PreparedStatement st1 = connection.prepareStatement("select * from Humans;");
                                    ResultSet rs = st1.executeQuery();
                                    CachedRowSet cs = new CachedRowSetImpl();
                                    cs.populate(rs);
                                    TreeSet<Human> col = new TreeSet<>();
                                    while (cs.next()) {
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
                            }break;
                            case "remove":{
                                try {
                                    PreparedStatement st = connection.prepareStatement("delete from Humans where (name = ?) and (age = ?) and (location = ?);");
                                    st.setString(1, receivedHuman.getName());
                                    st.setInt(2, receivedHuman.getAge());
                                    st.setString(3, receivedHuman.getLocation());
                                    System.out.println("Removing object from collection... "+port);
                                    st.execute();
                                    PreparedStatement st1 = connection.prepareStatement("select * from Humans;");
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
                                    System.out.println("Removing objects lower than received object... "+port);
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
                                    byte[] updateBytes=new byte[1024];
                                    ByteBuffer updateBuffer=ByteBuffer.wrap(updateBytes);
                                    updateBuffer.clear();
                                    receiveFromAddress(serverChannel,clientAddress1,updateBuffer);
                                    receiveTimeout.sleepTime=120000;
                                    receiveTimeout.interrupt();
                                    updateBuffer.flip();
                                    int attributeNumber=updateBuffer.getInt();
                                    System.out.println("Received attribute number from client "+attributeNumber+" "+port);
                                    updateBuffer.clear();
                                    clientAddress=receiveFromAddress(serverChannel,clientAddress1,updateBuffer);
                                    receiveTimeout.sleepTime=120000;
                                    receiveTimeout.interrupt();
                                    updateBuffer.flip();
                                    byte[] serNewValue=new byte[updateBuffer.remaining()];
                                    updateBuffer.get(serNewValue);
                                    String newValue=new String(serNewValue);
                                    System.out.println("Received attribute value from client: "+newValue+" "+port);
                                    PreparedStatement st;
                                    switch (attributeNumber){
                                        case 1:{st=connection.prepareStatement("update Humans set name=? where (name=?) and (age=?) and (location=?);");}break;
                                        case 2:{st=connection.prepareStatement("update Humans set age=? where (name=?) and (age=?) and (location=?);");}break;
                                        case 3:{st=connection.prepareStatement("update Humans set location=? where (name=?) and (age=?) and (location=?);");}break;
                                        default:{st=connection.prepareStatement("update Humans set ?=? where (name=?) and (age=?) and (location=?);");}
                                    }
                                    if (attributeNumber==2){ st.setInt(1, Integer.parseInt(newValue));}
                                    else{st.setString(1,newValue);}
                                    st.setString(2, receivedHuman.getName());
                                    st.setInt(3, receivedHuman.getAge());
                                    st.setString(4, receivedHuman.getLocation());
                                    st.execute();
                                    PreparedStatement st1 = connection.prepareStatement("select * from Humans;");
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
                                }catch (SQLException e) {System.out.println("Something went wrong "+port);}
                                break;
                            }
                            case "add":{
                                try {
                                    PreparedStatement st = connection.prepareStatement("insert into Humans (name,age,location) values(?,?,?)");
                                    st.setString(1, receivedHuman.getName());
                                    st.setInt(2, receivedHuman.getAge());
                                    st.setString(3, receivedHuman.getLocation());
                                    System.out.println("Adding new object to database... "+port);
                                    st.executeUpdate();
                                    PreparedStatement st1 = connection.prepareStatement("select * from Humans;");
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
                        for (int i=0;i<=10;i++){
                            if(8880+i!=port){
                            needsRefreshing[i]=true;}
                        }
                        System.out.println("Sending collection to client on port "+port);
                        System.out.println();
                        System.out.println();
                        sendBuffer = ByteBuffer.wrap(returnCollection.serialize());
                        serverChannel.send(sendBuffer, clientAddress);
                        portQueue.poll();
                        receiveBuffer.clear();
                    }
                } catch (ClosedByInterruptException e) {
                    try{
                    if(portQueue.peek()==port){
                    portQueue.poll();}
                    }catch (NullPointerException np){}
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

    public static SocketAddress receiveFromAddress(DatagramChannel serverChannel,SocketAddress client,ByteBuffer data) throws IOException {
        boolean received=false;
        SocketAddress thisClient=client;
        while(!received) {
            thisClient = serverChannel.receive(data);
            if (!getHostname(client).contains(getHostname(thisClient))) {
                data.clear();
                continue;
            }
            received=true;
        }
        return thisClient;
    }

    public static String getHostname(SocketAddress address) {
        return ((InetSocketAddress) address).getHostName();
    }
}