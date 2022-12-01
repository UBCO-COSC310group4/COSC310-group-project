import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.time.*;

public class DBConnection{
    private Connection con;
    public DBConnection(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/cosc310database","root","password");
        }catch(Exception e){
            System.out.println(e);
        }
    }

    public boolean checkLogin(String user, String pword, Boolean manager){
        try{
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT password, manager FROM Staff WHERE username = \"" + user + "\";");
            if(!rs.next()) {
                rs.close();
                stmt.close();
                throw new IllegalArgumentException("invalid username");
            }
            if(!rs.getString("password").equals(pword)) {
                rs.close();
                stmt.close();
                throw new IllegalArgumentException("invalid password");
            }
            if(manager && !rs.getBoolean("manager")){
                rs.close();
                stmt.close();
                throw new IllegalArgumentException("Not a manager");
            }
            rs.close();
            stmt.close();
            return true;
        }catch(IllegalArgumentException e){
            throw e;
        }catch(Exception e){
            System.out.println(e);
            return false;
        }
    }

    public String[] getAllEmployees(){
        try{
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT username FROM Staff");
            ArrayList<String> usernames = new ArrayList<String>();
            while(rs.next()){
                usernames.add(rs.getString(1));
            }
            String[] usernamesArr = new String[usernames.size()];
            int i = 0;
            for(String id : usernames){
                usernamesArr[i++] = id;
            }
            rs.close();
            stmt.close();
            return usernamesArr;
        }catch(Exception e){
            System.out.println(e);
            return new String[]{};
        }
    }

    public String getEmployeeNameByUsername(String username){
        try{
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT first_name, last_name FROM Staff WHERE username = \"" + username + "\";");
            if(!rs.next()) {
                rs.close();
                stmt.close();
                return "";
            }
            String out = rs.getString("first_name") + " " + rs.getString("last_name");
            rs.close();
            stmt.close();
            return out;
        }catch(Exception e){
            System.out.println(e);
            return "";
        }
    }

    public boolean isEmployeeManagerByUsername(String username){
        try{
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT manager FROM Staff WHERE username = \"" + username + "\";");
            if(!rs.next()) {
                rs.close();
                stmt.close();
                return false;
            }
            boolean out = rs.getBoolean("manager");
            rs.close();
            stmt.close();
            return out;
        }catch(Exception e){
            System.out.println(e);
            return false;
        }
    }

    public void addEmployee(String firstName, String lastName, String userName, String password, boolean manager){
        try {
            Statement stmt = con.createStatement();
            stmt.executeUpdate("INSERT INTO Staff VALUES (\"" + firstName + "\", \"" + lastName + "\", \"" + userName + "\", \"" + password + "\", " + manager + ");");
        }catch(Exception e){
            System.out.println(e);
        }
    }

    public boolean checkProductExists(int id){
        try{
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT * FROM Product WHERE id = " + id + ";");
            if(!rs.next()) {
                rs.close();
                stmt.close();
                return false;
            }
            rs.close();
            stmt.close();
            return true;
        }catch(Exception e){
            System.out.println(e);
            return false;
        }
    }

    public int getProductStock(int id){
        try{
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT stock FROM Product WHERE id = " + id + ";");
            if(!rs.next()) {
                rs.close();
                stmt.close();
                return 0;
            }
            int out = rs.getInt("stock");
            rs.close();
            stmt.close();
            return out;
        }catch(Exception e){
            System.out.println(e);
            return 0;
        }
    }

    public double getProductPrice(int id){
        try{
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT price FROM Product WHERE id = " + id + ";");
            if(!rs.next()) {
                rs.close();
                stmt.close();
                return 0;
            }
            double out = rs.getDouble("price");
            rs.close();
            stmt.close();
            return out;
        }catch(Exception e){
            System.out.println(e);
            return 0;
        }
    }

    public String getProductName(int id){
        try{
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT product_name FROM Product WHERE id = " + id + ";");
            if(!rs.next()) {
                rs.close();
                stmt.close();
                return null;
            }
            String out = rs.getString("product_name");
            rs.close();
            stmt.close();
            return out;
        }catch(Exception e){
            System.out.println(e);
            return null;
        }
    }

    public String getProductSupplier(int id){
        try{
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT supplier_name FROM Product AS P, Supplier AS S WHERE P.supplier_id = S.id AND P.id = " + id + ";");
            if(!rs.next()) {
                rs.close();
                stmt.close();
                return null;
            }
            String out = rs.getString("supplier_name");
            rs.close();
            stmt.close();
            return out;
        }catch(Exception e){
            System.out.println(e);
            return null;
        }
    }

    public int[] getAllProductIds(){
        try{
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT id FROM Product");
            ArrayList<Integer> ids = new ArrayList<Integer>();
            while(rs.next()){
                ids.add(rs.getInt(1));
            }
            int[] idsArr = new int[ids.size()];
            int i = 0;
            for(int id : ids){
                idsArr[i++] = id;
            }
            rs.close();
            stmt.close();
            return idsArr;
        }catch(Exception e){
            System.out.println(e);
            return new int[]{};
        }
    }

    public int[] getLowStockProductIds(){
        try{
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT id FROM Product WHERE stock <= 3");
            ArrayList<Integer> ids = new ArrayList<Integer>();
            while(rs.next()){
                ids.add(rs.getInt(1));
            }
            int[] idsArr = new int[ids.size()];
            int i = 0;
            for(int id : ids){
                idsArr[i++] = id;
            }
            rs.close();
            stmt.close();
            return idsArr;
        }catch(Exception e){
            System.out.println(e);
            return new int[]{};
        }
    }

    public void changeProductStock(int id, int amount){
        try{
            Statement stmt=con.createStatement();
            stmt.executeUpdate("UPDATE Product SET stock = stock + "+ amount + " WHERE id = " + id + ";");
            stmt.close();
        }catch(Exception e){
            System.out.println(e);
        }
    }

    public void setProductStock(int id, int stock){
        try{
            Statement stmt=con.createStatement();
            stmt.executeUpdate("UPDATE Product SET stock = "+ stock + " WHERE id = " + id + ";");
            stmt.close();
        }catch(Exception e){
            System.out.println(e);
        }
    }

    public void setProductPrice(int id, double price){
        try{
            Statement stmt=con.createStatement();
            stmt.executeUpdate("UPDATE Product SET stock = "+ price + " WHERE id = " + id + ";");
            stmt.close();
        }catch(Exception e){
            System.out.println(e);
        }
    }

    public void addNewProduct(String name, double price, int supplier_id, int stock){
        try {
            Statement stmt = con.createStatement();
            stmt.executeUpdate("INSERT INTO Product VALUES (Null, \"" + name + "\", " + price + ", " + supplier_id + ", " + stock + ");");
        }catch(Exception e){
            System.out.println(e);
        }
    }

    public int recordSale(ArrayList<Integer> productIds, int custId)
    {
        try{
            Statement stmt=con.createStatement();
            stmt.executeUpdate("INSERT INTO Purchase VALUES (Null, CURDATE(), " + custId + ");");
            ResultSet rs=stmt.executeQuery("SELECT LAST_INSERT_ID();");
            rs.next();
            int purchaseId = rs.getInt(1);
            while(productIds.size() > 0){
                int productId = productIds.get(0);
                int amount = Collections.frequency(productIds, productId);
                productIds.removeAll(Collections.singletonList(productId));
                stmt.executeUpdate("INSERT INTO PurchasedProduct VALUES (" + purchaseId + ", " + productId + ", " + amount + ");");
            }
            rs.close();
            stmt.close();
            return purchaseId;
        }catch(Exception e){
            System.out.println(e);
            return -999;
        }
    }

    public double getThisMonthsSales(){
        try{
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT SUM(amount * price) FROM Purchase as Pu, PurchasedProduct as PP, Product as Pr WHERE Pu.id = PP.purchase_id AND PP.product_id = Pr.id AND YEAR(Pu.purchase_date) = YEAR(CURDATE());");
            rs.next();
            double out = rs.getDouble(1);
            rs.close();
            stmt.close();
            return out;
        }catch(Exception e){
            System.out.println(e);
            return 0;
        }
    }

    public double getSalesByDate(LocalDate date){
        try{
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT SUM(amount * price) FROM Purchase as Pu, PurchasedProduct as PP, Product as Pr WHERE Pu.id = PP.purchase_id AND PP.product_id = Pr.id AND purchase_date = '"+ Date.valueOf(date) +"';");
            rs.next();
            double sales = rs.getDouble(1);
            rs.close();
            stmt.close();
            return sales;
        }catch(Exception e){
            System.out.println(e);
            return 0;
        }

    }

    public int[] getAllSaleIds(){
        try{
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT id FROM Purchase");
            ArrayList<Integer> ids = new ArrayList<Integer>();
            while(rs.next()){
                ids.add(rs.getInt(1));
            }
            int[] idsArr = new int[ids.size()];
            int i = 0;
            for(int id : ids){
                idsArr[i++] = id;
            }
            rs.close();
            stmt.close();
            return idsArr;
        }catch(Exception e){
            System.out.println(e);
            return new int[]{};
        }
    }

    public String getSaleInfoById(int id){
        try{
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT purchase_date, SUM(amount * price)\n" +
                    "FROM Purchase as Pu, PurchasedProduct as PP, Product as Pr\n" +
                    "WHERE Pu.id = PP.purchase_id AND PP.product_id = Pr.id AND Pu.id = " + id + "\n" +
                    "GROUP BY Pu.id\n" +
                    "ORDER BY purchase_date DESC;");
            rs.next();
            String out = rs.getString(1) + "   $" + rs.getString(2);
            rs.close();
            stmt.close();
            return out;
        }catch(Exception e){
            System.out.println(e);
            return "";
        }
    }

    public String getSaleItemsById(int id){
        try{
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT product_name, amount, (price * amount)\n" +
                    "FROM PurchasedProduct AS PP, Product AS P\n" +
                    "WHERE PP.product_id = P.id AND purchase_id = " + id + ";");
            String out = "";
            while(rs.next()){
                out += out = rs.getString(1) + " x" + rs.getString(2) + "  $" + rs.getString(3) + "\n";
            }
            rs.close();
            stmt.close();
            return out;
        }catch(Exception e){
            System.out.println(e);
            return "";
        }
    }

    public int[] getAllSuppliers(){
        try{
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT id FROM Supplier");
            ArrayList<Integer> ids = new ArrayList<Integer>();
            while(rs.next()){
                ids.add(rs.getInt(1));
            }
            int[] idsArr = new int[ids.size()];
            int i = 0;
            for(int id : ids){
                idsArr[i++] = id;
            }
            rs.close();
            stmt.close();
            return idsArr;
        }catch(Exception e){
            System.out.println(e);
            return new int[]{};
        }
    }

    public String getSupplierNameById(int id){
        try{
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT supplier_name FROM Supplier WHERE id = " + id + ";");
            if(!rs.next()) {
                rs.close();
                stmt.close();
                return null;
            }
            String out = rs.getString("supplier_name");
            rs.close();
            stmt.close();
            return out;
        }catch(Exception e){
            System.out.println(e);
            return null;
        }
    }

    public void addNewSupplier(String name){
        try {
            Statement stmt = con.createStatement();
            stmt.executeUpdate("INSERT INTO Supplier VALUES (Null, \"" + name +"\");");
        }catch(Exception e){
            System.out.println(e);
        }
    }

    public int addNewCustomer(String firstName, String lastName, String phoneNumber, String email){
        try {
            Statement stmt = con.createStatement();
            stmt.executeUpdate("INSERT INTO Customer VALUES (Null, \"" + firstName +"\", \"" + lastName + "\", \"" + email + "\", \"" + phoneNumber + "\");");
            ResultSet rs=stmt.executeQuery("SELECT LAST_INSERT_ID();");
            rs.next();
            int customerId = rs.getInt(1);
            return customerId;
        }catch(Exception e){
            System.out.println(e);
        }
        return -999;
    }

    public String getCustomerNameById(int id){
        try{
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT first_name, last_name FROM Customer WHERE id = " + id + ";");
            if(!rs.next()) {
                rs.close();
                stmt.close();
                return null;
            }
            String out = rs.getString("first_name") + " " + rs.getString("last_name");
            rs.close();
            stmt.close();
            return out;
        }catch(Exception e){
            System.out.println(e);
            return null;
        }
    }

    public String getCustomerEmailById(int id){
        try{
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT email FROM Customer WHERE id = " + id + ";");
            if(!rs.next()) {
                rs.close();
                stmt.close();
                return null;
            }
            String out = rs.getString("email");
            rs.close();
            stmt.close();
            return out;
        }catch(Exception e){
            System.out.println(e);
            return null;
        }
    }

    public void addOrder(int[] productIds, int[] productAmounts, int supplierId)
    {
        try{
            Statement stmt=con.createStatement();
            stmt.executeUpdate("INSERT INTO ProductOrder VALUES (Null, CURDATE(), " + supplierId + ");");
            ResultSet rs=stmt.executeQuery("SELECT LAST_INSERT_ID();");
            rs.next();
            int orderId = rs.getInt(1);
            for(int i = 0; i < productIds.length; i++){
                int amount = productAmounts[i];
                if(amount > 0) {
                    int productId = productIds[i];
                    stmt.executeUpdate("INSERT INTO OrderedProduct VALUES (" + orderId + ", " + productId + ", " + amount + ");");
                }
            }
            rs.close();
            stmt.close();
        }catch(Exception e){
            System.out.println(e);
        }
    }

    public int[] getAllOrderIds(){
        try{
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT id FROM ProductOrder");
            ArrayList<Integer> ids = new ArrayList<Integer>();
            while(rs.next()){
                ids.add(rs.getInt(1));
            }
            int[] idsArr = new int[ids.size()];
            int i = 0;
            for(int id : ids){
                idsArr[i++] = id;
            }
            rs.close();
            stmt.close();
            return idsArr;
        }catch(Exception e){
            System.out.println(e);
            return new int[]{};
        }
    }

    public String getOrderInfoById(int id) {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT order_date, SUM(amount * price)\n" +
                    "FROM ProductOrder as PO, OrderedProduct as OP, Product as Pr\n" +
                    "WHERE PO.id = OP.order_id AND OP.product_id = Pr.id AND PO.id = " + id + "\n" +
                    "GROUP BY PO.id\n" +
                    "ORDER BY order_date DESC;");
            rs.next();
            String out = rs.getString(1) + "   $" + rs.getString(2);
            rs.close();
            stmt.close();
            return out;
        } catch (Exception e) {
            System.out.println(e);
            return "";
        }
    }

    public String getOrderItemsById(int id){
        try{
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT product_name, amount, (price * amount)\n" +
                    "FROM OrderedProduct AS OP, Product AS P\n" +
                    "WHERE OP.product_id = P.id AND order_id = " + id + ";");
            String out = "";
            while(rs.next()){
                out += out = rs.getString(1) + " x" + rs.getString(2) + "  $" + rs.getString(3) + "\n";
            }
            rs.close();
            stmt.close();
            return out;
        }catch(Exception e){
            System.out.println(e);
            return "";
        }
    }

    public void close(){
        try{
            con.close();
        }catch(Exception e) {}
    }
}
