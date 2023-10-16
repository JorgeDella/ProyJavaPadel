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
import vista.VisualizarP;
import vista.CreateUser;
import vista.CreatePista;
import vista.EditUser;
import vista.EditPista;
import vista.SignIn;

import modelo.Modelo;
import modelo.ModeloP;
 
 import controlador.Connect;

public class Control {
    public static SignIn sign = new SignIn();
    public static Visualizar viewUser = new Visualizar();
    public static VisualizarP viewPista = new VisualizarP();
    public static CreateUser user = new CreateUser();
    public static CreatePista pista = new CreatePista();
    public static EditUser editUser = new EditUser();
    public static EditPista editPista = new EditPista();
    
    public static Modelo model = new Modelo();
    public static ModeloP modelP = new ModeloP();
    
    public static Connect conexio = new Connect();
    
    //Conexio a la base de dades
    public static Statement conexion() {
        String driver = "com.mysql.jdbc.Driver";
        String cadenaConexion = "jdbc:mysql://127.0.0.1/padel_java";
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
    
    //#####################
    //  INICI DE SESSIO   #
    //#####################
    
    //Inicia la pestaña d'inici de sessio
    public static void inici() {
        sign.setTitle("Sign In");
        sign.setLocationRelativeTo(null);
        sign.setVisible(true); 
    }
    
    //Crea la consulta per a la BBDD si les credencials d'inici son correctes
    public static void iniciSessio() throws SQLException {
        if(sign.jCheckBoxAdmin.isSelected()) {
            String SQL = "SELECT * FROM admin WHERE dni = ? AND contrasenya = ? ;";
            consultaIniciSessio(SQL);
        }
        else {
            String SQL = "SELECT * FROM usuaris WHERE dni = ? AND contrasenya = ? ;";
            consultaIniciSessio(SQL);
        } 
    }
    
    //Consulta inici sessio
    public static void consultaIniciSessio(String SQL) {
        Statement estado = conexion();
        try(PreparedStatement prepareQuery = estado.getConnection().prepareStatement(SQL)) {
                prepareQuery.setString(1, sign.jTextFieldDNI.getText());
                char[] contrasenyaChar = sign.jPasswordFieldContrasenya.getPassword();
                String contrasenya = new String(contrasenyaChar);
                prepareQuery.setString(2, contrasenya);
                
                ResultSet result = prepareQuery.executeQuery();
                
                if(result.next()) {
                    pestanyaAdminUsuaris();
                    sign.setVisible(false); 
                }
                else {
                    JOptionPane.showMessageDialog(null, "No s'ha trobat cap coincidencia a la base de dades.");
                }
            }
            catch (SQLException e) {
                // Manejar la excepción (por ejemplo, mostrar un mensaje de error)
                e.printStackTrace();
            }
    }
    
    //##################
    //  USUARIS        #
    //##################
    
    //Pestanya d'administracio d'usuaris
    public static void pestanyaAdminUsuaris() {
        viewUser.setTitle("Administrador d'usuaris");
        viewUser.setLocationRelativeTo(null);
        viewUser.setVisible(true);
        viewPista.setVisible(false);
        try {
            listaUsuaris("SELECT * FROM usuaris");
        } catch(SQLException ex) {
            Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Administracio d'usuaris
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
        
        viewUser.setVisible(false);
    }
    
    //Creacio d'usuaris
    public static void crearUsuari() {
        try {
            insertTabla();
        } catch(SQLException ex) {
            Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
        }
        cancelarUsuari();
    }
    
    //Cancelacio de la creacio d'usuaris
    public static void cancelarUsuari() {
        pestanyaAdminUsuaris();
        user.setVisible(false);
        viewUser.jTextFieldBuscar.setText("");
    }
    
    //Per insertar dades a la taula
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
    
    //Buscador d'usuaris
    public static void buscarUsuaris() {
        String busqueda = viewUser.jTextFieldBuscar.getText();
        if(busqueda.length() == 0) {
            try {
                listaUsuaris("SELECT * FROM usuaris");
            } catch(SQLException ex) {
                Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else {
            String SQL = "SELECT * FROM usuaris WHERE dni LIKE '"+busqueda+"%';";
            try {
                listaUsuaris(SQL);
            } catch(SQLException ex) {
                Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
            }
        }    
    }
    
    //Llista d'usuaris
    public static void listaUsuaris(String SQL) throws SQLException {
        Statement estado = conexion();
        ResultSet result = estado.executeQuery(SQL);
        ArrayList<String> usuaris = new ArrayList<>();
        while (result.next()) {
            String dni = result.getString(1);
            String nombre = result.getString(2);
            String apellidos = result.getString(3);
            usuaris.add(dni + ": " + nombre + " " + apellidos);
        }
        if(usuaris.size() == 0) {
            model.dni = viewUser.jTextFieldBuscar.getText();
            nouUsuari();
            user.jTextFieldDNI.setText(model.dni);
        }
        viewUser.jListUsuaris.setModel(new javax.swing.AbstractListModel<String>() {

            public int getSize() {
                return usuaris.size();
            }

            public String getElementAt(int i) {
                return usuaris.get(i);
            }
        });
    }
    
    //Editar usuaris
    public static void editarUsuaris() {
        editUser.setTitle("Editar usuaris");
        editUser.setLocationRelativeTo(null);
        editUser.setVisible(true);
        viewUser.setVisible(false);
        String[] datos = viewUser.jListUsuaris.getSelectedValue().split(":\\s+|\\s+");
        model.dni = datos[0];
        model.nom = datos[1];
        model.cognoms = datos[2];
        String SQL = "SELECT * FROM usuaris WHERE dni='"+datos[0]+"';";
        try {
            iniciarCampsUsuaris(SQL);
        } catch(SQLException ex) {
            Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Inicialitzacio dels camps de editar perque les dades de l'usuari apareguin al formulari
    public static void iniciarCampsUsuaris(String SQL) throws SQLException {
        Statement estado = conexion();
        ResultSet result = estado.executeQuery(SQL);
        if (result.next()) {
            editUser.jTextFieldNom.setText(result.getString(2));
            editUser.jTextFieldCognoms.setText(result.getString(3));
            editUser.jTextFieldCorreu.setText(result.getString(4));
            editUser.jTextFieldTelefon.setText(result.getString(5));
            editUser.jPasswordFieldContrasenya.setText(result.getString(6));
        }
    }
    
    //Update de les dades que s'han editat
    public static void updateTablaUsuaris() throws SQLException {
        Statement estado = conexion();
        String SQL = "UPDATE usuaris SET nom=?, cognoms=?, correu=?, telefon=?, contrasenya=? WHERE dni='"+model.dni+"';";
        try(PreparedStatement prepareQuery = estado.getConnection().prepareStatement(SQL)){
            prepareQuery.setString(1, editUser.jTextFieldNom.getText());
            prepareQuery.setString(2, editUser.jTextFieldCognoms.getText());
            prepareQuery.setString(3, editUser.jTextFieldCorreu.getText());
            prepareQuery.setString(4, editUser.jTextFieldTelefon.getText());
            char[] contrasenyaChar = editUser.jPasswordFieldContrasenya.getPassword();
            String contrasenya = new String(contrasenyaChar);
            prepareQuery.setString(5, contrasenya);
            prepareQuery.executeUpdate();
        }
    }
    
    //Cancelar edició d'usuaris
    public static void cancelarEditarUsuaris() {
        pestanyaAdminUsuaris();
        editUser.setVisible(false);
    }
    
    //Activar o desactivar usuarios
    public static void activarDesactivarUsuaris() throws SQLException {
        Statement estado = conexion();
        String[] datos = viewUser.jListUsuaris.getSelectedValue().split(":\\s+|\\s+");
        
        String SQL = "UPDATE usuaris " +
             "SET actiu = CASE " +
             "    WHEN actiu = 1 THEN 0 " +
             "    ELSE 1 " +
             "END " +
             "WHERE dni = '"+datos[0]+"'";
        estado.executeUpdate(SQL);
    }
    
    //##################
    //  PISTES         #
    //##################
    
    //Pestanya d'administracio de pistes
    public static void pestanyaAdminPistes() {
        viewPista.setTitle("Administrador de pistes");
        viewPista.setLocationRelativeTo(null);
        viewPista.setVisible(true);
        viewUser.setVisible(false);
        try {
            listaPistes("SELECT * FROM pista");
        } catch(SQLException ex) {
            Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Administracio de pistes
    public static void novaPista() {
        pista.setTitle("Nova Pista");
        pista.setLocationRelativeTo(null);
        pista.setVisible(true);
        pista.jTextFieldEstat.setText("");
        pista.jCheckBoxDisponibilitat.setSelected(true);
        pista.jTextFieldUbicacio.setText("");
        pista.jTextFieldSol.setText("");
        pista.jTextFieldParets.setText("");
        
        viewPista.setVisible(false);
    }
    
    //Creacio de pistes
    public static void crearPista() {
        try {
            insertTablaPista();
        } catch(SQLException ex) {
            Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
        }
        cancelarPista();
    }
    
    //Cancelacio de la creacio de pistes
    public static void cancelarPista() {
        pestanyaAdminPistes();
        pista.setVisible(false);
    }
    
    //Funcio per insertar pistes
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
    
    //Buscador de psites
    public static void buscarPistes() {
        String busqueda = viewPista.jTextFieldBuscar.getText();
        if(busqueda.length() == 0) {
            try {
                listaPistes("SELECT * FROM pista");
            } catch(SQLException ex) {
                Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else {
            String SQL = "SELECT * FROM pista WHERE id_pista LIKE '"+busqueda+"%';";
            try {
                listaPistes(SQL);
            } catch(SQLException ex) {
                Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
            }
        }    
    }
    
    //Llista de pistes
    public static void listaPistes(String SQL) throws SQLException {
        Statement estado = conexion();
        ResultSet result = estado.executeQuery(SQL);
        ArrayList<String> pistes = new ArrayList<>();
        while (result.next()) {
            String id_pista = result.getString(1);
            String estat = result.getString(2);
            String disponibilitat = result.getString(3);
            pistes.add(id_pista + ": " + estat + " " + disponibilitat);
        }
        if(pistes.size() == 0) {
            novaPista();
            //Com el id_pista es autonumeric si no es troba al buscado no es passa la dada al crear
        }
        viewPista.jListPistes.setModel(new javax.swing.AbstractListModel<String>() {

            public int getSize() {
                return pistes.size();
            }

            public String getElementAt(int i) {
                return pistes.get(i);
            }
        });
    }
    
    //Editar pistes
    public static void editarPistes() {
        editPista.setTitle("Editar pistes");
        editPista.setLocationRelativeTo(null);
        editPista.setVisible(true);
        viewPista.setVisible(false);
        String[] datos = viewPista.jListPistes.getSelectedValue().split(":\\s+|\\s+");
        modelP.id_pista = Integer.parseInt(datos[0]);
        modelP.estat = datos[1];
        modelP.disponibilitat = Boolean.parseBoolean(datos[2]);
        String SQL = "SELECT * FROM pista WHERE id_pista="+datos[0]+";";
        try {
            iniciarCampsPistes(SQL);
        } catch(SQLException ex) {
            Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Inicialitzacio dels camps de editar perque les dades de la pista apareguin al formulari
    public static void iniciarCampsPistes(String SQL) throws SQLException {
        Statement estado = conexion();
        ResultSet result = estado.executeQuery(SQL);
        if (result.next()) {
            editPista.jTextFieldEstat.setText(result.getString(2));
            editPista.jCheckBoxDisponibilitat.setSelected(result.getBoolean(3));
            editPista.jTextFieldUbicacio.setText(result.getString(4));
            editPista.jTextFieldSol.setText(result.getString(5));
            editPista.jTextFieldParets.setText(result.getString(6));
        }
    }
    
    //Update de les dades que s'han editat
    public static void updateTablaPistes() throws SQLException {
        Statement estado = conexion();
        String SQL = "UPDATE pista SET estat=?, disponibilitat=?, ubicacio=?, sol=?, pared=? WHERE id_pista="+modelP.id_pista+";";
        try(PreparedStatement prepareQuery = estado.getConnection().prepareStatement(SQL)){
            prepareQuery.setString(1, editPista.jTextFieldEstat.getText());
            prepareQuery.setBoolean(2, editPista.jCheckBoxDisponibilitat.isSelected());
            prepareQuery.setString(3, editPista.jTextFieldUbicacio.getText());
            prepareQuery.setString(4, editPista.jTextFieldSol.getText());
            prepareQuery.setString(5, editPista.jTextFieldParets.getText());
            prepareQuery.executeUpdate();
        }
    }
    
    //Cancelar edició de pistes
    public static void cancelarEditarPistes() {
        pestanyaAdminPistes();
        editPista.setVisible(false);
    }
    
    //Activar o desactivar usuarios
    public static void activarDesactivarPistes() throws SQLException {
        Statement estado = conexion();
        String[] datos = viewPista.jListPistes.getSelectedValue().split(":\\s+|\\s+");
        int id = Integer.parseInt(datos[0]);
        
        String SQL = "UPDATE pista " +
             "SET disponibilitat = CASE " +
             "    WHEN disponibilitat = 1 THEN 0 " +
             "    ELSE 1 " +
             "END " +
             "WHERE id_pista="+id;
        estado.executeUpdate(SQL);
    }
    
}
