package controlador;

import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;

public class Connect {
    //Atributos
    String driver="com.mysql.jdbc.Driver";
    String cadenaConnexio="jdbc:mysql://127.0.0.1/padel_java";
    String usr="root";
    String psswd="";
    public Connection con;
    
    
    public Connect(){
        //Constructor
        try{
            Class.forName(driver);
            con=DriverManager.getConnection(cadenaConnexio, usr, psswd);
            JOptionPane.showMessageDialog(null, "Se ha connectado con la BBDD exitosamente.");
        } catch(Exception e){
            JOptionPane.showMessageDialog(null, "No se ha podido establecer una connexion con la BBDD "+e.getMessage());
        }
    }
}
