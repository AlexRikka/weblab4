package ejb;

import classes.Customer;
import classes.DBConnector;
import javax.ejb.Stateless;
import java.sql.SQLException;

@Stateless
public class CustomerEJB {
    public Customer validateUserLogin(String login, String password) throws SQLException {
        System.out.println(login);
        System.out.println(password);
        DBConnector conn = new DBConnector();
        Customer newCust = new Customer();
        newCust.setCustomerLogin(login);
        newCust.setCustomerPassword(password);
        //проверка
        if (conn.authentication(newCust)) {
            return newCust;
        }else {
            return null;
        }
    }
}
