package classes;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.*;
import java.math.BigDecimal;
import java.util.Scanner;
import java.util.StringTokenizer;

@XmlRootElement(name = "item")
@XmlType(propOrder = { "name", "price"})
public class Item implements Serializable, Comparable<Item> {
    private String name; //наименование товара
    private Double price; //цена за шт
    private int amount;  //количество
    private Double cost; //стоимость

    public Item(){}

    public Item(String name, Double price, int amount) throws IOException {  //check correct
        this.name = name;
        //округление возможно до 2 цифр копейки
        if (Math.abs(price) < Double.MIN_VALUE) {

            throw new IllegalArgumentException("Price must be positive.");
        } else if (BigDecimal.valueOf(price).scale() > 2) {
            this.price = roundAvoid(price, 2);
        } else {
            this.price = price;
        }
        this.amount = amount;
        this.cost = roundAvoid(price*amount, 2);
    }

    private static double roundAvoid(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

    public int getAmount() {
        return amount;
    }

    public Double getCost() {
        return cost;
    }

    @XmlElement(name="name")
    public void setName(String Name) {
        this.name = Name;
    }
    @XmlElement(name = "price")
    public void setPrice(Double price) {
        if (Math.abs(price) < Double.MIN_VALUE) {
            throw new IllegalArgumentException("Price must be positive.");
        } else if (BigDecimal.valueOf(price).scale() > 2) {
            this.price = roundAvoid(price, 2);
        } else {
            this.price = price;
        }
    }
    @XmlTransient
    public void setAmount(int amount) {
        this.amount = amount;
    }
    @XmlTransient
    public void setCost(Double summ) {
        this.cost = summ;
    }

    @Override
    public String toString() {
        return "name=" + this.name + ", price=" + this.price + ", amount=" + this.amount
                + ", cost=" + this.cost;
    }

    // prepare for Serialization
    public static void writeItem(Object out, String s){
        try {
            FileOutputStream fos = new FileOutputStream(s); //an output stream for writing data to a File
            ObjectOutputStream oos = new ObjectOutputStream(fos); //writes primitive data types and graphs of Java objects to an OutputStream
            oos.writeObject(out);
            oos.close();
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    public static Object readItem(String s) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(s);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Object in;
        in = ois.readObject();
        ois.close();
        return in;
    }

    // reading and writing for user
    public static Item readFromFile(Scanner  scanner ) throws IOException {
        String itemString = scanner.nextLine();
        if (itemString == null) {
            throw new RuntimeException("Null string");
        }

        StringTokenizer st = new StringTokenizer(itemString, "=, ");
        st.nextToken(); // take off name
        String name = st.nextToken();
        st.nextToken(); // take off price
        double price = Double.parseDouble(st.nextToken());
        st.nextToken(); // take off amount
        int amount = Integer.parseInt(st.nextToken());
        st.nextToken();
        st.nextToken(); // take off cost

        return new Item(name,price, amount);
    }

    public void writeToFile(String s) throws IOException {
        FileWriter fout = new FileWriter(s);
        fout.write(this.toString());
        fout.close();
    }

    @Override
    public int compareTo(Item o) {
        return this.getCost().compareTo(o.getCost());
    }
}

