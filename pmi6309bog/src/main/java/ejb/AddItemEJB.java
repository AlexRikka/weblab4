package ejb;

import classes.DBConnector;
import classes.Item;

import javax.ejb.Stateless;
import java.sql.SQLException;

@Stateless
public class AddItemEJB {
    public void addItem(Integer itemOrderID, Item item) throws SQLException {
        DBConnector conn = new DBConnector();
        conn.addItemFromJSF(itemOrderID, item);
    }
}
