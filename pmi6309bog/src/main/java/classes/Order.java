package classes;

import javax.xml.bind.annotation.*;
import java.io.*;
import java.util.*;

@XmlRootElement(name = "order")
@XmlType(propOrder = { "order_id", "itemList"})
public class Order implements Serializable {
    private int order_id;  // unique identifier
    private ArrayList<Item> itemList;
    private static int obs = 1;

    public Order() {
        this.order_id = obs;
        obs++;
        this.itemList = new ArrayList<Item>();
    }

    @XmlElement(name = "order_id")
    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int identifier) {
        this.order_id = identifier;
    }

    public int getLength() {
        return itemList.size();
    }

    public Item getItemFromOrder(int index){
        return this.itemList.get(index);
    }

    public void addItem(Item newItem) {
        itemList.add(newItem);
    }

    public void deleteItem(int index) {
        itemList.remove(index);
    }

    public void sortAndDistinct(){
        Set<Item> set = new HashSet<Item>(this.itemList);
        this.itemList.clear();
        this.itemList.addAll(set);
        Collections.sort(this.itemList);
    }

    @Override
    public String toString() {
        String tmp = "order_id=" + this.order_id + "\n";
        for (int i = 0; i < this.getLength(); i++) {
            tmp += this.itemList.get(i).toString() + "\n";
        }
        return tmp;
    }

    // prepare for Serialization
    public boolean writeOrder(String s) {
        try {
            FileOutputStream fos = new FileOutputStream(s); //an output stream for writing data to a File
            ObjectOutputStream oos = new ObjectOutputStream(fos); //writes primitive data types and graphs of Java objects to an OutputStream
            oos.writeObject(itemList);
            oos.close();
            return true;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public void readOrder(String s) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(s);
        ObjectInputStream ois = new ObjectInputStream(fis);
        itemList = (ArrayList<Item>) ois.readObject();
    }

    // reading and writing for user
    public void writeToFile(String s) throws IOException {
        FileWriter fout = new FileWriter(s);
        fout.write(this.toString());
        fout.close();
    }

    public void readFromFile(String s) throws IOException {
        FileInputStream fis = new FileInputStream(s);
        Scanner scanner = new Scanner(fis);
        ArrayList<Item> items = new ArrayList<>();
        String idString = scanner.nextLine();
        StringTokenizer st = new StringTokenizer(idString, "=");
        st.nextToken(); // take off order_id
        this.setOrder_id(Integer.parseInt(st.nextToken()));

        FileReader fin = new FileReader(s);
        LineNumberReader lineNumberReader = new LineNumberReader(fin);
        int lineNumber = 0;
        while (lineNumberReader.readLine() != null) {
            lineNumber++;
        }
        lineNumberReader.close();
        for(int i=1; i< lineNumber; i++){
            items.add(Item.readFromFile(scanner));
        }

        fis.close();
        this.itemList = items;
    }

    public ArrayList<Item> getItemList() {
        return this.itemList;
    }

    @XmlElementWrapper(name = "itemList")
    @XmlElement(name = "item")
    public void setItemList(ArrayList<Item> itemList) {
        this.itemList = itemList;
    }
}
