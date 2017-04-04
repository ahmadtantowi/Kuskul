package Helper;

/**
 *
 * @author ahmad tantowi
 */
import com.mysql.jdbc.Driver;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class Koneksi {
    private static Connection koneksi;
    
    /**
     *
     * @return
     * @throws SQLException
     */
    public static Connection GetConnection() throws SQLException{        
        try{
            Driver driver = new Driver();
            
            koneksi = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_kuskul", "root", ""); 
        }
        catch(SQLException E){
            JOptionPane.showMessageDialog(null, "Koneksi Gagal Tersambung");
        }
        
        return koneksi;
    }
}
