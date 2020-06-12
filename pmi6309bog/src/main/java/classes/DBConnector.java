package classes;

import java.io.IOException;
import java.sql.*;

public class DBConnector {

    private String host = "jdbc:mysql://localhost:3306/online_store?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    private String root = "root";
    private String pass = "wa94zg2a";
    Connection connection;
    Statement statement;
    PreparedStatement preparedStmt;
    private String qInCustomers = "INSERT INTO customers VALUES(?, ?, ?, ?)";
    private String qInOrderitems = "INSERT INTO orderitems VALUES(?, ?, ?, ?)";
    private String qInOrders = "INSERT INTO orders VALUES(?, ?)";
    private String qInItems = "INSERT INTO items VALUES(?, ?)";

    private String qSel_id_orders = "SELECT order_id FROM orders WHERE customer_id=?";
    private String qSel_all_orderitems = "SELECT * FROM orderitems WHERE order_id=?";
    private String qSel_price_items = "SELECT price FROM items WHERE item_name=?";
    private String qSel_count_orders = "SELECT COUNT(order_id) FROM orders WHERE customer_id=?";
    private String qSel_itname_items = "SELECT item_name FROM items";

    private String qSel_name_customers = "SELECT name FROM customers where login=? and password=?";
    private String qSel_id_customers = "SELECT customer_id FROM customers where login=? and password=?";

    private String q_last_order = "SELECT * FROM orders ORDER BY order_id ASC";
    private String q_last_customer = "SELECT * FROM customers ORDER BY customer_id ASC";


    public DBConnector() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            this.connection = DriverManager.getConnection(host, root, pass);
            //this.statement = connection.createStatement();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // устанавливает верный order_id, use only right before sort
    public void lastOrderId(Order ord) throws SQLException {
        this.preparedStmt = connection.prepareStatement(q_last_order);
        ResultSet rs = preparedStmt.executeQuery();
        rs.last();
        ord.setOrder_id(rs.getInt("order_id") + 1);
    }

    // get last customer_id
    private int lastCustomerId() throws SQLException {
        this.preparedStmt = connection.prepareStatement(q_last_customer);
        ResultSet rs = preparedStmt.executeQuery();
        rs.last();
        return rs.getInt("customer_id");
    }

    // добавлениe нового пользователя в customers, установка верного id
    public void addUser(Customer cust) throws SQLException {
        cust.setCustomer_id(lastCustomerId() + 1);
        this.preparedStmt = connection.prepareStatement(qInCustomers);
        preparedStmt.setInt(1, cust.getCustomer_id());
        preparedStmt.setString(2, cust.getCustomerName());
        preparedStmt.setString(3, cust.getCustomerLogin());
        preparedStmt.setString(4, cust.getCustomerPassword());
        preparedStmt.execute();
    }

    // добавлние нового пользователя с заказом в orders, то есть соединение id заказа и пользователя
    public void addToOrders(int custID, Order ord) throws SQLException {
        this.preparedStmt = connection.prepareStatement(qInOrders);
        preparedStmt.setInt(1, ord.getOrder_id());
        preparedStmt.setInt(2, custID);
        preparedStmt.execute();
    }

    // добавление нового списка в orderitems, use only after sorting
    public void addOrder(Order ord) throws SQLException {
        for (int i = 0; i < ord.getLength(); i++) {
            this.preparedStmt = connection.prepareStatement(qInOrderitems);
            preparedStmt.setInt(1, ord.getOrder_id());
            preparedStmt.setString(2, ord.getItemFromOrder(i).getName());
            preparedStmt.setInt(3, ord.getItemFromOrder(i).getAmount());
            preparedStmt.setDouble(4, ord.getItemFromOrder(i).getCost());
            preparedStmt.execute();
        }
        addItem(ord);
    }

    //добавлние новых товаров из заказа товара в items
    public void addItem(Order ord) throws SQLException {
        for (int i = 0; i < ord.getLength(); i++) {
            if (isItemNameNew(ord.getItemFromOrder(i).getName())) {
                this.preparedStmt = connection.prepareStatement(qInItems);
                preparedStmt.setString(1, ord.getItemFromOrder(i).getName());
                preparedStmt.setDouble(2, ord.getItemFromOrder(i).getPrice());
                preparedStmt.execute();
            }
        }
    }

