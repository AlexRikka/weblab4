package ejb;

import classes.Customer;
import classes.DBConnector;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;

@ManagedBean(name = "cust")
@SessionScoped
public class CustomerBean {
    @EJB
    CustomerEJB customerEJB;
    private Customer customer;
    private String login;
    private String password;
    private String validState;
    private String name;
    private String fieldColor;

    public CustomerBean(){
        this.customerEJB = new CustomerEJB();
        fieldColor = "#ecf0f1";
    }
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValidState() {
        return validState;
    }

    public void setValidState(String validState) {
        this.validState = validState;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFieldColor() {
        return fieldColor;
    }

    public void setFieldColor(String fieldColor) {
        this.fieldColor = fieldColor;
    }

    public String validateUserLogin() throws SQLException, IOException {
        if (login == null || password == null || login.equals("") || password.equals("")) {
            login = null;
            password =null;
            fieldColor = "background: #F7D3C5;";
            return validState = "Все поля обязательны для заполнения";
        }
        customer = customerEJB.validateUserLogin(login, password);
        if (customer == null){
            login = null;
            password =null;
            fieldColor = "background: #F7D3C5;";
            return validState = "Пользователь не найден";
        } else {
            DBConnector conn = new DBConnector();
            customer.setCustomer_id(conn.getCustomerIdFromDB(customer));
            customer.setCustomerName(conn.getCustomerName(customer));
            name = customer.getCustomerName();
            conn.customerOrders(customer); // пользователь получает id своих заказов
            for (int i = 0; i < customer.getLength(); i++) {
                conn.orderItems(customer.getOrderList().get(i)); // заказ получает свое содержимое
            }
            return "confirmed";
        }
    }

    public void downloadXML() {
        JAXBContext jaxbContext = null;
        FacesContext ctx = FacesContext.getCurrentInstance();
        HttpServletResponse resp = (HttpServletResponse) ctx.getExternalContext().getResponse();
        System.out.println("a");
        try {
            jaxbContext = JAXBContext.newInstance(Customer.class); // создаем объект JAXBContext - точку входа для JAXB
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller(); //создание объекта Marshaller, который выполняет сериализацию
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            //позволяет запросить, чтобы браузер попросил пользователя сохранить ответ на диск в файле с заданным именем
            resp.setHeader("Content-disposition","attachment; filename = result.xml");
            resp.setContentType("application/xml"); //устанавливает тип содержимого ответа, отправляемого клиенту
            StringWriter writer = new StringWriter(); //результат сериализации пишется в Writer(StringWriter)
            jaxbMarshaller.marshal(customer, writer); //сама сериализация
            PrintWriter respWriter = resp.getWriter();
            respWriter.println(writer.toString());
            writer.close();
            ctx.responseComplete();
        } catch (IOException | JAXBException e) {
            e.printStackTrace();

        }
    }

    public String logOut() {
        customer = null;
        return "logout";
    }
}
