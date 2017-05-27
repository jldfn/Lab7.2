
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.util.TreeSet;

/**
 * Created by Денис on 12.03.2017.
 */
public class LabCollection implements Serializable{
    private String name;

    public TreeSet<Human> getUselessData() {
        return UselessData;
    }

    public void setUselessData(TreeSet<Human> uselessData) {
        UselessData = uselessData;
    }
    public void addUselessData(TreeSet<Human> uselessData){UselessData.addAll(uselessData);}
    private TreeSet<Human> UselessData;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    LabCollection(){
        this.name="";
        UselessData=new TreeSet<Human>();
    }
    LabCollection(String name){
        this.name=name;
        UselessData=new TreeSet<Human>();
    }

    public byte[] serialize() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(this);
            out.flush();
            byte[] serializedCollection = bos.toByteArray();
            bos.close();
            return serializedCollection;
        } catch (IOException ex) {
            // ignore close exception
        }finally {
            try{
                bos.close();}catch (IOException e){}
        }
        return null;
    }

    public static LabCollection deserialize(byte[] serializedCollection){
        ByteArrayInputStream bis = new ByteArrayInputStream(serializedCollection);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            Object o = in.readObject();
            return (LabCollection) o;
        } catch(IOException e){e.printStackTrace();}catch (ClassNotFoundException as){as.getCause();as.getMessage();}
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return null;
    }
}
