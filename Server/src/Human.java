import java.io.*;

/**
 * Created by Денис on 19.11.2016.
 */
public class Human implements Comparable, Serializable {
    private String name;
    private int age;
    private String location;
    Human(){}
    Human(String n, int a){
        setAge(a);
        setName(n);
    }

    Human(String n, int a, String loc){
        setLocation(loc);
        setName(n);
        setAge(a);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getAge() {return age;}

    public void setAge(int age) {this.age = age;}

    public boolean equals(Object o) {
        Human k=(Human) o;
        return (getLocation().equals(k.getLocation()) && getName().equals(k.getName()) && getAge()==k.getAge());
    }

    public int hashCode() {
        int result = getName().hashCode();
        result= 31 * result + getLocation().hashCode()*19+getAge()*53;
        return result;
    }
    public String toString(){
        return ("Человек по имени "+name+" возрастом "+getAge()+" находящийся в локации: "+location);
    }

    public int compareTo(Object o) {
        int c1=0,c2=0;
        for(int i=0;i<o.toString().length();i++) c1+=o.toString().charAt(i);
        for(int i=0;i<this.toString().length();i++) c2+=this.toString().charAt(i);
        return(c2-c1);
    }

    public byte[] serialize() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(this);
            out.flush();
            byte[] serializedHuman = bos.toByteArray();
            bos.close();
            return serializedHuman;
        } catch (IOException ex) {
            // ignore close exception
        }finally {
            try{
            bos.close();}catch (IOException e){}
        }
        return null;
    }

    public static Human deserialize(byte[] serializedHuman){
        ByteArrayInputStream bis = new ByteArrayInputStream(serializedHuman);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            Object o = in.readObject();
            return (Human) o;
        } catch(Exception e){}
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

