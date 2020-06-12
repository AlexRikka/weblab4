package ejb;

import classes.Customer;
import classes.DBConnector;
import classes.Item;
import classes.Order;

import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.persistence.criteria.CriteriaBuilder;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;

@ManagedBean(name="insert")
@SessionScoped
public class AddItemBean{
    @EJB
    AddItemEJB addItemEJB;
    Item item;
    Customer customer;
    Integer itemOrderID;
    String itemName;
    Integer itemQty;
    Double itemPrice;

    public AddItemBean(){
        addItemEJB = new AddItemEJB();
    }

    public Double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(Double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public Integer getItemOrderID() {
        return itemOrderID;
    }

    public void setItemOrderID(Integer itemOrderID) {
        this.itemOrderID = itemOrderID;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getItemQty() {
        return itemQty;
    }

    public void setItemQty(Integer itemQty) {
        this.itemQty = itemQty;
    }

    public String insertPage(Integer order_id, Customer cust) throws IOException {
        this.itemOrderID = order_id;
        this.customer = cust;
        System.out.println("page"+itemOrderID);
        System.out.println("page"+customer);
        //FacesContext.getCurrentInstance().getExternalContext().redirect("insert.xhtml");
        return "insert";
    }

    public String addItem() throws IOException, SQLException {
        System.out.println("add" + itemName);
        System.out.println("add" + itemPrice);
        System.out.println("add" + itemOrderID);
        this.item = new Item(itemName,itemPrice,itemQty);
        addItemEJB.addItem(this.itemOrderID, this.item);
        DBConnector conn = new DBConnector();
        Customer newCust = new Customer();
        newCust.setCustomer_id(conn.getCustomerIdFromDB(this.customer));
        newCust.setCustomerName(conn.getCustomerName(this.customer));
        System.out.println(newCust.getCustomer_id());
        conn.customerOrders(newCust); // пользователь получает id своих заказов
        for (int i = 0; i < newCust.getLength(); i++) {
            conn.orderItems(newCust.getOrderList().get(i)); // заказ получает свое содержимое
        }
        this.customer.setOrderList(null);
        this.customer.createNewList();
        conn.customerOrders(customer); // пользователь получает id своих заказов
        for (int i = 0; i < customer.getLength(); i++) {
            conn.orderItems(customer.getOrderList().get(i)); // заказ получает свое содержимое
        }
        return "update";
    }

    public void itemNameValidator(FacesContext facesContext, UIComponent uiComponent, Object o) throws SQLException {
           String toValidate = o.toString();
            if (toValidate.equals("")) {
                FacesMessage facesMessage = new FacesMessage("Имя должно быть непустым");
                throw new ValidatorException(facesMessage);
            }
            DBConnector conn = new DBConnector();
            if (!conn.isItemNameNew(toValidate)) {
                FacesMessage facesMessage = new FacesMessage("Такой товар уже существует");
                throw new ValidatorException(facesMessage);
            }
    }

    public void itemQtyValidator(FacesContext facesContext, UIComponent uiComponent, Object o){
        int toValidate;
        toValidate = Integer.parseInt(o.toString());
        if (toValidate < 1 || toValidate > 99) {
            FacesMessage facesMessage = new FacesMessage("Количество может быть от 1 до 99");
            throw new ValidatorException(facesMessage);
        }
    }

    public void priceValidator(FacesContext facesContext, UIComponent uiComponent, Object o) {
        double toValidate = Double.parseDouble(o.toString());
            if (toValidate < 0) {
                FacesMessage facesMessage = new FacesMessage("Цена должна быть неотрицательной");
                throw new ValidatorException(facesMessage);
            }
    }


}
