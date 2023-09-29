 package controlador;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

import vista.Visualizar;
import vista.CreateUser;
import vista.CreatePista;

import modelo.Modelo;

public class Control {
    public static Visualizar view = new Visualizar();
    public static CreateUser user = new CreateUser();
    public static CreatePista pista = new CreatePista();
    
    public static Modelo model = new Modelo();
    
    public static Statement conexion() {
        String driver = "com.mysql.jdbc.Driver";
        String cadenaConexion = "jdbc:mysql://localhost/padel_java";
        String usuario = "root";
        String contraseña = "";
        Connection conexion;
        Statement statement = null;
        try {
            Class.forName(driver);
            conexion = DriverManager.getConnection(cadenaConexion, usuario, contraseña);
            if (conexion != null) {
                statement = conexion.createStatement();
            }
            else {
                System.out.println("Error");
            }    
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "No se ha podido establecer una conexion con la BD" + e.getMessage());
        }
        return statement;
    }
    
    public static void pestanyaAdmin() {
        view.setTitle("Administrador");
        view.setLocationRelativeTo(null);
        view.setVisible(true); 
    }
    
    public static void nouUsuari() {
        user.setTitle("Nou Usuari");
        user.setLocationRelativeTo(null);
        user.setVisible(true);
        user.jTextFieldDNI.setText("");
        user.jTextFieldNom.setText("");
        user.jTextFieldCognoms.setText("");
        user.jTextFieldCorreu.setText("");
        user.jTextFieldTelefon.setText("");
        user.jPasswordFieldContrasenya.setText("");
        
        view.setVisible(false);
        crearUsuari();
    }
    
    public static void crearUsuari() {
        try {
            insertTabla();
        } catch(SQLException ex) {
            Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
        }
        cancelarUsuari();
    }
    
    public static void insertTabla() throws SQLException {
        Statement estado = conexion();
        String SQL = "INSERT INTO usuaris(dni, nom, cognoms, correu, telefon, contrasenya) VALUES( ?, ?, ?, ?, ?, ?)";
        try(PreparedStatement prepareQuery = estado.getConnection().prepareStatement(SQL)){
            prepareQuery.setString(1, user.jTextFieldDNI.getText());
            prepareQuery.setString(2, user.jTextFieldNom.getText());
            prepareQuery.setString(3, user.jTextFieldCognoms.getText());
            prepareQuery.setString(4, user.jTextFieldCorreu.getText());
            prepareQuery.setString(5, user.jTextFieldTelefon.getText());
            char[] contrasenyaChar = user.jPasswordFieldContrasenya.getPassword();
            String contrasenya = new String(contrasenyaChar);
            prepareQuery.setString(6, contrasenya);
            prepareQuery.executeUpdate();
        }
    }
    
    public static void cancelarUsuari() {
        pestanyaAdmin();
        user.setVisible(false);
    }
    
    public static void novaPista() {
        pista.setTitle("Nova Pista");
        pista.setLocationRelativeTo(null);
        pista.setVisible(true);
        pista.jTextFieldEstat.setText("");
        pista.jCheckBoxDisponibilitat.setSelected(true);
        pista.jTextFieldUbicacio.setText("");
        pista.jTextFieldSol.setText("");
        pista.jTextFieldParets.setText("");
        
        view.setVisible(false);
        crearPista();
    }
    
    public static void crearPista() {
        try {
            insertTablaPista();
        } catch(SQLException ex) {
            Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
        }
        cancelarPista();
    }
    
    public static void insertTablaPista() throws SQLException {
        Statement estado = conexion();
        String SQL = "INSERT INTO pista(estat, disponibilitat, ubicacio, sol, pared) VALUES( ?, ?, ?, ?, ?)";
        try(PreparedStatement prepareQuery = estado.getConnection().prepareStatement(SQL)){
            prepareQuery.setString(1, pista.jTextFieldEstat.getText());
            prepareQuery.setBoolean(2, pista.jCheckBoxDisponibilitat.isSelected());
            prepareQuery.setString(3, pista.jTextFieldUbicacio.getText());
            prepareQuery.setString(4, pista.jTextFieldSol.getText());
            prepareQuery.setString(5, pista.jTextFieldParets.getText());
            prepareQuery.executeUpdate();
        }
    }
    
    public static void cancelarPista() {
        pestanyaAdmin();
        pista.setVisible(false);
    }
    
    public static void buscarUsuaris() {
        String busqueda = view.jTextFieldBuscar.getText();
        if(busqueda.length() == 0) {
            try {
                listaUsuaris("SELECT * FROM usuaris");
            } catch(SQLException ex) {
                Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else {
            String SQL = "SELECT * FROM usuaris WHERE nombre LIKE '"+busqueda+"%';";
            try {
                listaUsuaris(SQL);
            } catch(SQLException ex) {
                Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
            }
        }    
    }
    
    public static void listaUsuaris(String SQL) throws SQLException {
        Statement estado = conexion();
        ResultSet result = estado.executeQuery(SQL);
        ArrayList<String> personas = new ArrayList<>();
        while (result.next()) {
            String nombre = result.getString(1);
            String apellidos = result.getString(2);
            personas.add(nombre + " " + apellidos);
        }
        if(personas.size() == 0) {
            model.nombre = view.jTextFieldBuscar.getText();
            añadir();
            añadir.jTextFieldNombre.setText(model.nombre);
        }
        view.jListContactos.setModel(new javax.swing.AbstractListModel<String>() {

            public int getSize() {
                return personas.size();
            }

            public String getElementAt(int i) {
                return personas.get(i);
            }
        });
    }
}
