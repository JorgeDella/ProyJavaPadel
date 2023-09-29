package controlador;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import vista.Visualizar;
import vista.CreateUser;
import vista.CreatePista;

import modelo.Modelo;

public class Control {
    public static Visualizar view = new Visualizar();
    public static CreateUser user = new CreateUser();
    public static CreatePista pista = new CreatePista();
    
    public static Modelo model = new Modelo();
    
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
        //user.jTextFieldContrasenya.setText("");
        
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
}
