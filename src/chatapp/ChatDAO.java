package chatapp;

import java.sql.*;

public class ChatDAO {

    public ChatDAO() {
        try {
            Statement st = DBConnection.getConnection().createStatement();
            st.execute("""
                CREATE TABLE IF NOT EXISTS messages (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    sender VARCHAR(50),
                    message TEXT,
                    time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveMessage(Message msg) {
        try {
            PreparedStatement ps = DBConnection.getConnection()
                    .prepareStatement("INSERT INTO messages(sender,message) VALUES(?,?)");
            ps.setString(1, msg.sender);
            ps.setString(2, msg.text);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


