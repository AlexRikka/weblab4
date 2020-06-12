package classes;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;

@XmlRootElement
@XmlType(propOrder = { "customer_id", "customerName", "customerLogin", "orderList" })
public class Customer {
    private int customer_id;
    private String customerName;
    private String customerLogin;
    private String customerPassword;
    private ArrayList<Order> orderList;
    private static int obs = 1;

    public Customer(){
        this.customer_id = obs;
        obs++;
        this.customerName = "somename";
        this.customerLogin = "somelogin";
        this.customerPassword = "somepass";
        this.orderList = new ArrayList<>();
    }

    Customer(String name, String login, String pass, ArrayList<Order> orders) {
        this.customer_id = obs;
        ++obs;
        this.customerName = name;
        this.customerLogin = login;
        this.customerPassword = pass;
        this.orderList = orders;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerLogin() {
        return customerLogin;
    }

    public String getCustomerPassword() {
        return customerPassword;
    }

    @XmlElementWrapper(name = "orderList")
    @XmlElement(name = "order")
    public ArrayList<Order> getOrderList() {
        return orderList;
    }
    @XmlElement(name = "customerID")
    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }
    @XmlElement(name = "customerName")
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    @XmlElement(name = "customerLogin")
    public void setCustomerLogin(String customerLogin) {
        this.customerLogin = customerLogin;
    }

    @XmlTransient
    public void setCustomerPassword(String customerPassword) {
        this.customerPassword = customerPassword;
    }

    public void setOrderList(ArrayList<Order> orderList) {
        this.orderList = orderList;
    }

    public int getLength() {
        return orderList.size();
    }

    public void addOrder(Order newOrder) {
        this.orderList.add(newOrder);
    }

    public void deleteOrder(int index) {
        orderList.remove(index);
    }

    public Order getOrder(int order_id){
        for (int i = 0; i < this.getLength() ; i++) {
            if (this.orderList.get(i).getOrder_id() == order_id) {
                return this.orderList.get(i);
            }
        }
        return null;
    }

    public void createNewList(){
        this.orderList = new ArrayList<>();
    }
}