    // проверяет, есть ли уже такое наименование товара в items
    public boolean isItemNameNew(String itemName) throws SQLException {
        this.preparedStmt = connection.prepareStatement(qSel_itname_items);
        ResultSet rs = preparedStmt.executeQuery();
        while (rs.next()) {
            if (rs.getNString("item_name").equals(itemName.intern())) {
                return false;
            }
        }
        return true;
    }

    // добавление элемента в бд из интернет-магазина
    public void addItemFromJSF(Integer order_id, Item item) throws SQLException {
        this.preparedStmt = connection.prepareStatement(qInItems);
        preparedStmt.setString(1, item.getName());
        preparedStmt.setDouble(2, item.getPrice());
        preparedStmt.execute();

        this.preparedStmt = connection.prepareStatement(qInOrderitems);
        preparedStmt.setInt(1, order_id);
        preparedStmt.setString(2, item.getName());
        preparedStmt.setInt(3, item.getAmount());
        preparedStmt.setDouble(4, item.getCost());
        preparedStmt.execute();
    }

    //проверки существование пользователя с заданными логином и паролем
    public boolean authentication(Customer cust) throws SQLException {
        this.preparedStmt = connection.prepareStatement(qSel_id_customers);
        preparedStmt.setString(1, cust.getCustomerLogin());
        preparedStmt.setString(2, cust.getCustomerPassword());
        ResultSet rs = preparedStmt.executeQuery();
        return rs.first(); // return false if there are no rows in the result set
    }

    //выбор id всех списков, соответствующих заданному пользователю
    public void customerOrders(Customer cust) throws SQLException {
        this.preparedStmt = connection.prepareStatement(qSel_id_orders);
        preparedStmt.setInt(1, cust.getCustomer_id());
        ResultSet rs = preparedStmt.executeQuery();
        while (rs.next()) {
            Order tmp = new Order();
            tmp.setOrder_id(rs.getInt("order_id"));
            cust.addOrder(tmp);
        }
    }

    //выбор всех элементов заданного списка
    public void orderItems(Order ord) throws SQLException, IOException {
        this.preparedStmt = connection.prepareStatement(qSel_all_orderitems);
        preparedStmt.setInt(1, ord.getOrder_id());
        ResultSet rs = preparedStmt.executeQuery();
        while (rs.next()) {
            String sname = rs.getNString("item_name");
            Double dprice = getItemPrice(sname);
            Item tmp = new Item(sname, dprice, rs.getInt("quantity"));
            ord.addItem(tmp);
        }
    }

    // получение стоимотси товара по имени
    public Double getItemPrice(String itemname) throws SQLException {
        this.preparedStmt = connection.prepareStatement(qSel_price_items);
        preparedStmt.setString(1, itemname);
        ResultSet rs = preparedStmt.executeQuery();
        rs.first();
        return rs.getDouble("price");
    }

    // получение имени пользователя по login и password
    public String getCustomerName(Customer cust) throws SQLException {
        this.preparedStmt = connection.prepareStatement(qSel_name_customers);
        preparedStmt.setString(1, cust.getCustomerLogin());
        preparedStmt.setString(2, cust.getCustomerPassword());
        ResultSet rs = preparedStmt.executeQuery();
        rs.first();
        return rs.getNString("name");
    }

    // получение id пользователя по login и password
    public int getCustomerIdFromDB(Customer cust) throws SQLException {
        this.preparedStmt = connection.prepareStatement(qSel_id_customers);
        preparedStmt.setString(1, cust.getCustomerLogin());
        preparedStmt.setString(2, cust.getCustomerPassword());
        ResultSet rs = preparedStmt.executeQuery();
        rs.first();
        return rs.getInt("customer_id");
    }

    // получение количества заказов пользователя
    public int countOrders(Customer cust) throws SQLException {
        this.preparedStmt = connection.prepareStatement(qSel_count_orders);
        preparedStmt.setInt(1, cust.getCustomer_id());
        ResultSet rs = preparedStmt.executeQuery();
        rs.first();
        return rs.getInt("COUNT(order_id)");
    }

}
