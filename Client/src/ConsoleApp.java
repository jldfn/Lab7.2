/**
 * Created by Денис on 02.03.2017.
 */

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConsoleApp {
    volatile  static boolean timedOut=false;
    final static String defaultPath = "src/Input_Output_Files/input.txt";
    static final String HOSTNAME = "52.174.250.216";
    static final String USERNAME = "kjkszpj361";
    static final String PASSWORD = "B9zbYEl*dj}6";
    static TimeoutThread timeOut;
    static public int port=8880;
    public static void main(String[] args) {
        timeOut=new TimeoutThread(Thread.currentThread());
        timeOut.start();
        LabCollection ExpCol = tryToConnect();
        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            gui(ExpCol);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Runtime.getRuntime().addShutdownHook(new Thread() {
                                                 public void run() {
                                                     if(timedOut) tryToConnect();
                                                     makeCall("disconnect",new Human("asldn",12,"msklad"));
                                                     SaveCollection(ExpCol);
                                                 }
                                             }
        );
    }

    private static void gui(LabCollection labCollection) throws InterruptedException {
        LabFrame guiFrame = new LabFrame("Lab6");
        //  <Background setting>
        JLabel backLabel=new JLabel();
        try {
            backLabel=new JLabel(new ImageIcon(ImageIO.read(new File("src/Backgrounds/animebg.jpg"))));
            guiFrame.setContentPane(backLabel);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //  </Background setting>

        //  <Frame Setting>
        guiFrame.setSize(720, 830);
        guiFrame.setResizable(false);
        guiFrame.setLayout(new FlowLayout());
        guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //  </Frame Setting>

        //  <Table setting>
        guiFrame.setTable(new LabTable(labCollection.getUselessData()));
        //  <Table sorter setting>
        TableRowSorter<LabTable> sort = new TableRowSorter<LabTable>(guiFrame.getTable());
        JTable sortTable = new JTable(guiFrame.getTable());
        DefaultTableCellRenderer first = new DefaultTableCellRenderer();
        first.setHorizontalAlignment(JLabel.CENTER);
        sortTable.getColumnModel().getColumn(0).setCellRenderer(first);
        sortTable.getColumnModel().getColumn(1).setCellRenderer(first);
        sortTable.getColumnModel().getColumn(2).setCellRenderer(first);
        sortTable.getColumnModel().getColumn(3).setCellRenderer(first);
        sortTable.setRowSorter(sort);
        // </Table sorter setting>

        // <Table filter setting>
        JTextField filterText = new JTextField(30);
        JButton filterButton = new JButton("Filter");
        filterText.addKeyListener(new EnterListener(filterButton));
        filterButton.setPreferredSize(new Dimension(125,20));
        JPanel filterPanel = new JPanel();
        filterPanel.setOpaque(false);
        filterPanel.add(filterButton);
        filterPanel.add(filterText);
        filterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (filterText.getText().length() == 0) {
                    sort.setRowFilter(null);
                } else {
                    sort.setRowFilter(RowFilter.regexFilter(filterText.getText()));
                }
            }
        });
        // <Table filter setting>

        //  </Table setting>

        //  <Output panel Setting>
        JTextPane OutputPanel = new JTextPane();
        OutputPanel.setPreferredSize(new Dimension(690, 135));
        OutputPanel.setText("Здравствуйте" + System.getProperty("line.separator") + "Здесь будут выводиться все сообщения системы");
        OutputPanel.setEditable(false);
        TextPaneOutputStream outputStream=new TextPaneOutputStream(OutputPanel);
        System.setOut(new PrintStream(outputStream));
        //  </Output panel setting>

        //  <ProgressBar setting>
        JProgressBar jpb = new JProgressBar();
        jpb.setPreferredSize(new Dimension(690, 35));
        jpb.setIndeterminate(false);
        //  </ProgressBar setting>

        //  <"Add" button setting>
        JSpinner spin = new JSpinner();
        AddingButton addHuman = new AddingButton(spin, labCollection.getUselessData(), guiFrame.getTable(), jpb);
        JPanel spinPanel = new JPanel();
        spinPanel.setOpaque(false);
        spinPanel.add(addHuman);
        spinPanel.add(spin);
        spin.setValue(0);
        spin.setPreferredSize(new Dimension(45, 25));
        addHuman.setText("Нажать, чтобы добавить людей в данном количестве");
        //  </"Add" button setting>

        //  <"SingleAdd" button setting>
        SingleAddingListener addingListener=new SingleAddingListener(new JTextField(),new JSpinner(),new JTextField(),labCollection.getUselessData(),guiFrame.getTable(),jpb);
        LabButton addingButton = new LabButton("Add",addingListener);
        //  </"SingleAdd" button setting>

        //  <"Remove" button setting>
        RemoveListener rmListener=new RemoveListener(new JTextField(),new JSpinner(),new JTextField(),labCollection.getUselessData(),guiFrame.getTable(),jpb,backLabel);
        LabButton rmButton = new LabButton("Remove",rmListener);
        //  </"Remove" button setting>

        //  <"Remove lower" button setting>
        RemoveLowerListener rmlListener=new RemoveLowerListener(new JTextField(),new JSpinner(),new JTextField(),labCollection.getUselessData(),guiFrame.getTable(),jpb);
        LabButton rmLButton = new LabButton("Remove lower", rmlListener);
        //  </"Remove lower" button setting>

        //  <"Import" button setting>
        LabButton ImportButton = new LabButton(labCollection.getUselessData(), guiFrame.getTable(),new JTextField(),jpb);
        //  </"Import" button setting>

        //  <"Save" button setting>
        JButton saveButton = new JButton("Save");
        saveButton.setPreferredSize(new Dimension(125,20));
        saveButton.addActionListener(new SaveListener(labCollection));
        //  </"Save" button setting>

        //  <Adding elements to frame>
        guiFrame.add(rmLButton.getButtonPanel());
        guiFrame.add(rmButton.getButtonPanel());
        guiFrame.add(addingButton.getButtonPanel());
        guiFrame.add(ImportButton.getButtonPanel());
        guiFrame.add(saveButton);
        guiFrame.add(filterPanel);
        guiFrame.add(new JScrollPane(sortTable));
        //guiFrame.add(spinPanel);
        guiFrame.add(new JScrollPane(OutputPanel));
        guiFrame.add(jpb);
        //  </Adding elements to frame>
        guiFrame.setVisible(true);
    }

    public static LabCollection ImportFrom(String path) {
        LabCollection a = new LabCollection();
        try {
            File input = new File(path);
            Scanner startReader = new Scanner(input);
            String currentString = startReader.nextLine();
            a = new LabCollection(currentString.split(" ")[1].substring(6, currentString.split(" ")[1].length() - 2));
            currentString = startReader.nextLine();
            while (!currentString.contains("</Collection>")) {
                String[] sepString = currentString.split(" ");
                if (sepString[0].matches("\t<Human")) {
                    String name = null;
                    boolean[] hasAttributes = new boolean[3];
                    int age = -1;
                    String location = null;
                    Matcher forHuman = Pattern.compile("[A-z]+=\"[A-z,А-я,0-9]+\"").matcher(currentString);
                    while (forHuman.find()) {
                        String[] buffString = forHuman.group().split("=");
                        switch (buffString[0]) {
                            case "name":
                                Matcher hName = Pattern.compile("[0-9]").matcher(buffString[1]);
                                if (hName.find()) {
                                    System.out.print("Поле name может содержать исключительно символы латинского алфавита и кириллицы");
                                } else {
                                    if (name != null)
                                        System.out.print("Атрибут name введён повторно, будет использовано следующее значение: " + buffString[1]);
                                    name = buffString[1].substring(1, buffString[1].length() - 1);
                                    hasAttributes[0] = true;
                                }
                                break;
                            case "age":
                                Matcher hAge = Pattern.compile("\"[0-9]+\"").matcher(buffString[1]);
                                if (hAge.matches()) {
                                    if (age != -1)
                                        System.out.print("Атрибут age введён несколько раз, будет использовано последнее значение: " + buffString[1]);
                                    age = Integer.parseInt(buffString[1].substring(1, buffString[1].length() - 1));
                                    hasAttributes[1] = true;
                                } else {
                                    System.out.print("В возрасте могут присутствовать только цифры!");
                                }
                                break;
                            case "loc":
                                if (location != null)
                                    System.out.print("Атрибут location введён несколько раз, будет использовано корректное последнее значение" + buffString[1]);
                                location = buffString[1].substring(1, buffString[1].length() - 1);
                                hasAttributes[2] = true;
                                break;
                            default:
                                System.out.print("В классе Human нет поля " + buffString[0]);
                        }
                    }
                    if (hasAttributes[0] && hasAttributes[1] && hasAttributes[2]) {
                        Human newHuman=new Human(name, age, location);
                        System.out.print("Объект "+newHuman.toString()+" успешно был добавлен в коллекцию");
                        a.getUselessData().add(newHuman);
                    } else {
                        if (!hasAttributes[0]) System.out.print("Атрибут name не был задан");
                        if (!hasAttributes[1]) System.out.print("Атрибут age не был задан");
                        if (!hasAttributes[2]) System.out.print("Атрибут location не был задан");
                    }
                } else {
                    System.out.print("Объекты данного типа не могут быть занесены в коллекцию");
                }
                currentString = startReader.nextLine();
            }
        } catch (FileNotFoundException e) {
            System.out.print("Файл не найден");
        }
        return a;
    }


    public static void SaveCollection(LabCollection collection) {
        try {
            FileWriter toEmptyFile = new FileWriter(defaultPath);
            BufferedWriter headerWriter = new BufferedWriter(toEmptyFile);
            headerWriter.write("<Collection name=\"" + collection.getName() + "\">");
            headerWriter.close();
            FileWriter fileWriter = new FileWriter(defaultPath, true);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            writer.newLine();
            for (Comparable elem : collection.getUselessData()) {
                String[] a = elem.toString().split(" ");
                switch (elem.toString().charAt(0)) {
                    case 'Ч': {
                        writer.write("\t<Human name=\"" + a[3] + "\" age=\"" + a[5] + "\" loc=\"" + a[9] + "\"/>");
                        writer.newLine();
                        break;
                    }
                }
                writer.flush();
            }
            writer.write("</Collection>");
            writer.close();
            System.out.print("Коллекция успешно сохранена в файл "+defaultPath);
        } catch (Exception f) {
            System.out.print(f.getMessage());
        }
    }

    public static LabCollection tryToConnect() {
        int i=0;
        try {
            for (i = 8880; i <= 8890; i++) {
                System.out.println("Trying to connect to port " + i);
                DatagramSocket clientSocket = new DatagramSocket();
                InetSocketAddress address = new InetSocketAddress(HOSTNAME, i);
                DatagramPacket testPacket = new DatagramPacket("test".getBytes(), "test".getBytes().length, address);
                clientSocket.setSoTimeout(1000);
                byte[] receiveData = new byte[1024];
                DatagramPacket testReceivePacket = new DatagramPacket(receiveData, receiveData.length);
                try {
                    clientSocket.send(testPacket);
                    clientSocket.receive(testReceivePacket);
                    System.out.println(new String(testReceivePacket.getData()));
                    port=i;
                    break;
                }catch (SocketTimeoutException ex){System.out.println("Port "+i+" is unavailable");continue;}
            }
        } catch (Exception e) {
            System.out.println("asda");
            e.printStackTrace();
        }
        if(i==8891){System.out.print("Нет соединения с сервером или заняты все порты"); return null;}
        else return LabListener.makeCall("collection",new Human());
    }

    public static void makeCall(String command,Human object) {
        try {
            SocketAddress address = new InetSocketAddress(ConsoleApp.HOSTNAME, ConsoleApp.port);
            DatagramSocket clientSocket = new DatagramSocket();
            byte[] sendData;
            byte[] receiveData = new byte[1024];
            String sentence = command;
            sendData = sentence.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address);
            clientSocket.send(sendPacket);
            clientSocket.send(new DatagramPacket(object.serialize(), object.serialize().length, address));
        } catch (IOException e) {
        }
    }
}

