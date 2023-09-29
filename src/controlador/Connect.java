package controlador;

public class Connect {
        public static Connect Connection(){
        //Atributos
            String driver="com.mysql.jdbc.Driver";
            String cadenaConnexio="jdbc:mysql://127.0.0.1/padel_java";
            String usr="root";
            String psswd="";
            Connect con = null;
            
        //Constructor
        try{
            Class.forName(driver);
            con=DriverManager.getConnection(cadenaConnexio, usr, psswd);
            //JOptionPane.showMessageDialog(null, "Se ha connectado con la BBDD exitosamente.");
        } catch(Exception e){
            JOptionPane.showMessageDialog(null, "No se ha podido establecer una connexion con la BBDD "+e.getMessage());
        }
        return con;
    }
}
