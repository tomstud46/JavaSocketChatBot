package chatapp;

public class DBConnectionTest {
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL Driver Loaded Successfully ✅");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL Driver NOT Found ❌");
            e.printStackTrace();
        }
    }
}
